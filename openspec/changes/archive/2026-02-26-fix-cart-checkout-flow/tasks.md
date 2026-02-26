## Repository Context

**Backend Repository**: `C:\Sofkify\Sofkify_BE` (https://github.com/nico-salsa/Sofkify_BE.git)
- Branch: `fix/cart-checkout-flow`
- Changes: **CRITICAL FIX REQUIRED** - AddItemToCartService returns CONFIRMED carts (root cause of bug #2)

**Frontend Repository**: `C:\Sofkify\sofkify-fe` (https://github.com/nico-salsa/sofkify-fe.git)
- Branch: `fix/cart-checkout-flow`
- Changes: Remove materialization loop, track backendCartId, improve state management

---

## 1. Repository Setup

- [ ] 1.1 Backend branch confirmed (C:\Sofkify\Sofkify_BE on fix/cart-checkout-flow)
- [ ] 1.2 Frontend branch confirmed (C:\Sofkify\sofkify-fe on fix/cart-checkout-flow)

## 2. Backend Critical Fix - Filter ACTIVE Carts Only (Backend Repo: Sofkify_BE)

- [ ] 2.1 Add `findByCustomerIdAndStatus` method to `CartJpaRepository` interface
- [ ] 2.2 Add `findByCustomerIdAndStatus` method to `CartRepositoryPort` interface
- [ ] 2.3 Implement `findByCustomerIdAndStatus` in `CartRepositoryAdapter`
- [ ] 2.4 Modify `AddItemToCartService` to use `findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)` instead of `findByCustomerId`
- [ ] 2.5 (Optional) Add defensive validation in `Cart.addItem()`: throw exception if status is CONFIRMED

## 3. Frontend - Remove "Buy Now" Duplication (Frontend Repo: sofkify-fe)

- [ ] 3.1 Remove "materialization loop" (lines 53-56) from `useCartConfirmation.ts`
- [ ] 3.2 Replace materialization with `cartApi.getActiveCart(userId)` call to fetch backendCartId
- [ ] 3.3 Use the fetched `backendCart.id` directly in `confirmCart()` call

## 4. Frontend - Track Backend Cart ID (Frontend Repo: sofkify-fe)

- [ ] 4.1 Add `backendCartId: string | null` to CartContext interface (`cart-context.ts`)
- [ ] 4.2 Add `backendCartId` state in `CartContext.tsx` provider
- [ ] 4.3 Update `addItem()` to extract and save `cartResponse.id` from backend response
- [ ] 4.4 Update `clearCart()` to also reset `backendCartId` to null
- [ ] 4.5 (Optional) Use `backendCartId` from context in `useCartConfirmation` instead of fetching

## 5. Frontend - Post-Checkout State Cleanup (Frontend Repo: sofkify-fe)

- [ ] 5.1 Verify `clearCart()` is called after order creation in `CartConfirmationPage.tsx` (line 34)
- [ ] 5.2 Ensure navigation to `/order-success` happens after clearCart
- [ ] 5.3 Test that backendCartId is null after checkout completes

## 6. Frontend - Navigation Validation (Frontend Repo: sofkify-fe)

- [ ] 6.1 Verify "Continuar Comprando" button in `OrderSuccessPage.tsx` navigates to `/` correctly
- [ ] 6.2 Verify logo in `Header.tsx` has correct `<Link to="/">` 
- [ ] 6.3 Test navigation from order success page to home and back to shopping

## 7. Integration Testing (Both Repositories)

- [ ] 7.1 Start backend services with Docker Compose in backend repo
- [ ] 7.2 Start frontend with `npm run dev` in frontend repo (verify .env points to localhost:808X)
- [ ] 7.3 Test first purchase flow: add items → confirm cart → create order → verify cart cleared
- [ ] 7.4 **CRITICAL TEST**: Second purchase flow - add new items → verify NEW cart created (not CONFIRMED one)
- [ ] 7.5 Test "Buy Now" button: verify products are NOT duplicated in cart
- [ ] 7.6 Test cart confirmation with already-confirmed cart: verify graceful error handling instead of 400 error
- [ ] 7.7 Test post-checkout navigation: verify "Back to Store" and logo both work
- [ ] 7.8 Test cart state persistence across page reloads
- [ ] 7.9 Verify backend logs show new cart creation on second purchase (different cartId)
- [ ] 7.10 Test multi-tab scenario: confirm cart in tab 1, add items in tab 2, verify new cart created

## 8. Code Review & Merge Strategy

- [ ] 8.1 Review backend changes: verify repository layer follows hexagonal architecture
- [ ] 8.2 Review frontend changes: verify state management is clean and predictable
- [ ] 8.3 Merge backend `fix/cart-checkout-flow` → `develop` (Sofkify_BE repo)
- [ ] 8.4 Merge frontend `fix/cart-checkout-flow` → `develop` (sofkify-fe repo)
- [ ] 8.5 Test integration on develop branches
- [ ] 8.6 Merge both develop → main if all tests pass
