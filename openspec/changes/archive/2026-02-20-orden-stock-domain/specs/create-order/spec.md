## ADDED Requirements

### Requirement: Create order from confirmed cart

The system SHALL create a new order in PENDING state from a confirmed cart, preserving all item details, quantities, and pricing information. The order creation MUST be idempotent and maintain referential integrity.

#### Scenario: Successful order creation from confirmed cart
- **WHEN** the system processes a confirmed cart to create an order
- **THEN** the system SHALL generate a new order with PENDING status containing all cart items with their quantities and prices

#### Scenario: Order creation preserves cart item details
- **WHEN** creating an order from a cart with multiple items having different prices and quantities  
- **THEN** the system SHALL preserve exact product IDs, names, quantities, unit prices, and calculate accurate totals

#### Scenario: Order creation generates unique identifier
- **WHEN** the system creates any new order
- **THEN** the system SHALL assign a unique UUID identifier that can be used for future order operations and event correlation

#### Scenario: Order creation handles pricing calculations
- **WHEN** creating an order with multiple items
- **THEN** the system SHALL calculate item subtotals (quantity × unit price) and total order amount accurately using decimal precision