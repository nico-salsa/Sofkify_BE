package com.sofkify.cartservice.domain.ports.out;

import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartStatus;

import java.util.Optional;
import java.util.UUID;

public interface CartRepositoryPort {
    
    Cart save(Cart cart);
    
    Optional<Cart> findByCustomerId(UUID customerId);
    
    Optional<Cart> findByCustomerIdAndStatus(UUID customerId, CartStatus status);
    
    Optional<Cart> findById(UUID cartId);
}
