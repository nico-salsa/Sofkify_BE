## Baseline

- Backend repository: `Sofkify_BE`
- Backend branch: `main`
- Backend commit: `1ab9b49a31c7ee1712f973c51e3552929e638893`

- Frontend repository: `sofkify-fe`
- Frontend branch: `develop`
- Frontend commit: `1eeda1298fca148b5982434c1ad1c848914d729b`

This baseline was captured before applying implementation tasks for the
`fix-docker-compose-front-back-integration` change.

## Validation Summary

- Backend checks:
  - `order-service`: tests OK.
  - `cart-service`: compile OK, `ConfirmCartServiceTest` OK.
  - `product-service`: compile/build OK.
  - `user-service`: compile/build OK.
- Frontend checks:
  - `npm run lint`: OK.
  - `npm run build`: OK.
  - `npx vitest run`: OK.

## Runtime Verification (2026-02-25)

- Integrated stack started with:
  - `docker compose -f docker-compose.yml -f docker-compose.integration.yml up -d --build`
- E2E API flow verified:
  - register -> login -> list products -> add cart item -> confirm cart -> create/get order.
  - result: `confirmSuccess=true`, `orderStatus=PENDING`.
- Backend HTTP evidence captured in logs via `RequestLoggingFilter`.

## Additional Runtime Hardening

- `product-service` updated for event forward compatibility:
  - `OrderCreatedEventDTO` now uses `@JsonIgnoreProperties(ignoreUnknown = true)`.
- This prevents parsing failures when order events include extra metadata fields
  (for example `eventId`, `occurredOn`, `eventType`) and keeps stock decrement processing stable.
