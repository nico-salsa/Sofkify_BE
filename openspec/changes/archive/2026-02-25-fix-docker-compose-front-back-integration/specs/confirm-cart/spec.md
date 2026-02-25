## MODIFIED Requirements

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

