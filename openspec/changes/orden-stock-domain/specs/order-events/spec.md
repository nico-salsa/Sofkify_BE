## ADDED Requirements

### Requirement: Publish OrderCreatedEvent from domain

The system SHALL generate and publish OrderCreatedEvent when an order is successfully created, ensuring the event contains all necessary information for downstream consumers and maintains delivery guarantees.

#### Scenario: OrderCreatedEvent published on successful order creation
- **WHEN** an order is successfully created from a confirmed cart
- **THEN** the system SHALL publish an OrderCreatedEvent containing order ID, customer ID, and all order items with quantities

#### Scenario: OrderCreatedEvent contains correlation metadata
- **WHEN** publishing any OrderCreatedEvent
- **THEN** the system SHALL include event ID, order ID, timestamp, and correlation ID for tracing and idempotency

#### Scenario: OrderCreatedEvent publication uses outbox pattern
- **WHEN** the system creates an order but cannot immediately publish the event due to broker unavailability
- **THEN** the system SHALL store the event in an outbox table for guaranteed eventual delivery

#### Scenario: OrderCreatedEvent includes complete order information
- **WHEN** publishing an OrderCreatedEvent for a multi-item order
- **THEN** the system SHALL include product IDs, product names, quantities, unit prices, and total amount for downstream stock processing