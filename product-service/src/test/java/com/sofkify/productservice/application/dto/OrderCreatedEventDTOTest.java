package com.sofkify.productservice.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderCreatedEventDTO Tests")
class OrderCreatedEventDTOTest {

    @Test
    @DisplayName("Should hold all values")
    void shouldHoldValues() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        OrderCreatedEventDTO.OrderItemEventDTO item = new OrderCreatedEventDTO.OrderItemEventDTO(
            UUID.randomUUID(), "Product", 2, BigDecimal.TEN, BigDecimal.valueOf(20)
        );

        OrderCreatedEventDTO dto = new OrderCreatedEventDTO(
            orderId, customerId, cartId, List.of(item), BigDecimal.valueOf(20), now
        );

        assertEquals(orderId, dto.orderId());
        assertEquals(customerId, dto.customerId());
        assertEquals(cartId, dto.cartId());
        assertEquals(1, dto.items().size());
        assertEquals(BigDecimal.valueOf(20), dto.totalAmount());
        assertEquals(now, dto.createdAt());
    }

    @Test
    @DisplayName("OrderItemEventDTO should hold values")
    void orderItemShouldHoldValues() {
        UUID productId = UUID.randomUUID();
        OrderCreatedEventDTO.OrderItemEventDTO item = new OrderCreatedEventDTO.OrderItemEventDTO(
            productId, "P", 3, BigDecimal.ONE, BigDecimal.valueOf(3)
        );

        assertEquals(productId, item.productId());
        assertEquals("P", item.productName());
        assertEquals(3, item.quantity());
        assertEquals(BigDecimal.ONE, item.unitPrice());
        assertEquals(BigDecimal.valueOf(3), item.totalPrice());
    }
}
