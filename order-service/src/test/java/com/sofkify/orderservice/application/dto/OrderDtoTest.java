package com.sofkify.orderservice.application.dto;

import com.sofkify.orderservice.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order DTO Tests")
class OrderDtoTest {

    @Test
    @DisplayName("CreateOrderRequest should hold values and validate")
    void createOrderRequest() {
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CreateOrderRequest req = new CreateOrderRequest(cartId, customerId);

        assertEquals(cartId, req.getCartId());
        assertEquals(customerId, req.getCustomerId());
        assertEquals(req, new CreateOrderRequest(cartId, customerId));
        assertEquals(req.hashCode(), new CreateOrderRequest(cartId, customerId).hashCode());
        assertNotNull(req.toString());
    }

    @Test
    @DisplayName("CreateOrderRequest should reject nulls")
    void createOrderRequestRejectsNulls() {
        assertThrows(NullPointerException.class, () -> new CreateOrderRequest(null, UUID.randomUUID()));
        assertThrows(NullPointerException.class, () -> new CreateOrderRequest(UUID.randomUUID(), null));
    }

    @Test
    @DisplayName("CreateOrderResponse should hold values")
    void createOrderResponse() {
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        CreateOrderResponse resp = new CreateOrderResponse(orderId, cartId, true, "ok");

        assertEquals(orderId, resp.getOrderId());
        assertEquals(cartId, resp.getCartId());
        assertTrue(resp.isSuccess());
        assertEquals("ok", resp.getMessage());
        assertNotNull(resp.getCreatedAt());
        assertNotNull(resp.toString());
    }

    @Test
    @DisplayName("CreateOrderResponse failure has null createdAt")
    void createOrderResponseFailure() {
        CreateOrderResponse resp = new CreateOrderResponse(null, UUID.randomUUID(), false, "fail");
        assertNull(resp.getOrderId());
        assertFalse(resp.isSuccess());
        assertNull(resp.getCreatedAt());
    }

    @Test
    @DisplayName("CreateOrderResponse equals and hashCode")
    void createOrderResponseEquality() {
        UUID id = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        CreateOrderResponse a = new CreateOrderResponse(id, cartId, true, "ok");
        CreateOrderResponse b = new CreateOrderResponse(id, cartId, true, "ok");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    @DisplayName("OrderResponse record should hold values")
    void orderResponse() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        OrderItemResponse item = new OrderItemResponse(
            UUID.randomUUID(), UUID.randomUUID(), "P", BigDecimal.ONE, 1, BigDecimal.ONE, now
        );
        OrderResponse resp = new OrderResponse(id, UUID.randomUUID(), UUID.randomUUID(),
            "PENDING", List.of(item), BigDecimal.ONE, now);

        assertEquals(id, resp.id());
        assertEquals("PENDING", resp.status());
        assertEquals(1, resp.items().size());
    }

    @Test
    @DisplayName("OrderItemResponse record should hold values")
    void orderItemResponse() {
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        OrderItemResponse item = new OrderItemResponse(id, productId, "P", BigDecimal.TEN, 2, BigDecimal.valueOf(20), now);

        assertEquals(id, item.id());
        assertEquals(productId, item.productId());
        assertEquals("P", item.productName());
        assertEquals(2, item.quantity());
    }

    @Test
    @DisplayName("UpdateOrderStatusRequest record should hold value")
    void updateOrderStatusRequest() {
        UpdateOrderStatusRequest req = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, req.status());
    }

    @Test
    @DisplayName("CartResponse record should hold values")
    void cartResponse() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        CartResponse cart = new CartResponse(id, UUID.randomUUID(), "CONFIRMED", List.of(),
            BigDecimal.ZERO, now, now);

        assertEquals(id, cart.id());
        assertEquals("CONFIRMED", cart.status());
    }

    @Test
    @DisplayName("CartItemResponse record should hold values")
    void cartItemResponse() {
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        CartItemResponse item = new CartItemResponse(id, productId, "P", BigDecimal.TEN, 3,
            BigDecimal.valueOf(30), now, now);

        assertEquals(id, item.id());
        assertEquals(productId, item.productId());
        assertEquals("P", item.productName());
        assertEquals(3, item.quantity());
    }
}
