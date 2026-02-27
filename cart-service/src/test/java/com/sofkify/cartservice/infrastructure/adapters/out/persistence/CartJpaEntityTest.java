package com.sofkify.cartservice.infrastructure.adapters.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartJpaEntity Tests")
class CartJpaEntityTest {

    @Test
    @DisplayName("Should create with default constructor and set all fields")
    void shouldCreateWithDefaultConstructor() {
        CartJpaEntity entity = new CartJpaEntity();
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(id);
        entity.setCustomerId(customerId);
        entity.setStatus(CartStatusJpa.ACTIVE);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setItems(List.of());

        assertEquals(id, entity.getId());
        assertEquals(customerId, entity.getCustomerId());
        assertEquals(CartStatusJpa.ACTIVE, entity.getStatus());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
        assertTrue(entity.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should create with parameterized constructor")
    void shouldCreateWithParameterizedConstructor() {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CartJpaEntity entity = new CartJpaEntity(id, customerId, CartStatusJpa.CONFIRMED, now, now);

        assertEquals(id, entity.getId());
        assertEquals(customerId, entity.getCustomerId());
        assertEquals(CartStatusJpa.CONFIRMED, entity.getStatus());
    }

    @Test
    @DisplayName("Should handle all CartStatusJpa values")
    void shouldHandleAllStatusValues() {
        CartJpaEntity entity = new CartJpaEntity();

        entity.setStatus(CartStatusJpa.ACTIVE);
        assertEquals(CartStatusJpa.ACTIVE, entity.getStatus());

        entity.setStatus(CartStatusJpa.CONFIRMED);
        assertEquals(CartStatusJpa.CONFIRMED, entity.getStatus());

        entity.setStatus(CartStatusJpa.EXPIRED);
        assertEquals(CartStatusJpa.EXPIRED, entity.getStatus());
    }
}
