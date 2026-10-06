# QuickCart

![Java 25](https://img.shields.io/badge/Java-25-2fbf00?style=flat-square&labelColor=555)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-088cc7?style=flat-square&labelColor=555)
![Next.js](https://img.shields.io/badge/Next.js-15.5.27-111827?style=flat-square&labelColor=555)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.6-336791?style=flat-square&labelColor=555)
![Branch](https://img.shields.io/badge/Branch-local--dev-f97316?style=flat-square&labelColor=555)

---

![Project Snapshot](https://img.shields.io/badge/Section-Project%20Snapshot-f97316?style=flat-square&labelColor=555)

QuickCart is a Blinkit-style instant delivery demo with a Spring Boot backend, PostgreSQL product catalogue, email/password JWT-style login, cart, checkout, card payment confirmation, and a Next.js storefront.

![Features](https://img.shields.io/badge/Section-Features-22c55e?style=flat-square&labelColor=555)

- Email/password registration and login with signed JWT response.
- MFA-ready user model for later integration.
- PostgreSQL-backed stores, categories, product catalogue, stock, and availability.
- Cart add, increment, decrement, remove, and total calculation.
- Checkout flow with delivery address and card payment confirmation.
- Order history with payment-confirmed status and ETA.
- Next.js + Tailwind storefront with filters, search, product images, cart, checkout, and order panel.
- Postgres-only local Docker setup. No Redis and no OTP logic.

![API Surface](https://img.shields.io/badge/Section-API%20Surface-088cc7?style=flat-square&labelColor=555)

| Area | Method | Endpoint |
| --- | --- | --- |
| Auth | POST | `/api/v1/auth/register` |
| Auth | POST | `/api/v1/auth/login` |
| Stores | GET | `/api/v1/stores` |
| Products | GET | `/api/v1/products` |
| Products | GET | `/api/v1/products?category=Fresh` |
| Categories | GET | `/api/v1/categories` |
| Cart | GET | `/api/v1/users/{userId}/cart` |
| Cart | PUT | `/api/v1/users/{userId}/cart/items` |
| Cart | DELETE | `/api/v1/users/{userId}/cart/items/{productId}` |
| Checkout | POST | `/api/v1/checkout` |
| Orders | GET | `/api/v1/users/{userId}/orders` |

![Repository Layout](https://img.shields.io/badge/Section-Repository%20Layout-38bdf8?style=flat-square&labelColor=555)

```text
quick_cart/
  backend/   Spring Boot API, JPA entities, Flyway migrations
  frontend/  Next.js storefront
  codex/     Local branch guardrails and notes
```

![Local Setup](https://img.shields.io/badge/Section-Local%20Setup-f97316?style=flat-square&labelColor=555)

Start PostgreSQL:

```bash
docker compose up -d
```

Backend:

```bash
cd backend
set -a
source ../.env
set +a
./mvnw spring-boot:run
```

Frontend:

```bash
cd frontend
pnpm install
cp .env.local.example .env.local
pnpm dev
```

![Local Links](https://img.shields.io/badge/Section-Local%20Links-a855f7?style=flat-square&labelColor=555)

```text
Frontend:   http://localhost:3000
Backend:    http://localhost:8081
PostgreSQL: jdbc:postgresql://localhost:5432/quickcart
Container:  quick_cart_postgres
```

![Verification](https://img.shields.io/badge/Section-Verification-22c55e?style=flat-square&labelColor=555)

Verified locally:

```bash
cd backend && ./mvnw test
cd frontend && pnpm build
```

Also smoke-tested a full API flow: register user, list products, add product to cart, checkout by card, and fetch order history.
