## ADDED Requirements

### Requirement: Confirm active cart with stock validation
The system SHALL validate stock availability and transition an ACTIVE cart to CONFIRMED state when confirmation is initiated through `POST /api/carts/{cartId}/confirm`. The confirmation process MUST be atomic, return explicit confirmation metadata, and reject invalid transitions deterministically.

#### Scenario: Successful cart confirmation with available stock
- **WHEN** a user confirms an active cart that contains items with sufficient stock
- **THEN** the system SHALL validate stock for all items, transition the cart status to CONFIRMED, and return confirmation details including `cartId` and `confirmedAt`

#### Scenario: Cart confirmation fails due to insufficient stock
- **WHEN** a user attempts to confirm a cart containing items with insufficient stock
- **THEN** the system SHALL reject the confirmation with specific stock shortage details and maintain the cart in ACTIVE state

#### Scenario: Cart confirmation fails for already confirmed cart
- **WHEN** a user attempts to confirm a cart that is already in CONFIRMED state
- **THEN** the system SHALL reject the operation with an appropriate error indicating the cart is already confirmed

#### Scenario: Cart confirmation with mixed stock availability
- **WHEN** a user confirms a cart where some items have sufficient stock and others do not
- **THEN** the system SHALL reject the entire confirmation and provide details about all items lacking sufficient stock

#### Scenario: Confirmation request is observable in logs
- **WHEN** frontend triggers `POST /api/carts/{cartId}/confirm` in local integration mode
- **THEN** request and outcome SHALL be visible in frontend and cart-service logs

---

## ADDED Requirements (Frontend)

### Requirement: Frontend handles cart confirmation edge cases gracefully
The frontend SHALL detect and recover from scenarios where cached cart state diverges from backend state, particularly around the CONFIRMED status.

#### Scenario: Frontend detects cached CONFIRMED cart before re-confirmation
- **WHEN** the frontend has a locally-cached cart reference whose status is already CONFIRMED (e.g., from a previous session or stale localStorage)
- **THEN** the frontend SHALL skip the confirmation API call and either proceed directly to order creation or prompt the user to start a new cart

#### Scenario: Frontend handles 400 "already confirmed" error
- **WHEN** the frontend sends `POST /api/carts/{cartId}/confirm` and receives a 400 response with message indicating the cart is already confirmed
- **THEN** the frontend SHALL treat the cart as confirmed, clear the stale cart reference, and allow the user to proceed to order creation or start a new shopping session

#### Scenario: Frontend recovers from stale cart state
- **WHEN** the frontend detects a state inconsistency between local cart data and backend cart state (e.g., local says ACTIVE but backend says CONFIRMED or cart not found)
- **THEN** the frontend SHALL clear all local cart references, display an informational message to the user, and redirect to product catalog to start fresh
