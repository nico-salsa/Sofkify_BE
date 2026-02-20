package com.sofkify.cartservice.domain.ports.out;

import com.sofkify.cartservice.domain.model.Cart;
import java.util.Optional;
import java.util.UUID;

/**
 * Port para persistencia de carritos.
 * Abstrae el acceso a datos de carritos.
 */
public interface CartRepository {
    
    /**
     * Busca un carrito por su ID.
     * 
     * @param cartId el identificador del carrito
     * @return el carrito si existe, Optional.empty() en caso contrario
     */
    Optional<Cart> findById(UUID cartId);
    
    /**
     * Guarda un carrito.
     * 
     * @param cart el carrito a guardar
     * @return el carrito guardado
     */
    Cart save(Cart cart);
    
    /**
     * Busca un carrito activo por customer ID.
     * 
     * @param customerId el identificador del customer
     * @return el carrito activo si existe, Optional.empty() en caso contrario
     */
    Optional<Cart> findActiveByCustomerId(UUID customerId);
}