🏦 Loan Origination System (LOS) — Backend

🚀 Enterprise-grade microservices backend built with Spring Boot
Focused on security, scalability, and distributed system reliability

------------------------------------------------------------------------

✨ Key Features

-   RBAC + Annotation-based Authorization (AOP)
-   Field Level Security (FLS) — masking & write control
-   Role-based Data Visibility Engine
-   Outbox Pattern + Kafka Integration
-   Retry + Dead Letter Queue (DLQ)
-   Audit Logging
-   Secure Authentication (JWT + O(1) Token Lookup)

------------------------------------------------------------------------

🧱 Architecture Overview

🔐 Security Layer - Role-Based Access Control (RBAC) - Profile →
Permission mapping - Annotation-driven authorization
(@RequiresPermission) - Enforced via AOP (PermissionAspect)

🔍 Field Level Security (FLS) - Read masking (email, mobile) - Write
validation (restricted updates)

Core Components: - FieldSecurityService - FieldFilterUtil -
FieldWriteFilterUtil - GlobalFieldMaskingAdvice

👁 Visibility Engine - Closure Table based role hierarchy - Manual
sharing - Rule-based sharing - Precomputed visibility (UserVisibility)

⚙️ Reliability & Messaging

📦 Outbox Pattern - No data loss - Stored events before publish - Retry
support

📨 Kafka Integration - Event-driven architecture - Idempotent
consumers - Retry + DLQ support

🔑 Authentication - JWT based auth - External secret (env config) - O(1)
token lookup (no full scan)

🧾 Audit System - Tracks user actions - Endpoint + role + status logging

------------------------------------------------------------------------

🧠 Key Design Decisions

Authorization → AOP + annotations
Data Security → Field-level masking
Visibility → Closure table
Messaging → Kafka + Outbox
Token → Hash lookup
Idempotency → Event tracking

------------------------------------------------------------------------

🛠 Tech Stack

-   Java 17
-   Spring Boot
-   Spring Security
-   Spring Data JPA
-   Kafka
-   H2 / PostgreSQL
-   Docker (optional)

------------------------------------------------------------------------

⚡ Running the Project

1.  Clone: git clone https://github.com/PrinceReshav/LOS.git

2.  Run: mvn clean install mvn spring-boot:run

3.  H2 Console: http://localhost:8080/h2-console

------------------------------------------------------------------------

📌 Version

v2.0-security-visibility-fls

------------------------------------------------------------------------

👨‍💻 Author

Prince Reshav
