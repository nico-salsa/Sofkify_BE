package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartItem;
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

@DisplayName("RemoveItemFromCartService Tests")
class RemoveItemFromCartServiceTest {

    @Mock
    private CartRepositoryPort cartRepository;

    private RemoveItemFromCartService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RemoveItemFromCartService(cartRepository);
    }

    @Test
    @DisplayName("Should remove item from cart successfully")
    void shouldRemoveItemFromCart() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Cart cart = new Cart(UUID.randomUUID(), customerId);
        cart.addItem(productId, "Product A", new BigDecimal("10.00"), 2);
        UUID cartItemId = cart.getItems().get(0).getId();

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        Cart result = service.removeItemFromCart(customerId, cartItemId);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw when cart not found for customer")
    void shouldThrowWhenCartNotFound() {
        UUID customerId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        CartException ex = assertThrows(CartException.class,
            () -> service.removeItemFromCart(customerId, cartItemId));
        assertTrue(ex.getMessage().contains("Cart not found"));
    }

    @Test
    @DisplayName("Should throw when cart item not found")
    void shouldThrowWhenCartItemNotFound() {
        UUID customerId = UUID.randomUUID();
        UUID nonExistentItemId = UUID.randomUUID();

        Cart cart = new Cart(UUID.randomUUID(), customerId);
        cart.addItem(UUID.randomUUID(), "Product", new BigDecimal("5.00"), 1);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        CartException ex = assertThrows(CartException.class,
            () -> service.removeItemFromCart(customerId, nonExistentItemId));
        assertTrue(ex.getMessage().contains("Cart item not found"));
    }
}
