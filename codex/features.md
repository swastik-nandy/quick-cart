# QuickCart Local Dev Features

Target product: full-stack instant-delivery commerce demo with a five-minute order flow.

Core backend modules:

- Email/password authentication with JWT-style token response.
- MFA-ready identity model for later integration.
- Store discovery.
- Product catalog.
- Cart item management.
- Card checkout and payment-confirmed order creation.
- Order history and ETA tracking.
- Health and operational readiness endpoints through Spring Actuator.
- Next.js storefront with search, filters, cart, checkout, and order panels.

Local infrastructure:

- Single Docker container: `quick_cart_postgres`.
- PostgreSQL exposed on `localhost:5432`.
- Spring Boot API running on `localhost:8081`.
- Next.js frontend running on `localhost:3000`.
