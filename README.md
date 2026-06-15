# Inventory Order System

A mini inventory and checkout system for senior backend interview practice. The stack pairs a Spring Boot 3 API with a React + TypeScript frontend and PostgreSQL.

## Prerequisites

- Java 21+
- Node.js 20+
- Docker and Docker Compose

## Quick start

### 1. Start PostgreSQL

```bash
docker compose up -d
```

### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

The API runs at `http://localhost:8080`.

- Actuator health: `http://localhost:8080/actuator/health`
- API health: `http://localhost:8080/api/v1/health`

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The UI runs at `http://localhost:5173`.

## Project layout

```
spring-boot-react-hibernate/
├── backend/          # Spring Boot 3 + Maven + Flyway
├── frontend/         # Vite + React + TypeScript
└── docker-compose.yml
```

## Database

PostgreSQL 16 runs on port `5432` with:

| Setting  | Value       |
|----------|-------------|
| Database | `inventory` |
| User     | `app`       |
| Password | `app`       |

Flyway migrations live in `backend/src/main/resources/db/migration/`.

## Tests

Integration tests connect to the docker-compose PostgreSQL instance. Start the database first:

```bash
docker compose up -d
cd backend && ./mvnw test
```

## API (planned)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/v1/products` | Paginated catalog |
| GET | `/api/v1/products/{id}` | Product detail |
| POST | `/api/v1/products` | Create product |
| PATCH | `/api/v1/products/{id}/restock` | Add stock |
| POST | `/api/v1/orders` | Checkout |
| GET | `/api/v1/orders` | Order history |
| GET | `/api/v1/orders/{id}` | Order detail |
| PATCH | `/api/v1/orders/{id}/cancel` | Cancel order |

## Interview extension tasks

See the project plan for timed practice scenarios covering concurrency, caching, transactions, and full-stack features.
