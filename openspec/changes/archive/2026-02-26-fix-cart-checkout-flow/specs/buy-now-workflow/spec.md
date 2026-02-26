## ADDED Requirements

### Requirement: "Buy Now" button provides express checkout without product duplication
The system SHALL implement "Buy Now" as a direct path to cart confirmation without duplicating products that are already in the cart with the selected quantity.

#### Scenario: "Buy Now" with product not yet in cart
- **WHEN** a user selects a product with quantity N and clicks "Buy Now" when that product is NOT already in their active cart
- **THEN** the frontend SHALL call `POST /api/carts/items` with the product and quantity, receive the cart response, then immediately call `POST /api/carts/{cartId}/confirm` to confirm the cart

#### Scenario: "Buy Now" with product already in cart at exact quantity
- **WHEN** a user selects a product with quantity N and clicks "Buy Now" when that product is ALREADY in their active cart with exactly quantity N
- **THEN** the frontend SHALL skip the add-to-cart step and directly call `POST /api/carts/{cartId}/confirm` to confirm the existing cart

#### Scenario: "Buy Now" with product in cart at different quantity
- **WHEN** a user selects a product with quantity N and clicks "Buy Now" when that product is ALREADY in their active cart with quantity M (where M ≠ N)
- **THEN** the frontend SHALL call `PUT /api/carts/items/{cartItemId}` to update the quantity to N, then call `POST /api/carts/{cartId}/confirm` to confirm the cart

#### Scenario: "Buy Now" navigates to checkout page after confirmation
- **WHEN** the cart confirmation succeeds (200 response from `POST /api/carts/{cartId}/confirm`)
- **THEN** the frontend SHALL navigate the user to the checkout/order creation page with the confirmed cart ID

#### Scenario: "Buy Now" handles confirmation failure gracefully
- **WHEN** cart confirmation fails due to insufficient stock or other validation errors
- **THEN** the frontend SHALL display the error message from the backend response and keep the user on the product page without clearing the cart
