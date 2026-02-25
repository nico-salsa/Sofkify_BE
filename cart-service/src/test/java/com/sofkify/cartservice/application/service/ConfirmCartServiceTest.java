package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.application.dto.ConfirmCartRequest;
import com.sofkify.cartservice.application.dto.ConfirmCartResponse;
import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
import com.sofkify.cartservice.domain.ports.out.ProductServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ConfirmCartService Tests")
class ConfirmCartServiceTest {

    @Mock
    private CartRepositoryPort cartRepository;

    @Mock
    private ProductServicePort productServicePort;

    private ConfirmCartService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ConfirmCartService(cartRepository, productServicePort);
    }

    @Test
    @DisplayName("Should confirm cart successfully when cart exists and customer matches")
    void shouldConfirmCartSuccessfully() {
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Cart cart = new Cart(cartId, customerId);
        cart.addItem(productId, "Test Product", new BigDecimal("10.00"), 2);

        ConfirmCartRequest request = new ConfirmCartRequest(cartId, customerId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productServicePort.validateStock(productId, 2)).thenReturn(true);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ConfirmCartResponse response = service.execute(request);

        assertNotNull(response);
        assertEquals(cartId, response.getCartId());
        assertTrue(response.isSuccess());

        verify(cartRepository).findById(cartId);
        verify(cartRepository).save(any(Cart.class));
        verify(productServicePort).validateStock(productId, 2);
    }

    @Test
    @DisplayName("Should fail when cart does not exist")
    void shouldFailWhenCartDoesNotExist() {
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        ConfirmCartRequest request = new ConfirmCartRequest(cartId, customerId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(CartException.class, () -> service.execute(request));
    }

    @Test
    @DisplayName("Should fail when customer ID does not match")
    void shouldFailWhenCustomerDoesNotMatch() {
        UUID cartId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID differentCustomerId = UUID.randomUUID();

        Cart cart = new Cart(cartId, ownerId);
        ConfirmCartRequest request = new ConfirmCartRequest(cartId, differentCustomerId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        assertThrows(CartException.class, () -> service.execute(request));
    }
}
