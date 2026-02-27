package com.sofkify.orderservice.infrastructure.mapper;

import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
import com.sofkify.orderservice.domain.model.OrderStatus;
import com.sofkify.orderservice.infrastructure.adapters.out.persistence.OrderItemJpaEntity;
import com.sofkify.orderservice.infrastructure.adapters.out.persistence.OrderJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderMapper Tests")
class OrderMapperTest {

    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderMapper();
    }

    @Test
    @DisplayName("Should map domain Order to JPA entity")
    void shouldMapToJpaEntity() {
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Product A", BigDecimal.valueOf(10.00), 2);
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(item));

        OrderJpaEntity entity = mapper.toJpaEntity(order);

        assertEquals(order.getId(), entity.getId());
        assertEquals(order.getCartId(), entity.getCartId());
        assertEquals(order.getCustomerId(), entity.getCustomerId());
        assertEquals(order.getStatus(), entity.getStatus());
        assertEquals(order.getTotalAmount(), entity.getTotalAmount());
        assertNotNull(entity.getItems());
        assertEquals(1, entity.getItems().size());
        assertSame(entity, entity.getItems().get(0).getOrder());
    }

    @Test
    @DisplayName("Should map OrderItem domain to JPA entity")
    void shouldMapItemToJpaEntity() {
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "P", BigDecimal.valueOf(5.00), 3);

        OrderItemJpaEntity entity = mapper.toJpaEntity(item);

        assertEquals(item.getId(), entity.getId());
        assertEquals(item.getProductId(), entity.getProductId());
        assertEquals(item.getProductName(), entity.getProductName());
        assertEquals(item.getUnitPrice().amount(), entity.getProductPrice());
        assertEquals(item.getQuantity().value(), entity.getQuantity());
        assertEquals(item.getSubtotal().amount(), entity.getSubtotal());
    }

    @Test
    @DisplayName("Should map JPA entity to domain Order")
    void shouldMapToDomainModel() {
        OrderItemJpaEntity itemEntity = new OrderItemJpaEntity();
        itemEntity.setId(UUID.randomUUID());
        itemEntity.setProductId(UUID.randomUUID());
        itemEntity.setProductName("Test");
        itemEntity.setProductPrice(BigDecimal.valueOf(10.00));
        itemEntity.setQuantity(2);

        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setCartId(UUID.randomUUID());
        entity.setCustomerId(UUID.randomUUID());
        entity.setStatus(OrderStatus.PENDING);
        entity.setItems(List.of(itemEntity));

        Order order = mapper.toDomainModel(entity);

        assertEquals(entity.getId(), order.getId());
        assertEquals(entity.getCartId(), order.getCartId());
        assertEquals(entity.getCustomerId(), order.getCustomerId());
        assertEquals(1, order.getItems().size());
    }

    @Test
    @DisplayName("Should map JPA item entity to domain OrderItem")
    void shouldMapItemToDomainModel() {
        OrderItemJpaEntity itemEntity = new OrderItemJpaEntity();
        itemEntity.setId(UUID.randomUUID());
        itemEntity.setProductId(UUID.randomUUID());
        itemEntity.setProductName("Test Product");
        itemEntity.setProductPrice(BigDecimal.valueOf(15.00));
        itemEntity.setQuantity(4);

        OrderItem item = mapper.toDomainModel(itemEntity);

        assertEquals(itemEntity.getId(), item.getId());
        assertEquals(itemEntity.getProductId(), item.getProductId());
        assertEquals(itemEntity.getProductName(), item.getProductName());
    }
}
