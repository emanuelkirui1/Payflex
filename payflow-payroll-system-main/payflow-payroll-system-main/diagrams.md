# System Diagrams

## Architecture Overview

```
+----------------+     +----------------+     +----------------+
|   Frontend     |     |    Backend     |     |   Database     |
|   (React)      | --> | (Spring Boot)  | --> |   (H2/MySQL)   |
|                |     |                |     |                |
| - Login        |     | - Controllers  |     | - Users        |
| - Dashboard    |     | - Services     |     | - Employees    |
| - Payroll View |     | - Repositories |     | - Payslips     |
+----------------+     +----------------+     +----------------+
```

## Layered Architecture

```
+-------------------+
|   Controllers     |
| (REST Endpoints)  |
+-------------------+
          |
          v
+-------------------+
|    Services       |
| (Business Logic)  |
+-------------------+
          |
          v
+-------------------+
|  Repositories     |
| (Data Access)     |
+-------------------+
```

## Payroll Generation Flow

```
Employee Request --> Controller --> Service --> Calculate Salary --> Calculate Tax --> Generate Payslip --> Repository --> Database
```

## Authentication Flow

```
Login Request --> AuthController --> AuthService --> Validate Credentials --> Generate JWT --> Return Token
                                                                 |
                                                                 v
Protected Request --> JwtFilter --> Validate Token --> Allow Access
```

## Multi-Tenant Data Isolation

```
Company A Data     Company B Data
+-------------+    +-------------+
| Employees   |    | Employees   |
| Salaries    |    | Salaries    |
| Payslips    |    | Payslips    |
+-------------+    +-------------+
      |                   |
      +---------+---------+
                |
          SUPERADMIN Access
```