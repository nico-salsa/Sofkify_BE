## Why

The shopping cart checkout flow has critical UX issues preventing repeat purchases and causing product duplication. Users can only complete ONE purchase per session before encountering "Cart is already confirmed" errors. The "Buy Now" button duplicates products, navigation breaks post-checkout, and there's no way to clear confirmed carts or start a new purchase flow.

## What Changes

- **New cart lifecycle behavior**: Frontend SHALL create a new cart after confirming the previous one, enabling multiple purchases per session
- **Fix "Buy Now" duplication**: "Buy Now" button SHALL only confirm the current cart WITHOUT adding products a second time
- **Add cart cleanup mechanism**: After order creation, the frontend SHALL clear the confirmed cart state and initialize a fresh cart for the next purchase
- **Fix post-checkout navigation**: "Back to Store" button and logo SHALL navigate correctly to the product catalog
- **Improve cart state handling**: Frontend SHALL handle CONFIRMED cart state gracefully and prevent re-confirmation attempts

## Capabilities

### New Capabilities
- `cart-lifecycle-management`: Defines how carts transition through states (ACTIVE → CONFIRMED → archived/replaced) and how the frontend manages multiple purchase sessions
- `buy-now-workflow`: Specifies the correct behavior for "Buy Now" vs "Add to Cart" buttons to prevent product duplication

### Modified Capabilities
- `confirm-cart`: Add frontend behavior requirements for handling already-confirmed carts and preventing duplicate confirmation attempts
- `frontend-backend-runtime-integration`: Extend navigation requirements for post-checkout flows (back to store, logo navigation)

## Impact

**Frontend (Primary Impact)**:
- Cart state management logic
- Button click handlers ("Buy Now", "Add to Cart", "Confirm Purchase")
- Navigation after checkout
- Cart initialization/cleanup lifecycle

**Backend (Minimal Impact)**:
- No API contract changes required (current behavior is correct)
- Possible new endpoint: `POST /api/carts` to create new empty carts if not already implemented

**User Experience**:
- Users can complete multiple purchases without errors
- No product duplication on "Buy Now"
- Clear navigation flow: catalog → cart → checkout → back to catalog
- Predictable cart behavior across sessions
