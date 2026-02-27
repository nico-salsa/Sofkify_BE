package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.dto.OrderCreatedEventDTO;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.exception.InsufficientStockException;
import com.sofkify.productservice.domain.exception.ProductNotFoundException;
import com.sofkify.productservice.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("StockDecrementService Tests")
class StockDecrementServiceTest {

    @Mock private ProductPersistencePort persistencePort;
    private StockDecrementService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new StockDecrementService(persistencePort);
    }

    private OrderCreatedEventDTO createEvent(UUID productId, int quantity) {
        OrderCreatedEventDTO.OrderItemEventDTO item = new OrderCreatedEventDTO.OrderItemEventDTO(
            productId, "Product", quantity, BigDecimal.TEN, BigDecimal.valueOf(quantity * 10)
        );
        return new OrderCreatedEventDTO(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            List.of(item), BigDecimal.valueOf(quantity * 10), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should decrement stock for order items")
    void shouldDecrementStock() {
        UUID productId = UUID.randomUUID();
        Product product = Product.reconstitute(productId, "P", "D", "SKU", BigDecimal.TEN, 20, ProductStatus.ACTIVE);
        OrderCreatedEventDTO event = createEvent(productId, 5);

        when(persistencePort.findById(productId)).thenReturn(Optional.of(product));
        when(persistencePort.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        service.handleOrderCreated(event);

        assertEquals(15, product.getStock());
        verify(persistencePort).save(product);
    }

    @Test
    @DisplayName("Should throw when product not found")
    void shouldThrowProductNotFound() {
        UUID productId = UUID.randomUUID();
        OrderCreatedEventDTO event = createEvent(productId, 1);
        when(persistencePort.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.handleOrderCreated(event));
    }

    @Test
    @DisplayName("Should throw when insufficient stock")
    void shouldThrowInsufficientStock() {
        UUID productId = UUID.randomUUID();
        Product product = Product.reconstitute(productId, "P", "D", "SKU", BigDecimal.TEN, 2, ProductStatus.ACTIVE);
        OrderCreatedEventDTO event = createEvent(productId, 10);

        when(persistencePort.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () -> service.handleOrderCreated(event));
    }
}
