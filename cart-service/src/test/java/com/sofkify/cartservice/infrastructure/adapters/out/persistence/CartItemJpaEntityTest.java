package com.sofkify.cartservice.infrastructure.adapters.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartItemJpaEntity Tests")
class CartItemJpaEntityTest {

    @Test
    @DisplayName("Should create with default constructor and set all fields")
    void shouldCreateWithDefaultConstructor() {
        CartItemJpaEntity entity = new CartItemJpaEntity();
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        CartJpaEntity cart = new CartJpaEntity();

        entity.setId(id);
        entity.setCart(cart);
        entity.setProductId(productId);
        entity.setProductName("Widget");
        entity.setProductPrice(new BigDecimal("25.00"));
        entity.setQuantity(3);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertEquals(id, entity.getId());
        assertEquals(cart, entity.getCart());
        assertEquals(productId, entity.getProductId());
        assertEquals("Widget", entity.getProductName());
        assertEquals(new BigDecimal("25.00"), entity.getProductPrice());
        assertEquals(3, entity.getQuantity());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create with parameterized constructor")
    void shouldCreateWithParameterizedConstructor() {
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        CartJpaEntity cart = new CartJpaEntity();

        CartItemJpaEntity entity = new CartItemJpaEntity(
            id, cart, productId, "Gadget", new BigDecimal("15.50"), 2, now, now
        );

        assertEquals(id, entity.getId());
        assertEquals(cart, entity.getCart());
        assertEquals(productId, entity.getProductId());
        assertEquals("Gadget", entity.getProductName());
        assertEquals(new BigDecimal("15.50"), entity.getProductPrice());
        assertEquals(2, entity.getQuantity());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }
}
