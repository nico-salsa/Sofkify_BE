## Context

The Sofkify e-commerce platform has 4 microservices (user, product, cart, order) orchestrated via REST APIs. The frontend (React SPA on localhost:5173) interacts with cart-service (port 8083) and order-service (port 8082). Cart state flows through: ACTIVE → CONFIRMED → order created.

**Current cart creation flow**: The first `POST /api/carts/items` for a customer automatically creates a cart if none exists (see `AddItemToCartService.addItem()`). No explicit "create cart" endpoint exists.

**Current cart state management**: Carts remain CONFIRMED indefinitely after confirmation. The backend correctly rejects duplicate confirmation attempts (400 "Cart is already confirmed"). The frontend incorrectly retries confirmation on the same cart instead of creating a new one.

**Constraints**:
- Backend API contracts are stable and correct; minimize backend changes
- Frontend must handle multiple purchase sessions per user
- Hexagonal architecture and DDD principles must be preserved in any backend changes

## Goals / Non-Goals

**Goals:**
- Enable multiple purchases per session without "Cart is already confirmed" errors
- Fix "Buy Now" button duplicating products
- Implement post-checkout navigation back to store
- Clarify cart lifecycle management in frontend

**Non-Goals:**
- No cart deletion/archiving features (carts remain CONFIRMED in DB)
- No backend state machine changes (ACTIVE → CONFIRMED is correct)
- No user notification/email features
- No order cancellation/modification flows

## Decisions

### Decision 1: Cart Lifecycle Management Strategy

**Chosen**: Frontend-driven cart replacement after order creation

**Rationale**: The backend already auto-creates carts on first `POST /api/carts/items` if none exists. Reusing this pattern avoids new backend endpoints and preserves API simplicity.

**How it works**:
1. User shops → adds items → frontend tracks `currentCartId`
2. User confirms cart → cart transitions to CONFIRMED → order created
3. Frontend **clears** `currentCartId` from state (localStorage/context)
4. Next time user adds item → backend creates **new** cart automatically
5. Frontend receives new cart ID in response and tracks it

**Alternatives considered**:
- **Alt A**: Add `POST /api/carts` endpoint to create empty carts → Rejected: adds complexity, carts are implicitly created on first item
- **Alt B**: Add `DELETE /api/carts/{cartId}` endpoint → Rejected: unnecessary, confirmed carts are historical records
- **Alt C**: Backend auto-creates new cart on order creation → Rejected: violates single responsibility (order-service shouldn't manage carts)

**Implementation**:
- Frontend: After `POST /api/orders/from-cart/{cartId}` succeeds, call `clearCart()` in state management
- Backend: No changes required

---

### Decision 2: "Buy Now" Workflow

**Chosen**: "Buy Now" = confirm current cart WITHOUT adding products again

**Rationale**: The current implementation appears to call both `addItem(quantity)` AND `confirmCart()`, causing duplication. "Buy Now" should be a shortcut to checkout, not a secondary add-to-cart.

**Expected behavior**:
- User selects product + quantity → clicks "Buy Now"
- Frontend checks if product is already in cart with that quantity
  - **If NOT in cart**: `POST /api/carts/items` → then `POST /api/carts/{cartId}/confirm`
  - **If ALREADY in cart**: Skip add, directly `POST /api/carts/{cartId}/confirm`
- Navigate to checkout page

**Alternatives considered**:
- **Alt A**: "Buy Now" creates a separate ephemeral cart → Rejected: over-engineering, conflicts with single cart per customer
- **Alt B**: "Buy Now" always adds items regardless of cart state → Rejected: causes the reported duplication bug

**Implementation**:
- Frontend: Refactor "Buy Now" click handler to check cart state before calling addItem
- Backend: No changes required

---

### Decision 3: Post-Checkout Navigation

**Chosen**: Fix React Router links for "Back to Store" button and logo

**Rationale**: This is a frontend routing issue, not an API or state management problem. Navigation after checkout should return users to the product catalog.

**Implementation**:
- Frontend: Update `onClick` handlers and `Link` components on checkout success page
- Target route: `/products` or `/` (product catalog)
- Ensure logo and "Back to Store" button both navigate correctly

**Implementation**:
- Frontend only: No backend changes

---

### Decision 4: Cart State Handling in Frontend

**Chosen**: Add explicit cart state transitions in frontend logic

**Rationale**: The frontend should track cart lifecycle (ACTIVE → CONFIRMED → CLEARED) and prevent UI actions that would violate backend constraints.

**State transitions**:
```
ACTIVE          → User can add/remove items, update quantities, confirm cart
CONFIRMED       → User CANNOT modify cart, can create order
ORDER_CREATED   → Frontend clears cart reference, UI resets to shopping mode
```

**Alternatives considered**:
- **Alt A**: Always re-fetch cart state from backend before actions → Rejected: increases latency, unnecessary API calls
- **Alt B**: Backend adds websocket notifications for cart state changes → Rejected: over-engineering for single-user cart

**Implementation**:
- Frontend: Add cart status check before enabling "Confirm Purchase" button
- If cart status is CONFIRMED, show "Create Order" instead
- After order creation, transition to ORDER_CREATED state and clear cart reference

## Risks / Trade-offs

**Risk**: User opens multiple browser tabs and creates conflicting carts  
**Mitigation**: The backend already enforces one ACTIVE cart per customer. Multiple tabs will share the same cart (fetched by `X-Customer-Id`). This is existing behavior and acceptable.

**Risk**: Frontend cart state becomes stale (user modifies cart in another tab)  
**Mitigation**: Fetch fresh cart state on checkout page load via `GET /api/carts` (using `X-Customer-Id` header). Display loading state during fetch.

**Risk**: User navigates away mid-checkout and cart remains CONFIRMED without order  
**Mitigation**: This is acceptable. The user can return later and create the order from the CONFIRMED cart. CONFIRMED carts without orders are valid (user may abandon checkout).

**Trade-off**: No explicit cart cleanup means CONFIRMED carts accumulate in database  
**Acceptance**: This is historical data and acceptable for an e-commerce platform. Future optimization: add background job to archive carts older than 30 days.

**Trade-off**: "Buy Now" still requires two API calls if product not in cart  
**Acceptance**: This is acceptable latency. The alternative (backend supporting single-call checkout) would require significant API redesign.
