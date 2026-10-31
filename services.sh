#!/usr/bin/env bash
# services.sh - Build, start and stop the Onshore stack locally.
#
#   -l | --list    List services (name, folder, port, kind)
#   -b | --build   Build services (mvn clean install / pnpm build); compiles common-lib first
#   -up            Compile common-lib, start infra (docker) + Spring Cloud services + frontend
#   -s | --stop    Stop services by port and shut down docker infra

set -euo pipefail

VERBOSE=false
DO_BUILD=false
DO_LIST=false
DO_UP=false
DO_STOP=false
DO_PRUNE=false
DO_NO_INFRA=false
ONLY_SERVICES=""
COLOR_MODE="always" # auto|always|never
SPRING_PROFILE="" # optional Spring Boot profile (empty = default)

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MS_DIR="microservices"
COMPOSE_APP="$ROOT_DIR/docker-compose.yml"
COMPOSE_DEV="$ROOT_DIR/docker-compose.dev.yml"
# Infra containers (docker-compose.yml) needed by local services
DB_CONTAINERS=(userdb bookingdb paymentdb partnerdb planningdb creditdb payoutdb admindb minio pgadmin)

GREEN="\033[32m"
YELLOW="\033[33m"
RED="\033[31m"
BOLD="\033[1m"
RESET="\033[0m"
RESET_PFX=$'\e[0m'

# name|folder|port|kind   (kind: spring | frontend). Order = startup order.
SERVICES=(
  "service-registry|$MS_DIR/cloud/service-registry|8761|spring"
  "config-server|$MS_DIR/cloud/config-server|8888|spring"
  "user-service|$MS_DIR/services/user-service|5001|spring"
  "payment-service|$MS_DIR/services/payment-service|5002|spring"
  "booking-service|$MS_DIR/services/booking-service|5003|spring"
  "partner-service|$MS_DIR/services/partner-service|5004|spring"
  "planning-service|$MS_DIR/services/planning-service|5005|spring"
  "credit-service|$MS_DIR/services/credit-service|5006|spring"
  "payout-service|$MS_DIR/services/payout-service|5007|spring"
  "admin-service|$MS_DIR/services/admin-service|5008|spring"
  "api-gateway|$MS_DIR/cloud/api-gateway|5000|spring"
  "onshore-frontend|onshore-frontend|5173|frontend"
)

service_prefix_color() {
  if [[ $COLOR_MODE == "never" || -n "${NO_COLOR:-}" ]]; then
    echo ""; return 0
  fi
  if [[ $COLOR_MODE == "auto" && ! -t 1 && -z "${FORCE_COLOR:-}" ]]; then
    echo ""; return 0
  fi
  local name="$1"
  local palette=(
    $'\e[38;5;33m' $'\e[38;5;39m' $'\e[38;5;135m' $'\e[38;5;172m'
    $'\e[38;5;75m' $'\e[38;5;207m' $'\e[38;5;244m' $'\e[38;5;112m'
  )
  local sum=0 i ch
  for (( i=0; i<${#name}; i++ )); do
    printf -v ch '%d' "'${name:i:1}"
    sum=$(( sum + ch ))
  done
  echo -n "${palette[$(( sum % ${#palette[@]} ))]}"
}

# True when service name passes the --only filter
selected() {
  [[ -z "$ONLY_SERVICES" ]] && return 0
  local f
  IFS=',' read -r -a __filter <<<"$ONLY_SERVICES"
  for f in "${__filter[@]}"; do [[ "$f" == "$1" ]] && return 0; done
  return 1
}

build_cmd_for() { # kind
  case "$1" in
    spring) echo "mvn -q clean install -DskipTests" ;;
    frontend) echo "npm install && npm run build" ;;
  esac
}

dev_cmd_for() { # kind
  case "$1" in
    spring)
      if [[ -n "$SPRING_PROFILE" ]]; then
        echo "mvn spring-boot:run -Dspring-boot.run.profiles=$SPRING_PROFILE"
      else
        echo "mvn spring-boot:run"
      fi ;;
    frontend) echo "npm install && npm run dev" ;;
  esac
}

port_open() { (exec 3<>"/dev/tcp/127.0.0.1/$1") 2>/dev/null; }

wait_for_port() { # name port timeout
  local name="$1" port="$2" timeout="${3:-120}" waited=0
  until port_open "$port"; do
    sleep 2; waited=$(( waited + 2 ))
    if (( waited >= timeout )); then
      echo -e "${YELLOW}[WAIT ]${RESET} $name not listening on $port after ${timeout}s; continuing"
      return 0
    fi
  done
  echo -e "${GREEN}[READY]${RESET} $name on port $port"
}

