package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartStatus;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
import com.sofkify.cartservice.domain.ports.out.ProductServicePort;
import com.sofkify.cartservice.domain.ports.out.UserServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("AddItemToCartService Tests")
class AddItemToCartServiceTest {

    @Mock
    private CartRepositoryPort cartRepository;

    @Mock
    private ProductServicePort productServicePort;

    @Mock
    private UserServicePort userServicePort;

    private AddItemToCartService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AddItemToCartService(cartRepository, productServicePort, userServicePort);
    }

    @Test
    @DisplayName("Should add item to existing active cart")
    void shouldAddItemToExistingActiveCart() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int quantity = 2;

        Cart existingCart = new Cart(UUID.randomUUID(), customerId);
        ProductServicePort.ProductInfo productInfo = new ProductServicePort.ProductInfo(
            productId, "Test Product", new BigDecimal("25.00"), 10, true
        );

        when(userServicePort.validateUser(customerId)).thenReturn(true);
        when(productServicePort.getProduct(productId)).thenReturn(productInfo);
        when(productServicePort.validateStock(productId, quantity)).thenReturn(true);
        when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
            .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        Cart result = service.addItem(customerId, productId, quantity);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(productId, result.getItems().get(0).getProductId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should create new cart when no active cart exists")
    void shouldCreateNewCartWhenNoActiveExists() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProductServicePort.ProductInfo productInfo = new ProductServicePort.ProductInfo(
            productId, "New Product", new BigDecimal("10.00"), 5, true
        );

        when(userServicePort.validateUser(customerId)).thenReturn(true);
        when(productServicePort.getProduct(productId)).thenReturn(productInfo);
        when(productServicePort.validateStock(productId, 1)).thenReturn(true);
        when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
            .thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        Cart result = service.addItem(customerId, productId, 1);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw when user is invalid")
    void shouldThrowWhenUserIsInvalid() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(userServicePort.validateUser(customerId)).thenReturn(false);

        CartException ex = assertThrows(CartException.class,
            () -> service.addItem(customerId, productId, 1));
        assertTrue(ex.getMessage().contains("Invalid or inactive customer"));
    }

    @Test
    @DisplayName("Should throw when product is not active")
    void shouldThrowWhenProductIsNotActive() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProductServicePort.ProductInfo inactiveProduct = new ProductServicePort.ProductInfo(
            productId, "Inactive", new BigDecimal("10.00"), 5, false
        );

        when(userServicePort.validateUser(customerId)).thenReturn(true);
        when(productServicePort.getProduct(productId)).thenReturn(inactiveProduct);

        CartException ex = assertThrows(CartException.class,
            () -> service.addItem(customerId, productId, 1));
        assertTrue(ex.getMessage().contains("Product is not active"));
    }

    @Test
    @DisplayName("Should throw when stock is insufficient")
    void shouldThrowWhenStockIsInsufficient() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProductServicePort.ProductInfo product = new ProductServicePort.ProductInfo(
            productId, "Product", new BigDecimal("10.00"), 1, true
        );

        when(userServicePort.validateUser(customerId)).thenReturn(true);
        when(productServicePort.getProduct(productId)).thenReturn(product);
        when(productServicePort.validateStock(productId, 100)).thenReturn(false);

        CartException ex = assertThrows(CartException.class,
            () -> service.addItem(customerId, productId, 100));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }
}
