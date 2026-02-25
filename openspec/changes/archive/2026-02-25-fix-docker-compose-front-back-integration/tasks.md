## 1. Baseline and integration setup

- [x] 1.1 Freeze implementation baseline by checking out `main` in `Sofkify_BE` and `develop` in `sofkify-fe`, then record commit SHAs in the change notes.
- [x] 1.2 Create an integration compose entrypoint that can run frontend (`../sofkify-fe`) with backend services in one shared network.
- [x] 1.3 Add/update integration environment template files with explicit service URLs and documented defaults for local host + container mode.
- [x] 1.4 Validate integration startup (`docker compose up`) and confirm all required containers become healthy/reachable.

## 2. Backend contract and runtime fixes

- [x] 2.1 Fix `cart-service` and `order-service` CORS config package/location so Spring loads the configuration in both services.
- [x] 2.2 Implement or expose cart confirmation endpoint contract `POST /api/carts/{cartId}/confirm` with stock validation behavior.
- [x] 2.3 Ensure order creation contract `POST /api/orders/from-cart/{cartId}` is wired to a concrete use case implementation and returns deterministic errors for duplicates.
- [x] 2.4 Add/verify backend request logging for consumed frontend endpoints (users, products, carts, orders).

## 3. Frontend HTTP alignment (remove mock runtime)

- [x] 3.1 Replace product mock runtime (`productService` + static dataset fallback) with real HTTP calls to backend products API.
- [x] 3.2 Refactor frontend API config to domain-specific base URLs (users, products, carts, orders) instead of ambiguous single routing.
- [x] 3.3 Align cart confirmation and order creation clients with backend endpoint paths and payloads defined in specs.
- [x] 3.4 Update affected hooks/pages so checkout flow uses real cartId/user data and executes real backend requests end-to-end.

## 4. Request observability and verification

- [x] 4.1 Add frontend dev-mode HTTP instrumentation (method, URL, status, duration, error) for every outbound request.
- [x] 4.2 Define and document a log verification procedure showing matching frontend and backend traces for key user actions.
- [x] 4.3 Execute manual E2E verification (login, list products, add item, confirm cart, create order) and capture evidence of requests in terminal logs.

## 5. Repository hygiene and documentation

- [x] 5.1 Remove obsolete/contradictory integration artifacts in `Sofkify_BE` that conflict with the new working runtime path.
- [x] 5.2 Remove obsolete/contradictory mock/integration artifacts in `sofkify-fe` that hide real HTTP behavior.
- [x] 5.3 Rewrite `README.md` in `Sofkify_BE` with concise runbook for local FE+BE integration, verification commands, and troubleshooting basics.
- [x] 5.4 Rewrite `README.md` in `sofkify-fe` to match the same integration contract, environment variables, and request verification steps.

## 6. Quality gate and handoff

- [x] 6.1 Run backend tests/build checks for touched services and fix regressions introduced by contract/infrastructure changes.
- [x] 6.2 Run frontend lint/tests/build for touched flows and fix regressions introduced by HTTP refactor.
- [x] 6.3 Validate `openspec` artifacts against implementation readiness and prepare for apply/implementation phase handoff.