kill_port() {
  local name="$1" port="$2" pids
  if ! command -v lsof >/dev/null 2>&1; then
    echo -e "${RED}[STOP][ERROR] lsof not found; cannot inspect port $port${RESET}" >&2
    return 1
  fi
  pids=$(lsof -ti tcp:"$port" -sTCP:LISTEN 2>/dev/null || true)
  if [[ -z "$pids" ]]; then
    echo -e "${YELLOW}[STOP][WARN ]${RESET} $name: nothing listening on port $port"; return 0
  fi
  echo -e "${GREEN}[STOP]${RESET} $name: terminating PID(s) $(echo $pids) on port $port"
  for pid in $pids; do kill "$pid" 2>/dev/null || true; done
  sleep 1
  for pid in $pids; do
    if kill -0 "$pid" 2>/dev/null; then
      echo -e "${YELLOW}[STOP][WARN ]${RESET} $name: PID $pid still alive; sending SIGKILL"
      kill -9 "$pid" 2>/dev/null || true
    fi
  done
}

stop_services() {
  echo -e "${BOLD}Stopping services...${RESET}"
  local entry name port
  for entry in "${SERVICES[@]}"; do
    IFS='|' read -r name _ port _ <<<"$entry"
    selected "$name" && kill_port "$name" "$port"
  done
  if [[ -z "$ONLY_SERVICES" && "${STOP_DOCKER:-true}" == "true" ]] && command -v docker >/dev/null 2>&1; then
    echo -e "${GREEN}[STOP]${RESET} Stopping docker infra"
    docker compose -f "$COMPOSE_DEV" down || true
    docker compose -f "$COMPOSE_APP" rm -sf "${DB_CONTAINERS[@]}" || true
  fi
  echo -e "${GREEN}[STOP]${RESET} Done."
}

usage() {
  cat <<EOF
Usage: $(basename "$0") [options]

Options:
  -l, --list         List services (name, folder, port, kind)
  -b, --build        Compile common-lib, then build services and print summary table
  -up, --up, -u      Compile common-lib, then start infra, backend services and frontend
  -s, --stop         Stop services by port (+ docker infra when no --only)
  --prune            Prune all Docker volumes before starting (use with -up)
  --profile PROFILE  Spring Boot profile (default: none)
  --only svc1,svc2   Restrict to a comma-separated subset of services
  --no-infra         Do not start docker infra (postgres, redis, kafka)
  --color / --no-color
  -v, --verbose      Verbose output
  -h, --help         Show this help

Services: $(for e in "${SERVICES[@]}"; do echo -n "${e%%|*} "; done)
EOF
}

list_services() {
  printf "${BOLD}%-22s %-45s %-6s %s${RESET}\n" "SERVICE" "FOLDER" "PORT" "KIND"
  local entry name folder port kind
  for entry in "${SERVICES[@]}"; do
    IFS='|' read -r name folder port kind <<<"$entry"
    printf "%-22s %-45s %-6s %s\n" "$name" "$folder" "$port" "$kind"
  done
}

# Every Spring service depends on common-lib; it must be installed before they build or run.
build_common_lib() {
  echo -e "${BOLD}[BUILD]${RESET} common-lib (prerequisite)"
  (cd "$ROOT_DIR/$MS_DIR/common-lib" && mvn -q clean install -DskipTests) || {
    echo -e "${RED}[BUILD][ERROR]${RESET} common-lib failed; aborting" >&2
    return 1
  }
}

build_services() {
  build_common_lib
  local -a results=()
  local entry name folder port kind cmd start end rc log
  for entry in "${SERVICES[@]}"; do
    IFS='|' read -r name folder port kind <<<"$entry"
    selected "$name" || continue
    cmd="$(build_cmd_for "$kind")"
    log="/tmp/build-${name}-$$.log"
    echo -e "${BOLD}[BUILD]${RESET} $name"
    start=$(date +%s)
    rc=0
    if [[ $VERBOSE == true ]]; then
      (cd "$ROOT_DIR/$folder" && bash -lc "$cmd") 2>&1 | tee "$log" || rc=${PIPESTATUS[0]}
    else
      (cd "$ROOT_DIR/$folder" && bash -lc "$cmd") >"$log" 2>&1 || rc=$?
    fi
    end=$(date +%s)
    if [[ $rc -eq 0 ]]; then
      results+=("$name|Success|$(( end - start ))|")
    else
      results+=("$name|Failed|$(( end - start ))|$log")
    fi
  done

  echo
  printf "${BOLD}%-22s %-8s %s${RESET}\n" "SERVICE" "STATUS" "TIME(s)"
  local r n s d lf color
  for r in "${results[@]}"; do
    IFS='|' read -r n s d lf <<<"$r"
    [[ $s == Success ]] && color=$GREEN || color=$RED
    printf "%-22s ${color}%-8s${RESET} %d\n" "$n" "$s" "$d"
  done
  for r in "${results[@]}"; do
    IFS='|' read -r n s d lf <<<"$r"
    if [[ $s == Failed && -f $lf ]]; then
      echo "--- $n (log: $lf) ---"
      tail -n 40 "$lf" || true
    fi
  done
}

