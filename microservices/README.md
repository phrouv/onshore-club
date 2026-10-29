# On Shore Club — Microservices

Spring Boot / Spring Cloud backend of the On Shore Club (One Pass) sports-club platform: members book partner classes with credits, partners manage activities and sessions, admins configure rules and monitor usage.

See the [root README](../README.md) for the full-stack quick start and the frontend.

## Architecture

```
Browser ──► api-gateway :5000 (JWT, routing, CORS)
               │  lb:// via Eureka
   ┌───────────┼──────────────────────────────────────────┐
   ▼           ▼            ▼            ▼        ▼       ▼
user-service  partner   planning     booking   credit   payout   admin   payment
   :5001      :5004      :5005        :5003     :5006    :5007   :5008    :5002

Infra: service-registry (Eureka) :8761 · config-server :8888 · Redis · Kafka · PostgreSQL (one DB per service)
```

- Only the gateway is exposed. Service-to-service calls (OpenFeign) use `/internal/**` endpoints, which the gateway never routes.
- Each service pulls its config from the Config Server, which reads the `config-repo/` files (ports, datasource). `optional:` import means a service falls back to its local `application.yaml` if the Config Server is down.
- Shared code lives in `common-lib` (enums, roles, events, `ApiException`, `GlobalExceptionHandler`, cross-service records).

## Services

Ports are defined in `config-repo/<service>.yml` (source of truth).

| Service                | Port | Database (host port)         | Responsibility                                                                                                          |
| ---------------------- | ---- | ---------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| `api-gateway`          | 5000 | — (Redis)                    | JWT validation, routing, CORS, token blacklist                                                                          |
| `service-registry`     | 8761 | —                            | Eureka discovery                                                                                                        |
| `config-server`        | 8888 | —                            | Serves `config-repo/`                                                                                                   |
| `user-service`         | 5001 | `onshore_user_db` (3302)     | Signup/login, JWT, profiles                                                                                             |
| `payment-service`      | 5002 | `onshore_payment_db` (3303)  | Razorpay / Stripe payments; publishes `payment.completed`                                                               |
| `booking-service`      | 5003 | `onshore_booking_db` (3305)  | Bookings, cancellation refund policy, check-ins (QR / manual), no-shows                                                 |
| `partner-service`      | 5004 | `onshore_partner_db` (3304)  | Partners (signup, approval), activities (fixed `partnerPayout`)                                                         |
| `planning-service`     | 5005 | `onshore_planning_db` (3306) | Class sessions, One Pass seats, weekly recurrence, credit pricing                                                       |
| `credit-service`       | 5006 | `onshore_credit_db` (3307)   | Credit ledger, plans, subscriptions, credit grid                                                                        |
| `payout-service`       | 5007 | `onshore_payout_db` (3308)   | Partner payouts: fixed amount per check-in, never derived from credits                                                  |
| `admin-service`        | 5008 | `onshore_admin_db` (3309)    | Usage rules, check-in/cancellation settings, dashboard                                                                  |
| `notification-service` | 8094 | —                            | Consumes `booking.confirmed` → email (SMTP/Thymeleaf) + SMS (Twilio). Not part of `docker-compose.yml` or `services.sh` |

Visibility rules: `demandLevel` is admin-only, `creditsRequired` is hidden from partners, `partnerPayout` is shown to partners and admins only.

### Gateway routes

| Path                                                                                   | Service          | Access                                    |
| -------------------------------------------------------------------------------------- | ---------------- | ----------------------------------------- |
| `/auth/**`                                                                             | user-service     | Public                                    |
| `/api/users/**`                                                                        | user-service     | JWT                                       |
| `/api/partners/**`, `/api/activities/**`                                               | partner-service  | JWT                                       |
| `/api/classes/**`                                                                      | planning-service | JWT                                       |
| `/api/bookings/**`                                                                     | booking-service  | JWT                                       |
| `/api/credits/**`, `/api/plans/**`, `/api/subscriptions/**`, `/api/credit-settings/**` | credit-service   | JWT                                       |
| `/api/payments/**`                                                                     | payment-service  | JWT                                       |
| `/api/payouts/**`                                                                      | payout-service   | JWT                                       |
| `/api/admin/**`                                                                        | admin-service    | JWT + `ROLE_SYSTEM_ADMIN`                 |
| `/docs/{service}/v3/api-docs`                                                          | each service     | Public (Swagger UI at `/swagger-ui.html`) |

Finer role checks (member / partner / admin) are enforced inside the services from the headers below.

## Security

1. `POST /auth/login` → user-service returns a JWT (`userId`, `email`, roles, expiry).
2. The client sends `Authorization: Bearer <token>`.
3. The gateway validates signature, expiry and the Redis blacklist (logout), then forwards `X-User-Id`, `X-User-Email`, `X-User-Roles`.
4. Backend services trust these headers.

Roles: `ROLE_MEMBER`, `ROLE_PARTNER`, `ROLE_SYSTEM_ADMIN`. Admin accounts cannot be created through signup.

## Enum persistence

All 12 entity enum fields use `@Enumerated(EnumType.STRING)` and have `VARCHAR` columns in their service's Liquibase changelog. Persist enum names, never Java ordinals.

