# Payflow Payroll System

A full-stack payroll management system with React frontend and Spring Boot backend.

## Tech Stack

- **Frontend:** React 18, React Router
- **Backend:** Spring Boot 2.7, JPA, H2/PostgreSQL
- **Database:** H2 (dev), PostgreSQL (prod)
- **Security:** JWT, Spring Security
- **Deployment:** Docker, Docker Compose

## Quick Start

### Prerequisites

- Docker and Docker Compose
- Node.js 16+ (for local frontend development)
- Java 17 (for local backend development)

### Production Deployment

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd payflow-payroll-system
   ```

2. **Build and run with Docker Compose:**
   ```bash
   docker-compose up --build
   ```

3. **Access the application:**
   - Frontend: http://localhost
   - Backend API: http://localhost:8080

### Local Development

1. **Start the backend:**
   ```bash
   cd backend
   export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
   ./mvnw spring-boot:run
   ```

2. **Start the frontend:**
   ```bash
   cd frontend
   npm install
   npm start
   ```

3. **Access:**
   - Frontend: http://payflowpayroll.com:3000 (add to /etc/hosts: 127.0.0.1 payflowpayroll.com)
   - Backend: http://localhost:8080

## Default Users

- **Super Admin:** super@test.com / password
- **Employee:** admin@test.com / password

## API Documentation

- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Environment Variables

### Backend

- `SPRING_PROFILES_ACTIVE`: `prod` for production
- `JWT_SECRET`: Custom JWT secret (default provided)
- `DB_PASSWORD`: Database password (default: prodpassword)
- `PORT`: Server port (default: 8080)

### Frontend

- `PORT`: Frontend port (default: 3000)
- `HOST`: Host domain (default: payflowpayroll.com)

## Database

- **Development:** H2 in-memory/file database
- **Production:** PostgreSQL via Docker Compose

## Security

- JWT tokens with 24-hour expiration
- CORS configured for custom domain
- Password encryption with BCrypt
- Role-based authorization

## Deployment Notes

- Frontend is served via Nginx in production
- Backend runs on port 8080
- Database migrations handled by Flyway
- Static files optimized and gzipped

Password encryption using BCrypt

### 4. Employee Management
HR Capabilities:

Create, update, and delete employees

Assign employees to departments and companies

Store personal and payroll-related employee data

Employee Capabilities:

View own payroll information

Download personal payslips

### 5. Payroll Processing
Payroll Generation

Generate payroll for individual employees

Automatically calculates:

Basic salary

Tax deductions

Net salary

Payroll Logic Includes:

Salary computation

Tax calculation

Net pay calculation

Pay date tracking

### Payroll Approval Workflow (New) ✅

A multi-step approval workflow for payroll runs has been added to support proper review and authorization before payrolls are locked for payment.

- **DB migration:** `backend/src/main/resources/db/migration/V5__add_payroll_approvals.sql` (creates `payroll_run_approvals`).
- **Model:** `PayrollRunApproval` entity stores approver, role, decision, comment and timestamp.
- **Endpoints:**
  - `POST /api/payroll/runs/{id}/submit` — Submit a DRAFT run for review (role: HR).
  - `POST /api/payroll/runs/{id}/approve` — Approve a run (role: FINANCE).
  - `POST /api/payroll/runs/{id}/reject` — Reject a run (role: FINANCE).
  - `GET  /api/payroll/runs/{id}/approvals` — List approval events (roles: HR, FINANCE).
- **Behavior:** Submit moves status DRAFT → REVIEW; approve moves REVIEW → LOCKED; reject moves REVIEW → DRAFT.
- **Testing:** Unit and integration tests are recommended for idempotency and role checks (not yet included).

**Troubleshooting & Swagger/OpenAPI** ⚠️

If the application fails to start due to a SpringDoc / Spring HTTP API mismatch, you can temporarily start the backend with the SpringDoc auto-config excluded:

```bash
java -Dspring.autoconfigure.exclude=org.springdoc.webmvc.ui.SwaggerConfig,org.springdoc.core.configuration.SpringDocConfiguration -jar target/*.jar
```

Or permanently fix the problem by updating the `springdoc-openapi` dependency in `backend/pom.xml` to a version compatible with Spring Boot 2.7 (for example `1.6.x`), then rebuild to restore the Swagger UI.

### 6. Payslip Management
Payslip Features:

View payslips via API

Download payslips as PDF

Payslips include:

Employee ID

Salary

Tax

Net salary

Payment date

### 7. PDF Generation

✅ Automatically generates downloadable payslips

Uses a dedicated PayslipPdfService

Ready for integration with real PDF libraries (OpenPDF / iText)

### 8. Tax Management

Store and manage tax rules

Apply deductions during payroll generation

Centralized tax configuration (extensible)

### 9. Salary Management

Maintain salary records

Support for future:

Allowances

Bonuses

Deductions

Overtime

### 10. Audit Logging
✅ Audit Features:

Tracks system actions such as:

Payroll generation

User login

Employee creation

Records:

Action performed

User who performed it

Timestamp

Ensures accountability and compliance.

### 11. API Documentation (Swagger / OpenAPI)

✅ Auto-generated REST API documentation

Accessible via:

/swagger-ui.html


Easy API testing for developers & integrators

### 12. Database & Persistence
Database Support:

PostgreSQL / MySQL

JPA & Hibernate ORM

Flyway DB migrations (V1__init.sql)

Schema Includes:

Users

Roles

Companies

Employees

Payslips

Audit logs

### 13. Dockerized Deployment
✅ Docker Support:

Backend container

Database container

docker-compose.yml for local & production use

Benefits:

Easy setup

Consistent environments

Cloud-ready

### 14. Frontend (Scaffolded)
Pages:

Login Page

Dashboard

Payroll View

Payslip Download

Ready to integrate with:

React / Angular / Vue

REST APIs secured by JWT

### 15. System Architecture

RESTful API

Layered architecture:

Controller

Service

Repository

Clean package separation

Easy to extend and maintain

### 16. Future-Ready Enhancements

Planned / Extensible:

Monthly payroll runs

Email payslip delivery

Multiple tax slabs

Reports & analytics

Multi-currency support

Cloud deployment (AWS/GCP)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 16+ and npm
- H2 Database (embedded, no separate setup required)

## Installation

### Backend

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Install dependencies and build:
   ```bash
   mvn clean install
   ```

### Frontend

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

## Running the Application

### Backend

1. From the backend directory:
   ```bash
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080` (or configured port).

### Frontend

1. From the frontend directory:
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`.

## API Documentation

API documentation is available via Swagger UI at:
- `http://localhost:8080/swagger-ui.html`

## Database

The application uses H2 in-memory database for development. Data is persisted in `backend/testdb.mv.db`.

For production, configure PostgreSQL or MySQL in `application.properties`.

## Default Users

- **Super Admin:** super@test.com / `password` (role: **SUPERADMIN**)
- **Employee:** admin@test.com / `password` (role: **EMPLOYEE**)

---

## Developer Notes 🔧

- **Build & package (backend):**
  ```bash
  cd backend
  mvn -Dmaven.test.skip=true package
  # then
  java -jar target/*.jar
  ```

- **If Swagger/OpenAPI fails to start:**
  - Known issue: some `springdoc-openapi` versions expect newer Spring APIs and can throw `NoClassDefFoundError: org.springframework.http.HttpStatusCode`.
  - **Fix (recommended):** Update `backend/pom.xml` to use `org.springdoc:springdoc-openapi-ui:1.6.15` and rebuild (this repo has that change).
  - **Temporary workaround:** Start the app excluding springdoc auto-config:
    ```bash
    java -Dspring.autoconfigure.exclude=org.springdoc.webmvc.ui.SwaggerConfig,org.springdoc.core.configuration.SpringDocConfiguration -jar target/*.jar
    ```

- **H2 Console:** available at `http://localhost:8080/h2-console` (database file: `backend/testdb.mv.db`).

- **New: Payroll Approval Workflow (DB + API):**
  - Flyway migration: `V5__add_payroll_approvals.sql` creates table `payroll_run_approvals` to store approval history.
  - API endpoints:
    - `POST /api/payroll/runs/{id}/submit` — submit a payroll run for review (HR role).
    - `POST /api/payroll/runs/{id}/approve` — approve a payroll run (Finance role). Request body: `{ "comment": "Approved for payment" }`.
    - `POST /api/payroll/runs/{id}/reject` — reject a payroll run (Finance role). Request body: `{ "comment": "Reason for rejection" }`.
    - `GET  /api/payroll/runs/{id}/approvals` — list approval events for a payroll run (HR/Finance).

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html` (may be disabled until the `springdoc` dependency issue is resolved).

- **Testing:** Consider adding unit/integration tests for approval transitions (DRAFT → REVIEW → APPROVED/REJECTED) and controller endpoints.

---

## Architecture Diagrams

See [diagrams.md](diagrams.md) for system architecture and flow diagrams.