package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.application.dto.ConfirmCartRequest;
import com.sofkify.cartservice.application.dto.ConfirmCartResponse;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartItem;
import com.sofkify.cartservice.domain.model.CartStatus;
import com.sofkify.cartservice.domain.event.CartConfirmedEvent;
import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.ports.out.CartRepository;
import com.sofkify.cartservice.domain.ports.out.StockValidationPort;
import com.sofkify.cartservice.domain.ports.out.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@DisplayName("ConfirmCartService Tests - GREEN Phase")
class ConfirmCartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private StockValidationPort stockValidationPort;

    @Mock
    private EventPublisher eventPublisher;

    private ConfirmCartService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ConfirmCartService(cartRepository, stockValidationPort, eventPublisher);
    }

    @Test
    @DisplayName("Should confirm cart successfully when cart exists and customer matches")
    void shouldConfirmCartSuccessfully() {
        // Given
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        
        Cart cart = new Cart(cartId, customerId);
        cart.addItem(productId, "Test Product", new BigDecimal("10.00"), 2);
        
        ConfirmCartRequest request = new ConfirmCartRequest(cartId, customerId);
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(stockValidationPort.isStockAvailable(productId, 2)).thenReturn(true);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        // When
        ConfirmCartResponse response = service.execute(request);
        
        // Then
        assertNotNull(response);
        assertEquals(cartId, response.getCartId());
        assertTrue(response.isSuccess());
        
        verify(cartRepository).findById(cartId);
        verify(cartRepository).save(any(Cart.class));
        verify(eventPublisher, atLeastOnce()).publish(any(CartConfirmedEvent.class));
    }

    @Test
    @DisplayName("Should fail when cart does not exist")
    void shouldFailWhenCartDoesNotExist() {
        // Given
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        ConfirmCartRequest request = new ConfirmCartRequest(cartId, customerId);
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        
        // When / Then
        assertThrows(CartException.class, () -> service.execute(request));
    }

    @Test
    @DisplayName("Should fail when customer ID does not match")
    void shouldFailWhenCustomerDoesNotMatch() {
        // Given
        UUID cartId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID differentCustomerId = UUID.randomUUID();
        
        Cart cart = new Cart(cartId, ownerId);
        ConfirmCartRequest request = new ConfirmCartRequest(cartId, differentCustomerId);
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        
        // When / Then
        assertThrows(CartException.class, () -> service.execute(request));
    }
}