## ADDED Requirements

### Requirement: Frontend handles already-confirmed cart errors gracefully
The system SHALL detect when a cart is already confirmed (via backend 400 error or cached state) and prevent the user from attempting re-confirmation, ensuring a clear user experience and correct state management.

#### Scenario: Frontend detects cached CONFIRMED status
- **WHEN** the frontend has a cart in CONFIRMED status cached in application state (from previous confirmation or fetched cart data)
- **THEN** the frontend SHALL disable the "Confirm Cart" button and display messaging indicating the cart is ready for order creation (e.g., "Cart confirmed - proceed to create order")

#### Scenario: Frontend handles 400 error from duplicate confirmation attempt
- **WHEN** the frontend attempts to call `POST /api/carts/{cartId}/confirm` and receives a 400 error with message "Cart is already confirmed"
- **THEN** the frontend SHALL update the cart status to CONFIRMED in application state and redirect the user to the order creation flow or display an informative message instead of showing a generic error

#### Scenario: Frontend recovers from stale cart state
- **WHEN** the frontend has stale cart data (e.g., cart was confirmed in another browser tab) and attempts to modify the cart (add/remove items)
- **THEN** the frontend SHALL detect the 400/409 error from the backend, re-fetch the cart state via `GET /api/carts/{cartId}`, and update UI to reflect the CONFIRMED status
