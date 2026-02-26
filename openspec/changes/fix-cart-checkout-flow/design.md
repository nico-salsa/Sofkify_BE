## Context

The Sofkify e-commerce platform has 4 microservices (user, product, cart, order) orchestrated via REST APIs. The frontend (React SPA on localhost:5173) interacts with cart-service (port 8083) and order-service (port 8082). Cart state flows through: ACTIVE → CONFIRMED → order created.

**Current cart creation flow**: The first `POST /api/carts/items` for a customer automatically creates a cart if none exists (see `AddItemToCartService.addItem()`). No explicit "create cart" endpoint exists.

**Current cart state management**: Carts remain CONFIRMED indefinitely after confirmation. The backend correctly rejects duplicate confirmation attempts (400 "Cart is already confirmed"). The frontend incorrectly retries confirmation on the same cart instead of creating a new one.

**🚨 CRITICAL BUG FOUND IN BACKEND (Code Analysis Results)**:
- `AddItemToCartService.addItem()` uses `cartRepository.findByCustomerId(customerId)` WITHOUT filtering by status
- When a user has a CONFIRMED cart and adds items for a second purchase, the backend RETURNS THE CONFIRMED CART
- `Cart.addItem()` domain method does NOT validate cart status before adding items
- This allows items to be added to CONFIRMED carts, which then fail on re-confirmation
- **Root cause**: Missing filter for CartStatus.ACTIVE in repository query

**Affected files** (Backend):
- `AddItemToCartService.java` line 67
- `CartJpaRepository.java` - missing `findByCustomerIdAndStatus` method
- `Cart.java` addItem() - missing status validation

**Constraints**:
- Hexagonal architecture and DDD principles must be preserved
- Add minimal API surface changes
- Frontend must handle backendCartId tracking for robust state management

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

**Chosen**: Backend filters by ACTIVE status + Frontend tracks backendCartId

**Rationale**: Code analysis revealed the backend bug: `AddItemToCartService` returns CONFIRMED carts when searching by customerId. This causes the "Cart is already confirmed" error on second purchases.

**Backend fix (REQUIRED)**:
1. Add `findByCustomerIdAndStatus(UUID customerId, CartStatus status)` to repository layer
2. Modify `AddItemToCartService.addItem()` to search for ACTIVE carts only:
   ```java
   Cart cart = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
           .orElseGet(() -> new Cart(UUID.randomUUID(), customerId));
   ```
3. (Optional but recommended) Add defensive validation in `Cart.addItem()`:
   ```java
   if (this.status == CartStatus.CONFIRMED) {
       throw new IllegalStateException("Cannot add items to a confirmed cart");
   }
   ```

**Frontend enhancement (REQUIRED)**:
1. Track `backendCartId` in CartContext state (not just localStorage items)
2. After order creation, clear `backendCartId` from state
3. Remove "materialization loop" in `useCartConfirmation` (items are already in backend)

**Alternatives considered**:
- **Alt A**: Frontend calls new `POST /api/carts` to create empty cart → Rejected: unnecessary complexity, auto-creation works fine with ACTIVE filter
- **Alt B**: Backend auto-archives CONFIRMED carts on next addItem → Rejected: violates single responsibility, implicit state transitions are confusing
- **Alt C**: Backend allows adding items to CONFIRMED carts → Rejected: violates domain model integrity

**Implementation**:
- Backend: 3 files (CartJpaRepository, CartRepositoryAdapter, AddItemToCartService)
- Frontend: 2 files (CartContext, useCartConfirmation)

---

### Decision 2: "Buy Now" Workflow

**Chosen**: Remove "materialization loop" + use getActiveCart to find backendCartId

**Rationale**: Code analysis of `useCartConfirmation.ts` revealed a critical bug: lines 53-56 execute a loop that calls `cartApi.addItem()` for every item in the local cart. This happens AFTER the user already added items via "Add to Cart" or "Buy Now" buttons, causing duplication.

**Current problematic flow**:
1. User clicks "Buy Now" → `handleAddToCart()` → `CartContext.addItem()` → `cartApi.addItemToCart()` ✅ Items added to backend
2. Navigate to `/cart` → User clicks "Confirm Cart"
3. `useCartConfirmation.confirmCart()` executes:
   ```typescript
   // 🐛 BUG: Re-adds all items to backend cart
   for (const item of items) {
     const cartResponse = await cartApi.addItem(userId, item.productId, item.quantity);
   }
   ```
4. Backend receives duplicate addItem calls → products duplicated

**Fixed flow**:
1. User clicks "Buy Now" → `handleAddToCart()` → `CartContext.addItem()` → `cartApi.addItemToCart()` ✅ Items added to backend
2. Navigate to `/cart` → User clicks "Confirm Cart"
3. `useCartConfirmation.confirmCart()` executes:
   ```typescript
   // ✅ FIX: Get existing backend cart, don't re-add items
   const backendCart = await cartApi.getActiveCart(userId);
   const confirmResponse = await cartApi.confirmCart(backendCart.id, userId);
   ```

**Alternatives considered**:
- **Alt A**: "Buy Now" checks if product in cart before adding → Rejected: doesn't fix the root cause (materialization loop)
- **Alt B**: Backend deduplicates items on addItem → Rejected: violates idempotency, adds complexity

**Implementation**:
- Frontend: Remove lines 53-56 in `useCartConfirmation.ts`, replace with `getActiveCart()` call
- Backend: No changes required for this fix

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
