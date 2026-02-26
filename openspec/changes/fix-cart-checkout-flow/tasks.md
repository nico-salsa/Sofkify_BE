## Repository Context

**Backend Repository**: `C:\Sofkify\Sofkify_BE` (https://github.com/nico-salsa/Sofkify_BE.git)
- Branch: `fix/cart-checkout-flow`
- Changes: Verification only, no API contract changes needed

**Frontend Repository**: `C:\Sofkify\sofkify-fe` (https://github.com/nico-salsa/sofkify-fe.git)
- Branch: `fix/cart-checkout-flow` (to be created)
- Changes: All implementation work happens here

---

## 1. Repository Setup

- [ ] 1.1 Create branch `fix/cart-checkout-flow` in frontend repository (C:\Sofkify\sofkify-fe)
- [ ] 1.2 Verify backend branch exists and is current (C:\Sofkify\Sofkify_BE)

## 2. Cart Lifecycle Management (Frontend Repo: sofkify-fe)

- [ ] 2.1 Implement cart state clearing after order creation (clear `currentCartId` from localStorage/context after `POST /api/orders/from-cart/{cartId}` success)
- [ ] 2.2 Add cart status tracking in frontend state (ACTIVE, CONFIRMED, CLEARED) with transitions
- [ ] 2.3 Implement cart state persistence across page reloads (restore from localStorage or re-fetch via `GET /api/carts` on mount)
- [ ] 2.4 Add UI state management to disable cart modifications when status is CONFIRMED (disable add/remove/update buttons)
- [ ] 2.5 Implement automatic new cart initialization on first item add after previous cart confirmation (verify backend auto-creates cart on `POST /api/carts/items`)

## 3. "Buy Now" Workflow Fix (Frontend Repo: sofkify-fe)

- [ ] 3.1 Refactor "Buy Now" button click handler to check if product is already in cart with selected quantity
- [ ] 3.2 Implement conditional logic: if product NOT in cart, call `POST /api/carts/items` before `POST /api/carts/{cartId}/confirm`
- [ ] 3.3 Implement conditional logic: if product ALREADY in cart at exact quantity, skip add and directly call `POST /api/carts/{cartId}/confirm`
- [ ] 3.4 Implement quantity update logic: if product in cart at different quantity, call `PUT /api/carts/items/{cartItemId}` before confirmation
- [ ] 3.5 Add navigation to checkout page after successful cart confirmation (redirect to `/checkout` or `/order` with cartId)
- [ ] 3.6 Add error handling for confirmation failures (display backend error message without clearing cart)

## 4. Confirm Cart Error Handling (Frontend Repo: sofkify-fe)

- [ ] 4.1 Implement 400 error detection for "Cart is already confirmed" response from `POST /api/carts/{cartId}/confirm`
- [ ] 4.2 Update cart status to CONFIRMED in state when detecting duplicate confirmation error
- [ ] 4.3 Redirect user to order creation flow when cart is detected as already confirmed
- [ ] 4.4 Add UI messaging for confirmed carts (e.g., "Cart confirmed - proceed to create order")
- [ ] 4.5 Implement stale state recovery: re-fetch cart on backend 400/409 errors during cart modifications

## 5. Post-Checkout Navigation (Frontend Repo: sofkify-fe)

- [ ] 5.1 Add "Back to Store" or "Continue Shopping" button on order confirmation/success page
- [ ] 5.2 Implement navigation to product catalog on button click (route to `/products` or `/`)
- [ ] 5.3 Fix logo click handler on checkout page to navigate to home/product catalog
- [ ] 5.4 Clear checkout-specific state (cart reference, checkout steps) on navigation away from checkout pages

## 6. Backend Verification (Backend Repo: Sofkify_BE)

- [ ] 6.1 Verify cart auto-creation behavior: confirm `POST /api/carts/items` creates new cart if none exists for customer (check `AddItemToCartService.addItem()` implementation)
- [ ] 6.2 Verify no new backend endpoints are needed (confirm existing API contracts are sufficient)

## 7. Integration Testing (Both Repositories)

- [ ] 7.1 Start backend services with Docker Compose in backend repo
- [ ] 7.2 Start frontend with `npm run dev` in frontend repo
- [ ] 7.3 Test complete purchase flow: add items → confirm cart → create order → verify cart cleared
- [ ] 7.4 Test repeat purchase flow: after order creation, add new item → verify new cart is created
- [ ] 7.5 Test "Buy Now" with product not in cart (verify no duplication)
- [ ] 7.6 Test "Buy Now" with product already in cart (verify no duplication)
- [ ] 7.7 Test cart confirmation error recovery (attempt duplicate confirmation → verify graceful handling)
- [ ] 7.8 Test post-checkout navigation (click "Back to Store" and logo → verify both work)
- [ ] 7.9 Test cart state persistence across page reloads (add items, reload, verify cart restored)
- [ ] 7.10 Test multi-tab scenario (confirm cart in one tab, verify other tab detects state change)

## 8. Merge Strategy

- [ ] 8.1 Merge frontend `fix/cart-checkout-flow` → `develop` (sofkify-fe repo)
- [ ] 8.2 Merge backend `fix/cart-checkout-flow` → `develop` (Sofkify_BE repo)
- [ ] 8.3 Test integration on develop branches
- [ ] 8.4 Merge both develop → main if all tests pass
