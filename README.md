# On Shore Club

Sports-club platform (One Pass): members book partner classes with credits, partners manage activities and sessions, admins configure rules and review usage.

| Folder | Content |
|---|---|
| `microservices/` | Spring Boot / Spring Cloud backend — see [microservices/README.md](microservices/README.md) |
| `onshore-frontend/` | React frontend (member, partner, admin spaces) |
| `config-repo/` | Config Server files: port and datasource per service |
| `documentation`, `ai/` | Specs and AI-assistant material |
| `docker-compose.yml` | Full stack: Postgres per service, Redis, Kafka, Eureka, Config Server, gateway, services |
| `docker-compose.dev.yml` | Redis + Kafka only, ports exposed for services running on the host |
| `init-databases.sql` | Creates one Postgres database per service |
| `services.sh` | Build / start / stop everything locally |

## Quick start

Prerequisites: Java 25, Maven 3.9+, Docker, Node.

```sh
./services.sh -l       # list services and ports
./services.sh -b       # build backend + frontend
./services.sh -up      # start docker infra, Spring Cloud services and frontend
./services.sh -s       # stop everything
```

Gateway: http://localhost:5000 · Swagger UI: http://localhost:5000/swagger-ui.html · Frontend: http://localhost:5173

Everything in Docker instead: build the jars (`cd microservices && mvn clean package -DskipTests`), then `docker compose up -d`.

## Services and ports

Defined in `config-repo/`: user 5001 · payment 5002 · booking 5003 · partner 5004 · planning 5005 · credit 5006 · payout 5007 · admin 5008 · api-gateway 5000 · service-registry 8761 · config-server 8888. Details, routes, security and events are in the [backend README](microservices/README.md).

## Frontend (onshore-frontend)

React (Vite, React Router, Redux Toolkit, Tailwind) ported from `onshore-lovable`, talking to the api-gateway (`VITE_API_URL`, default `http://localhost:5000`).

```sh
cd onshore-frontend && npm install && npm run dev   # http://localhost:5173
npm run lint && npm run build
```

Three spaces: member (`/member`, mobile-first), partner (`/partner`), admin (`/admin`), selected from the JWT role after login. Admin accounts cannot be created through signup.
