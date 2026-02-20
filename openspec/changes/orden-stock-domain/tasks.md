# Implementation Tasks: orden-stock-domain

## 1. Domain Model Setup (TDD Red Phase)

- [x] 1.1 Create Cart aggregate with confirm() method test (RED)
- [x] 1.2 Create Order aggregate and OrderItem entity tests (RED)  
- [x] 1.3 Create CartStatus and OrderStatus enums tests (RED)
- [x] 1.4 Create Money and Quantity value objects tests (RED)
- [x] 1.5 Create domain exceptions tests: InsufficientStockException, CartAlreadyConfirmedException (RED)

## 2. Domain Events Infrastructure (TDD Red Phase)

- [x] 2.1 Create OrderCreatedEvent domain event test (RED)
- [x] 2.2 Create CartConfirmedEvent domain event test (RED) 
- [x] 2.3 Create DomainEventPublisher interface test (RED)
- [x] 2.4 Create event correlation and metadata tests (RED)

## 3. Use Cases and Ports Definition (TDD Red Phase)

- [x] 3.1 Create ConfirmCartUseCase interface test (RED)
- [x] 3.2 Create StockValidationPort interface test (RED)
- [x] 3.3 Create OrderEventPublisher port test (RED)
- [x] 3.4 Create CartRepository port test (RED)
- [x] 3.5 Create OrderRepository port test (RED)

## 4. Domain Logic Implementation (TDD Green Phase)

- [x] 4.1 Implement Cart.confirm() with stock validation (GREEN)
- [x] 4.2 Implement Order creation from confirmed cart (GREEN)
- [x] 4.3 Implement Money calculations and validations (GREEN)
- [x] 4.4 Implement domain events generation (GREEN)
- [x] 4.5 Implement domain exceptions with proper messages (GREEN)

## 5. Use Case Services (TDD Green Phase)

- [ ] 5.1 Implement ConfirmCartService coordinating the flow (GREEN)
- [ ] 5.2 Implement order creation service (GREEN)
- [ ] 5.3 Implement event publishing coordination (GREEN)
- [ ] 5.4 Implement idempotency checking logic (GREEN)

## 6. Domain Idempotency (TDD Cycles)

- [ ] 6.1 Create ProcessedEvent entity test (RED)
- [ ] 6.2 Create IdempotencyService interface test (RED)
- [ ] 6.3 Implement ProcessedEvent with event ID tracking (GREEN)
- [ ] 6.4 Implement IdempotencyService with duplicate detection (GREEN)
- [ ] 6.5 Refactor idempotency logic for clean separation (REFACTOR)

## 7. Edge Cases and Validations (TDD Cycles)

- [ ] 7.1 Test and implement cart status validation before confirm (RED→GREEN)
- [ ] 7.2 Test and implement stock validation failure handling (RED→GREEN)
- [ ] 7.3 Test and implement concurrent cart confirmation protection (RED→GREEN)
- [ ] 7.4 Test and implement order event publishing retries (RED→GREEN)

## 8. Domain Refactoring and Clean Code

- [ ] 8.1 Refactor Cart aggregate for single responsibility (REFACTOR)
- [ ] 8.2 Refactor value objects for immutability and validation (REFACTOR)
- [ ] 8.3 Refactor domain events for consistency and naming (REFACTOR)
- [ ] 8.4 Refactor exception handling for clarity (REFACTOR)
- [ ] 8.5 Extract common validation logic to shared utilities (REFACTOR)

## 9. Unit Test Coverage and Quality

- [ ] 9.1 Achieve ≥80% test coverage on domain model classes
- [ ] 9.2 Create parameterized tests for edge cases (boundary values)  
- [ ] 9.3 Add property-based tests for value objects (Money, Quantity)
- [ ] 9.4 Create integration tests for use case services with mocked ports
- [ ] 9.5 Add performance tests for order creation with large item lists

## 10. Documentation and Handoff

- [ ] 10.1 Document domain model class diagrams and relationships
- [ ] 10.2 Create developer guide for domain event patterns used
- [ ] 10.3 Document ports interface contracts for Humano B (infrastructure)
- [ ] 10.4 Create sample data builders for testing (Order, Cart, Events)
- [ ] 10.5 Validate all Gherkin scenarios from specs are covered by tests

---

## TDD Workflow Notes

**Red Phase**: Write failing tests first, commit with `test: red <description>`  
**Green Phase**: Implement minimal code to make tests pass, commit with `feat: green <description>`  
**Refactor Phase**: Clean code while keeping tests green, commit with `refactor: <description>`

**Branch**: `feature/orden-stock-sync`  
**Test Commands**: 
- Unit tests: `./gradlew :cart-service:test :order-service:test`
- Coverage: `./gradlew :cart-service:jacocoTestReport :order-service:jacocoTestReport`

**Handoff to Humano B**: Completed ports interfaces and domain models ready for infrastructure adapters (REST, RabbitMQ, JPA).