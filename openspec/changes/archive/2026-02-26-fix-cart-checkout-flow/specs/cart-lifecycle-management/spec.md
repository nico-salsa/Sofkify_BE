## ADDED Requirements

### Requirement: Frontend manages cart lifecycle across multiple purchase sessions
The system SHALL enable the frontend to track cart state transitions (ACTIVE → CONFIRMED → CLEARED) and automatically initialize new carts for subsequent purchases without backend state conflicts.

#### Scenario: New cart is created after order completion
- **WHEN** the frontend successfully creates an order from a confirmed cart via `POST /api/orders/from-cart/{cartId}`
- **THEN** the frontend SHALL clear the current cart reference from application state (localStorage, React context, etc.) and treat the next `POST /api/carts/items` as a new shopping session

#### Scenario: Frontend prevents duplicate confirmation attempts
- **WHEN** the frontend attempts to confirm a cart that is already in CONFIRMED state
- **THEN** the frontend SHALL detect the state mismatch (via cached status or backend 400 error) and prevent the user from re-confirming, instead offering navigation to order creation or cart clearing

#### Scenario: Cart state persists across page reloads during active session
- **WHEN** a user has an ACTIVE cart with items and reloads the browser page
- **THEN** the frontend SHALL retrieve the cart state from storage (localStorage) or re-fetch from backend via `GET /api/carts` using `X-Customer-Id` header and restore the shopping experience

#### Scenario: Multiple purchases in same session work seamlessly
- **WHEN** a user completes purchase flow (add items → confirm cart → create order) and then navigates back to product catalog to shop again
- **THEN** the frontend SHALL have cleared the previous cart reference, and adding a new item SHALL trigger backend to create a fresh cart automatically (existing backend behavior)

#### Scenario: Cart state transitions are reflected in UI
- **WHEN** a cart transitions from ACTIVE to CONFIRMED state (after `POST /api/carts/{cartId}/confirm` succeeds)
- **THEN** the frontend SHALL update UI to disable cart modification actions (add/remove items, update quantities) and enable order creation action
