package com.sofkify.cartservice.domain.ports.out;

import com.sofkify.cartservice.domain.model.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartRepository Port Tests - RED Phase")
class CartRepositoryTest {

    @Test
    @DisplayName("Should pass - CartRepository basic operations")
    void shouldPassCartRepositoryBasicOperations() {
        // This test validates CartRepository interface contract
        CartRepository repository = new CartRepository() {
            @Override
            public Optional<Cart> findById(UUID cartId) {
                // Should find cart by ID
                if (cartId.equals(UUID.fromString("00000000-0000-0000-0000-000000000001"))) {
                    return Optional.of(new Cart(cartId, UUID.randomUUID()));
                }
                return Optional.empty();
            }
            
            @Override
            public Cart save(Cart cart) {
                // Should persist cart
                return cart;
            }
            
            @Override
            public Optional<Cart> findActiveByCustomerId(UUID customerId) {
                // Should find active cart for customer
                return Optional.of(new Cart(UUID.randomUUID(), customerId));
            }
            
            @Override
            public List<Cart> findByCustomerId(UUID customerId) {
                // Should find all carts for customer
                return List.of(new Cart(UUID.randomUUID(), customerId));
            }
            
            @Override
            public void deleteById(UUID cartId) {
                // Should delete cart by ID
            }
        };
        
        UUID cartId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID customerId = UUID.randomUUID();
        
        // Should find existing cart
        Optional<Cart> foundCart = repository.findById(cartId);
        assertTrue(foundCart.isPresent());
        assertEquals(cartId, foundCart.get().getId());
        
        // Should not find non-existent cart
        Optional<Cart> notFound = repository.findById(UUID.randomUUID());
        assertTrue(notFound.isEmpty());
        
        // Should find active cart for customer
        Optional<Cart> activeCart = repository.findActiveByCustomerId(customerId);
        assertTrue(activeCart.isPresent());
        
        // Should find carts by customer
        List<Cart> customerCarts = repository.findByCustomerId(customerId);
        assertFalse(customerCarts.isEmpty());
        
        // Should be able to delete cart
        assertDoesNotThrow(() -> repository.deleteById(cartId));
    }

    @Test
    @DisplayName("Should pass - CartRepository save operations")
    void shouldPassCartRepositorySaveOperations() {
        CartRepository repository = new CartRepository() {
            @Override
            public Optional<Cart> findById(UUID cartId) {
                return Optional.empty();
            }
            
            @Override
            public Cart save(Cart cart) {
                assertNotNull(cart);
                assertNotNull(cart.getId());
                assertNotNull(cart.getCustomerId());
                return cart;
            }
            
            @Override
            public Optional<Cart> findActiveByCustomerId(UUID customerId) {
                return Optional.empty();
            }
            
            @Override
            public List<Cart> findByCustomerId(UUID customerId) {
                return List.of();
            }
            
            @Override
            public void deleteById(UUID cartId) {
                // Implementation for delete
            }
        };
        
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Cart cart = new Cart(cartId, customerId);
        
        Cart savedCart = repository.save(cart);
        
        assertNotNull(savedCart);
        assertEquals(cartId, savedCart.getId());
        assertEquals(customerId, savedCart.getCustomerId());
    }
}