# <span style="color:#22c55e">QuickCart</span>

<p align="center">
  <img alt="QuickCart banner" src="https://placehold.co/1200x320/0f172a/22c55e?text=QuickCart+Backend" />
</p>

<p align="center">
  <strong><span style="color:#38bdf8">Spring Boot backend for identity, OTP authentication, persistence, and service-ready commerce workflows.</span></strong>
</p>

---

## <span style="color:#f97316">Project Snapshot</span>

QuickCart is a Java Spring Boot backend focused on the core services behind a shopping experience. The current codebase includes phone OTP authentication, user and identity persistence, Redis-backed OTP challenge storage, Flyway migrations, and production-friendly configuration through environment variables.

## <span style="color:#a855f7">Tech Stack</span>

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

## <span style="color:#22c55e">Features</span>

- Phone OTP request and verification flow.
- OTP hashing, attempt limits, expiry, and resend cooldown configuration.
- User and authentication identity domain model.
- PostgreSQL schema migrations with Flyway.
- Redis adapter for OTP challenge persistence.
- Actuator health endpoint exposure.
- Environment-driven configuration for deployment.

## <span style="color:#38bdf8">Repository Layout</span>

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

## <span style="color:#f97316">Local Setup</span>

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

## <span style="color:#a855f7">Useful Commands</span>

```bash
cd backend
./mvnw test
./mvnw package
./mvnw spring-boot:run
```

## <span style="color:#22c55e">Git Hygiene</span>

Build output, compiled classes, local secrets, logs, and editor files are ignored. Keep source, configuration templates, migrations, wrapper files, and deployment manifests in Git; keep `target/` and `.env` files out.
