package com.sofkify.orderservice.infrastructure.adapters.out.persistence;

import com.sofkify.orderservice.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order JPA Entity Tests")
class OrderJpaEntityTest {

    @Test
    @DisplayName("OrderJpaEntity getters and setters")
    void orderJpaEntityGettersSetters() {
        OrderJpaEntity entity = new OrderJpaEntity();
        UUID id = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(id);
        entity.setCartId(cartId);
        entity.setCustomerId(customerId);
        entity.setStatus(OrderStatus.PENDING);
        entity.setTotalAmount(BigDecimal.valueOf(100));
        entity.setTotalAmountField(BigDecimal.valueOf(100));
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setItems(List.of());

        assertEquals(id, entity.getId());
        assertEquals(cartId, entity.getCartId());
        assertEquals(customerId, entity.getCustomerId());
        assertEquals(OrderStatus.PENDING, entity.getStatus());
        assertEquals(BigDecimal.valueOf(100), entity.getTotalAmount());
        assertEquals(BigDecimal.valueOf(100), entity.getTotalAmountField());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
        assertTrue(entity.getItems().isEmpty());
    }

    @Test
    @DisplayName("OrderItemJpaEntity getters and setters")
    void orderItemJpaEntityGettersSetters() {
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(id);
        entity.setProductId(productId);
        entity.setProductName("Test");
        entity.setProductPrice(BigDecimal.valueOf(10));
        entity.setQuantity(3);
        entity.setSubtotal(BigDecimal.valueOf(30));
        entity.setTotalAmount(BigDecimal.valueOf(30));
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setOrder(new OrderJpaEntity());

        assertEquals(id, entity.getId());
        assertEquals(productId, entity.getProductId());
        assertEquals("Test", entity.getProductName());
        assertEquals(BigDecimal.valueOf(10), entity.getProductPrice());
        assertEquals(3, entity.getQuantity());
        assertEquals(BigDecimal.valueOf(30), entity.getSubtotal());
        assertEquals(BigDecimal.valueOf(30), entity.getTotalAmount());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
        assertNotNull(entity.getOrder());
    }
}
