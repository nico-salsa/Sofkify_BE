## ADDED Requirements

### Requirement: Create order from confirmed cart
The system SHALL create a new order in PENDING state from a confirmed cart, preserving all item details, quantities, and pricing information. Order creation SHALL be triggered through `POST /api/orders/from-cart/{cartId}` and MUST be idempotent for repeated requests against the same confirmed cart.

#### Scenario: Successful order creation from confirmed cart via API
- **WHEN** the frontend invokes `POST /api/orders/from-cart/{cartId}` for a confirmed cart
- **THEN** the system SHALL generate a new order in PENDING state and return its identifier and order details

#### Scenario: Order creation preserves cart item details
- **WHEN** creating an order from a cart with multiple items having different prices and quantities
- **THEN** the system SHALL preserve exact product IDs, names, quantities, unit prices, and calculate accurate totals

#### Scenario: Order creation generates unique identifier
- **WHEN** the system creates any new order
- **THEN** the system SHALL assign a unique UUID identifier that can be used for future order operations and event correlation

#### Scenario: Order creation handles pricing calculations
- **WHEN** creating an order with multiple items
- **THEN** the system SHALL calculate item subtotals (quantity x unit price) and total order amount accurately using decimal precision

#### Scenario: Duplicate creation attempts are rejected deterministically
- **WHEN** the same confirmed cart is submitted again to `POST /api/orders/from-cart/{cartId}`
- **THEN** the system SHALL reject duplicate creation with deterministic conflict semantics and without creating a second order