## Events (Kafka)

| Topic               | Producer            | Consumer             |
| ------------------- | ------------------- | -------------------- |
| `payment.completed` | payment-service     | — (no consumer yet)  |
| `booking.confirmed` | — (no producer yet) | notification-service |

## Tech stack

Java 21 · Spring Boot 4.1.2 · Spring Cloud 2025.1.3 (Gateway MVC, Eureka, Config, OpenFeign) · Spring Data JPA (Hibernate `ddl-auto: update`) · PostgreSQL 18 · Redis · Apache Kafka (KRaft) · Resilience4j · JJWT · Razorpay / Stripe · Maven multi-module · Jib · Spotless (`hooks/pre-commit.sh`).

## Run

Prerequisites: Java 21, Maven 3.9+, Docker, Node (frontend only).

**Full stack in Docker** (from the repo root):

```bash
cd microservices && mvn clean package -DskipTests && cd ..
docker compose up -d
curl http://localhost:5000/actuator/health
```
```
{
   "components":{
      "clientConfigServer":{
         "details":{
            "propertySources":[
               "configserver:https://github.com/phrouv/onshore-config/api-gateway.yml",
               "configserver:https://github.com/phrouv/onshore-config/application.yml",
               "configClient"
            ]
         },
         "status":"UP"
      },
      "discoveryComposite":{
         "components":{
            "discoveryClient":{
               "details":{
                  "services":[
                     "booking-service",
                     "api-gateway",
                     "payment-service",
                     "config-server",
                     "admin-service",
                     "payout-service",
                     "notification-service",
                     "partner-service",
                     "user-service",
                     "planning-service",
                     "credit-service"
                  ]
               },
               "status":"UP"
            },
            "eureka":{
               "details":{
                  "applications":{
                     "BOOKING-SERVICE":1,
                     "API-GATEWAY":1,
                     "PAYMENT-SERVICE":1,
                     "CONFIG-SERVER":1,
                     "ADMIN-SERVICE":1,
                     "PAYOUT-SERVICE":1,
                     "NOTIFICATION-SERVICE":1,
                     "PARTNER-SERVICE":1,
                     "USER-SERVICE":1,
                     "PLANNING-SERVICE":1,
                     "CREDIT-SERVICE":1
                  }
               },
               "description":"Remote status from Eureka server",
               "status":"UP"
            }
         },
         "status":"UP"
      },
      "diskSpace":{
         "details":{
            "total":994662584320,
            "free":781929730048,
            "threshold":10485760,
            "path":"/Users/philipperouvray/dev/onshore-club/microservices/.",
            "exists":true
         },
         "status":"UP"
      },
      "livenessState":{
         "status":"UP"
      },
      "ping":{
         "status":"UP"
      },
      "readinessState":{
         "status":"UP"
      },
      "redis":{
         "details":{
            "version":"8.10.2"
         },
         "status":"UP"
      },
      "refreshScope":{
         "status":"UP"
      },
      "ssl":{
         "details":{
            "expiringChains":[
               
            ],
            "invalidChains":[
               
            ],
            "validChains":[
               
            ]
         },
         "status":"UP"
      }
   },
   "groups":[
      "liveness",
      "readiness"
   ],
   "status":"UP"
}
```

Images (`onshore/onshore-*`) are built with Jib; `init-databases.sql` creates the databases on first Postgres start.

**Local development** (services on the host, infra in Docker):

```bash
./services.sh -b      # build everything
./services.sh -up     # start infra + services + frontend
./services.sh -s      # stop
```

or only the infra (Redis + Kafka) with `docker compose -f docker-compose.dev.yml up -d`, then `mvn spring-boot:run` in a service folder. Postgres containers from `docker-compose.yml` are started by `services.sh`; the host ports above match `config-repo`.

## Configuration

Config Server source: `https://github.com/phrouv/onshore-config` (see `cloud/config-server/src/main/resources/application.yaml`); `config-repo/` in this repository holds the local copy of those files.

Secrets are placeholders in service `application.yaml` files and must be overridden by environment variables in any real deployment: `JWT_SECRET`, Razorpay (`RAZORPAY_KEY_ID`/`SECRET`), Stripe (`STRIPE_API_KEY`), SMTP credentials and Twilio (`TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_FROM_NUMBER`). The default `root` Postgres password in `config-repo` and compose is for local development only.

## Project structure

```
microservices/
├── pom.xml            # parent POM (versions)
├── common-lib/        # shared enums, roles, events, exceptions, club records
├── cloud/             # api-gateway, config-server, service-registry
├── services/          # user, payment, notification, partner, planning, credit, payout, admin, booking
└── hooks/pre-commit.sh
```

## Known limitations

- No lock on concurrent bookings of the last seat.
- User suspension is not ported.
- Service-to-service calls have no fallback (except `payment-service` → `UserClient`).
- `payment.completed` and `booking.confirmed` have no counterpart yet, so the payment → booking → notification chain is not wired end to end.
- Config Server is a single point of failure on first start (mitigated by the `optional:` import).
- Single Kafka broker, no replication in Docker Compose.
