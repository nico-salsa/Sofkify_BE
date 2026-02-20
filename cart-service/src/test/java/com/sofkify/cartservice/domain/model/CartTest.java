package com.sofkify.cartservice.domain.model;

import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.ports.out.StockValidationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Cart Aggregate Tests")
class CartTest {

    @Mock
    private StockValidationPort stockValidationPort;

    private Cart cart;
    private UUID cartId;
    private UUID customerId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        productId = UUID.randomUUID();
        cart = new Cart(cartId, customerId);
    }

    @Test
    @DisplayName("Should confirm cart when stock is available")
    void shouldConfirmCartWhenStockIsAvailable() {
        // Arrange
        cart.addItem(productId, "Test Product", BigDecimal.valueOf(99.99), 2);
        when(stockValidationPort.isStockAvailable(productId, 2)).thenReturn(true);

        // Act
        cart.confirm(stockValidationPort);

        // Assert
        assertEquals(CartStatus.CONFIRMED, cart.getStatus());
        verify(stockValidationPort, times(1)).isStockAvailable(productId, 2);
    }

    @Test
    @DisplayName("Should throw exception when confirming cart with insufficient stock")
    void shouldThrowExceptionWhenConfirmingCartWithInsufficientStock() {
        // Arrange
        cart.addItem(productId, "Test Product", BigDecimal.valueOf(99.99), 5);
        when(stockValidationPort.isStockAvailable(productId, 5)).thenReturn(false);

        // Act & Assert
        CartException exception = assertThrows(CartException.class, 
            () -> cart.confirm(stockValidationPort));
        
        assertEquals("Stock not available for product: " + productId, exception.getMessage());
        assertEquals(CartStatus.ACTIVE, cart.getStatus()); // Status should remain ACTIVE
    }

    @Test
    @DisplayName("Should throw exception when confirming already confirmed cart")
    void shouldThrowExceptionWhenConfirmingAlreadyConfirmedCart() {
        // Arrange
        cart.addItem(productId, "Test Product", BigDecimal.valueOf(99.99), 1);
        when(stockValidationPort.isStockAvailable(productId, 1)).thenReturn(true);
        cart.confirm(stockValidationPort); // First confirmation

        // Act & Assert
        CartException exception = assertThrows(CartException.class,
            () -> cart.confirm(stockValidationPort));
        
        assertEquals("Cart is already confirmed", exception.getMessage());
        assertEquals(CartStatus.CONFIRMED, cart.getStatus());
    }

    @Test
    @DisplayName("Should validate all items when confirming cart with multiple items")
    void shouldValidateAllItemsWhenConfirmingCartWithMultipleItems() {
        // Arrange
        UUID productId2 = UUID.randomUUID();
        cart.addItem(productId, "Product 1", BigDecimal.valueOf(50.00), 3);
        cart.addItem(productId2, "Product 2", BigDecimal.valueOf(25.00), 1);
        
        when(stockValidationPort.isStockAvailable(productId, 3)).thenReturn(true);
        when(stockValidationPort.isStockAvailable(productId2, 1)).thenReturn(true);

        // Act
        cart.confirm(stockValidationPort);

        // Assert
        assertEquals(CartStatus.CONFIRMED, cart.getStatus());
        verify(stockValidationPort).isStockAvailable(productId, 3);
        verify(stockValidationPort).isStockAvailable(productId2, 1);
    }
}