ensure_docker_and_infra() {
  if ! command -v docker >/dev/null 2>&1; then
    echo -e "${RED}Docker CLI not found in PATH.${RESET}" >&2
    return 1
  fi
  if ! docker info >/dev/null 2>&1; then
    echo -e "${YELLOW}Docker not running. Attempting to start...${RESET}"
    [[ "$OSTYPE" == darwin* ]] && open -a Docker || true
    local waited=0
    until docker info >/dev/null 2>&1; do
      sleep 2; waited=$(( waited + 2 ))
      if (( waited >= 120 )); then
        echo -e "${RED}Docker daemon not ready within 120s.${RESET}" >&2
        return 1
      fi
    done
  fi
  echo -e "${BOLD}Starting infra: redis + kafka (dev compose), postgres databases...${RESET}"
  docker compose -f "$COMPOSE_DEV" up -d
  docker compose -f "$COMPOSE_APP" up -d --wait "${DB_CONTAINERS[@]}"
}

up_services() {
  build_common_lib
  if [[ $DO_NO_INFRA == false ]]; then
    ensure_docker_and_infra
  fi
  echo -e "${BOLD}Starting services (streaming output)...${RESET}"
  local -a results=()
  local entry name folder port kind cmd pid
  for entry in "${SERVICES[@]}"; do
    IFS='|' read -r name folder port kind <<<"$entry"
    selected "$name" || continue
    if [[ ! -d "$ROOT_DIR/$folder" ]]; then
      results+=("$name|Failed|-|missing-folder"); continue
    fi
    if port_open "$port"; then
      results+=("$name|Skipped|-|port-$port-in-use"); continue
    fi
    cmd="$(dev_cmd_for "$kind")"
    # Serve the local config-repo (correct dev DB ports) instead of the remote git repo
    if [[ $name == config-server ]]; then
      cmd="SPRING_PROFILES_INCLUDE=native SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCHLOCATIONS=file:$ROOT_DIR/config-repo $cmd"
    fi
    (
      set -o pipefail
      trap '' INT # keep running after Ctrl+C on the orchestrator
      cd "$ROOT_DIR/$folder" || exit 98
      echo -e "${GREEN}[START]${RESET} $name -> $cmd"
      color="$(service_prefix_color "$name")"
      if [[ -n "$color" ]]; then prefix="${color}[${name}]${RESET_PFX} "; else prefix="[${name}] "; fi
      esc_prefix=${prefix//\//\\/}
      bash -lc "$cmd" 2>&1 | sed -u "s/^/${esc_prefix}/"
      rc=${PIPESTATUS[0]}
      if [[ $rc -ne 0 ]]; then
        echo -e "${RED}[EXIT ]${RESET} $name exited with code $rc" >&2
      fi
    ) &
    pid=$!
    results+=("$name|Started|$pid|")
    # Registry and config server must be up before the services that depend on them
    case "$name" in
      service-registry|config-server) wait_for_port "$name" "$port" 120 ;;
    esac
  done

  echo
  printf "${BOLD}%-22s %-8s %-7s %s${RESET}\n" "SERVICE" "STATUS" "PID" "REASON"
  local r n s p rest color
  for r in "${results[@]}"; do
    IFS='|' read -r n s p rest <<<"$r"
    case "$s" in
      Started) color=$GREEN ;;
      Skipped) color=$YELLOW ;;
      *) color=$RED ;;
    esac
    printf "%-22s ${color}%-8s${RESET} %-7s %s\n" "$n" "$s" "$p" "$rest"
  done
  echo
  echo "Frontend: http://localhost:5173  |  Gateway: http://localhost:5000  |  Eureka: http://localhost:8761"
  echo "Stop with: $(basename "$0") -s"
  wait
}

main() {
  if [[ $# -eq 0 ]]; then
    usage; exit 0
  fi
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -l|--list) DO_LIST=true ;;
      -b|--build) DO_BUILD=true ;;
      -up|--up|-u) DO_UP=true ;;
      -s|--stop) DO_STOP=true ;;
      --prune) DO_PRUNE=true ;;
      --profile)
        shift || { echo "--profile requires argument" >&2; exit 1; }
        SPRING_PROFILE="$1" ;;
      --color) COLOR_MODE="always" ;;
      --no-color) COLOR_MODE="never" ;;
      -v|--verbose) VERBOSE=true ;;
      --only)
        shift || { echo "--only requires argument" >&2; exit 1; }
        ONLY_SERVICES="$1" ;;
      --no-infra) DO_NO_INFRA=true ;;
      -h|--help) usage; exit 0 ;;
      *) echo "Unknown option: $1" >&2; usage >&2; exit 1 ;;
    esac
    shift
  done

  [[ $DO_LIST == true ]] && list_services
  [[ $DO_STOP == true ]] && stop_services
  [[ $DO_BUILD == true ]] && build_services
  if [[ $DO_UP == true ]]; then
    if [[ $DO_PRUNE == true ]]; then
      echo -e "${BOLD}Pruning all Docker volumes...${RESET}"
      docker volume prune -af
    fi
    up_services
  fi
  if [[ $DO_LIST == false && $DO_BUILD == false && $DO_UP == false && $DO_STOP == false ]]; then
    usage; exit 1
  fi
}

main "$@"
