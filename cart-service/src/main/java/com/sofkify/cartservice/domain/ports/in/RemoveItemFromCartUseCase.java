package com.sofkify.cartservice.domain.ports.in;

import com.sofkify.cartservice.domain.model.Cart;

import java.util.UUID;

public interface RemoveItemFromCartUseCase {
    
    Cart removeItemFromCart(UUID customerId, UUID cartItemId);
}
