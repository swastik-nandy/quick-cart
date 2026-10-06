# QuickCart Backend

![Java 25](https://img.shields.io/badge/Java-25-2fbf00?style=flat-square&labelColor=555)
![Spring Boot 4.1.1](https://img.shields.io/badge/Spring%20Boot-4.1.1-088cc7?style=flat-square&labelColor=555)
![PostgreSQL Flyway](https://img.shields.io/badge/PostgreSQL-Flyway-336791?style=flat-square&labelColor=555)
![Redis OTP Store](https://img.shields.io/badge/Redis-OTP%20Store-dc382d?style=flat-square&labelColor=555)

---

![Project Snapshot](https://img.shields.io/badge/Section-Project%20Snapshot-f97316?style=flat-square&labelColor=555)

QuickCart is a Java Spring Boot backend focused on the core services behind a shopping experience. The current codebase includes phone OTP authentication, user and identity persistence, Redis-backed OTP challenge storage, Flyway migrations, and production-friendly configuration through environment variables.

![Tech Stack](https://img.shields.io/badge/Section-Tech%20Stack-a855f7?style=flat-square&labelColor=555)

| Layer | Choice |
| --- | --- |
| Runtime | Java 25 |
| Framework | Spring Boot 4.1 |
| API | Spring Web MVC |
| Database | PostgreSQL |
| Migrations | Flyway |
| Cache / OTP Store | Redis |
| Build Tool | Maven Wrapper |
| Deployment Artifact | Dockerfile for Vercel-style backend packaging |

![Features](https://img.shields.io/badge/Section-Features-22c55e?style=flat-square&labelColor=555)

- Phone OTP request and verification flow.
- OTP hashing, attempt limits, expiry, and resend cooldown configuration.
- User and authentication identity domain model.
- PostgreSQL schema migrations with Flyway.
- Redis adapter for OTP challenge persistence.
- Actuator health endpoint exposure.
- Environment-driven configuration for deployment.

![Repository Layout](https://img.shields.io/badge/Section-Repository%20Layout-38bdf8?style=flat-square&labelColor=555)

```text
quick_cart/
  backend/
    src/main/java/com/quick_cart/backend/
      config/          Application properties bindings
      identity/        OTP authentication and identity domain
      infrastructure/  Redis-backed adapters
    src/main/resources/
      db/migration/    Flyway SQL migrations
      application.yaml Spring configuration
```

![Local Setup](https://img.shields.io/badge/Section-Local%20Setup-f97316?style=flat-square&labelColor=555)

From the repository root:

```bash
cd backend
./mvnw spring-boot:run
```

Required environment variables:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/quickcart
export DB_USERNAME=quickcart
export DB_PASSWORD=quickcart
export REDIS_URL=redis://localhost:6379
export OTP_HMAC_SECRET=replace-with-a-long-random-secret
```

Optional environment variables:

```bash
export PORT=8080
export OTP_DELIVERY_MODE=disabled
```

![Useful Commands](https://img.shields.io/badge/Section-Useful%20Commands-a855f7?style=flat-square&labelColor=555)

```bash
cd backend
./mvnw test
./mvnw package
./mvnw spring-boot:run
```

![Git Hygiene](https://img.shields.io/badge/Section-Git%20Hygiene-22c55e?style=flat-square&labelColor=555)

Build output, compiled classes, local secrets, logs, and editor files are ignored. Keep source, configuration templates, migrations, wrapper files, and deployment manifests in Git; keep `target/` and `.env` files out.
