## ADDED Requirements

### Requirement: Post-checkout navigation returns user to product catalog
The system SHALL provide clear navigation paths from the post-checkout success page back to the product catalog, enabling users to continue shopping after completing a purchase.

#### Scenario: "Back to Store" button navigates to product catalog
- **WHEN** a user successfully creates an order and lands on the order confirmation/success page
- **THEN** the page SHALL display a "Back to Store" or "Continue Shopping" button that navigates to the product catalog page (e.g., `/products` or `/`)

#### Scenario: Logo click navigates to home/product catalog
- **WHEN** a user clicks the application logo from the post-checkout page
- **THEN** the frontend SHALL navigate to the home page or product catalog, allowing the user to start a new shopping session

#### Scenario: Navigation from checkout page clears checkout state
- **WHEN** a user navigates away from the checkout/order confirmation page (via button, logo, or browser navigation)
- **THEN** the frontend SHALL clear checkout-specific state (e.g., selected cart reference, checkout step tracking) to prepare for the next shopping session
