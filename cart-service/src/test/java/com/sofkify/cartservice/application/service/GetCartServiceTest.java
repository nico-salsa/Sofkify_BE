package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartStatus;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GetCartService Tests")
class GetCartServiceTest {

    @Mock
    private CartRepositoryPort cartRepository;

    private GetCartService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GetCartService(cartRepository);
    }

    @Test
    @DisplayName("Should get cart by customer ID")
    void shouldGetCartByCustomerId() {
        UUID customerId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), customerId);

        when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
            .thenReturn(Optional.of(cart));

        Cart result = service.getCartByCustomerId(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        verify(cartRepository).findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throw when no active cart found for customer")
    void shouldThrowWhenNoActiveCartForCustomer() {
        UUID customerId = UUID.randomUUID();

        when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
            .thenReturn(Optional.empty());

        CartException ex = assertThrows(CartException.class,
            () -> service.getCartByCustomerId(customerId));
        assertTrue(ex.getMessage().contains("Active cart not found"));
    }

    @Test
    @DisplayName("Should get cart by cart ID")
    void shouldGetCartById() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        Cart result = service.getCartById(cartId);

        assertNotNull(result);
        assertEquals(cartId, result.getId());
        verify(cartRepository).findById(cartId);
    }

    @Test
    @DisplayName("Should throw when cart not found by ID")
    void shouldThrowWhenCartNotFoundById() {
        UUID cartId = UUID.randomUUID();

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        CartException ex = assertThrows(CartException.class,
            () -> service.getCartById(cartId));
        assertTrue(ex.getMessage().contains("Cart not found"));
    }
}
