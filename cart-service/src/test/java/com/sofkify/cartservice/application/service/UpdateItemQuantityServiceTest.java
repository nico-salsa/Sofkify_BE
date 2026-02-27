package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
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
import static org.mockito.Mockito.*;

@DisplayName("UpdateItemQuantityService Tests")
class UpdateItemQuantityServiceTest {

    @Mock
    private CartRepositoryPort cartRepository;

    private UpdateItemQuantityService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UpdateItemQuantityService(cartRepository);
    }

    @Test
    @DisplayName("Should update item quantity successfully")
    void shouldUpdateItemQuantity() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Cart cart = new Cart(UUID.randomUUID(), customerId);
        cart.addItem(productId, "Product", new BigDecimal("10.00"), 2);
        UUID cartItemId = cart.getItems().get(0).getId();

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        Cart result = service.updateItemQuantity(customerId, cartItemId, 5);

        assertNotNull(result);
        assertEquals(5, result.getItems().get(0).getQuantity());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw when cart not found")
    void shouldThrowWhenCartNotFound() {
        UUID customerId = UUID.randomUUID();

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        CartException ex = assertThrows(CartException.class,
            () -> service.updateItemQuantity(customerId, UUID.randomUUID(), 3));
        assertTrue(ex.getMessage().contains("Cart not found"));
    }

    @Test
    @DisplayName("Should throw when cart item not found")
    void shouldThrowWhenCartItemNotFound() {
        UUID customerId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), customerId);
        cart.addItem(UUID.randomUUID(), "P", new BigDecimal("5.00"), 1);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        CartException ex = assertThrows(CartException.class,
            () -> service.updateItemQuantity(customerId, UUID.randomUUID(), 3));
        assertTrue(ex.getMessage().contains("Cart item not found"));
    }

    @Test
    @DisplayName("Should throw when new quantity is invalid")
    void shouldThrowWhenNewQuantityIsInvalid() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Cart cart = new Cart(UUID.randomUUID(), customerId);
        cart.addItem(productId, "P", new BigDecimal("5.00"), 1);
        UUID cartItemId = cart.getItems().get(0).getId();

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        assertThrows(IllegalArgumentException.class,
            () -> service.updateItemQuantity(customerId, cartItemId, 0));
    }
}
