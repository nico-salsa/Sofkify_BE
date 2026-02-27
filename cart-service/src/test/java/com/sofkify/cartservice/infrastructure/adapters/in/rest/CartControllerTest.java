package com.sofkify.cartservice.infrastructure.adapters.in.rest;

import com.sofkify.cartservice.application.dto.AddItemRequest;
import com.sofkify.cartservice.application.dto.ConfirmCartRequest;
import com.sofkify.cartservice.application.dto.ConfirmCartResponse;
import com.sofkify.cartservice.application.dto.UpdateQuantityRequest;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.ports.in.AddItemToCartUseCase;
import com.sofkify.cartservice.domain.ports.in.ConfirmCartUseCase;
import com.sofkify.cartservice.domain.ports.in.GetCartUseCase;
import com.sofkify.cartservice.domain.ports.in.RemoveItemFromCartUseCase;
import com.sofkify.cartservice.domain.ports.in.UpdateItemQuantityUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("CartController Tests")
class CartControllerTest {

    @Mock private AddItemToCartUseCase addItemToCartUseCase;
    @Mock private GetCartUseCase getCartUseCase;
    @Mock private UpdateItemQuantityUseCase updateItemQuantityUseCase;
    @Mock private RemoveItemFromCartUseCase removeItemFromCartUseCase;
    @Mock private ConfirmCartUseCase confirmCartUseCase;

    private CartController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new CartController(
            addItemToCartUseCase, getCartUseCase,
            updateItemQuantityUseCase, removeItemFromCartUseCase,
            confirmCartUseCase
        );
    }

    private Cart createCartWithItem() {
        UUID productId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID());
        cart.addItem(productId, "Test Product", new BigDecimal("20.00"), 2);
        return cart;
    }

    @Test
    @DisplayName("Should add item and return 201")
    void shouldAddItemAndReturn201() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Cart cart = createCartWithItem();

        when(addItemToCartUseCase.addItem(eq(customerId), eq(productId), eq(3)))
            .thenReturn(cart);

        var response = controller.addItem(customerId, new AddItemRequest(productId, 3));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cart.getId(), response.getBody().id());
        assertFalse(response.getBody().items().isEmpty());
    }

    @Test
    @DisplayName("Should get cart by customer ID")
    void shouldGetCartByCustomerId() {
        UUID customerId = UUID.randomUUID();
        Cart cart = createCartWithItem();

        when(getCartUseCase.getCartByCustomerId(customerId)).thenReturn(cart);

        var response = controller.getCart(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cart.getCustomerId(), response.getBody().customerId());
    }

    @Test
    @DisplayName("Should get cart by cart ID")
    void shouldGetCartById() {
        Cart cart = createCartWithItem();

        when(getCartUseCase.getCartById(cart.getId())).thenReturn(cart);

        var response = controller.getCartById(cart.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cart.getId(), response.getBody().id());
    }

    @Test
    @DisplayName("Should update item quantity")
    void shouldUpdateItemQuantity() {
        UUID customerId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        Cart cart = createCartWithItem();

        when(updateItemQuantityUseCase.updateItemQuantity(customerId, cartItemId, 5))
            .thenReturn(cart);

        var response = controller.updateItemQuantity(customerId, cartItemId, new UpdateQuantityRequest(5));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should remove item from cart")
    void shouldRemoveItemFromCart() {
        UUID customerId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), customerId);

        when(removeItemFromCartUseCase.removeItemFromCart(customerId, cartItemId))
            .thenReturn(cart);

        var response = controller.removeItemFromCart(customerId, cartItemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().items().isEmpty());
    }

    @Test
    @DisplayName("Should confirm cart")
    void shouldConfirmCart() {
        UUID customerId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        ConfirmCartResponse confirmResponse = new ConfirmCartResponse(cartId, true, "Confirmed");

        when(confirmCartUseCase.execute(any(ConfirmCartRequest.class)))
            .thenReturn(confirmResponse);

        var response = controller.confirmCart(customerId, cartId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Cart response should include total amount")
    void cartResponseShouldIncludeTotalAmount() {
        Cart cart = createCartWithItem();

        when(getCartUseCase.getCartById(cart.getId())).thenReturn(cart);

        var response = controller.getCartById(cart.getId());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().totalAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Cart response items should have correct structure")
    void cartResponseItemsShouldHaveCorrectStructure() {
        Cart cart = createCartWithItem();

        when(getCartUseCase.getCartById(cart.getId())).thenReturn(cart);

        var response = controller.getCartById(cart.getId());
        var items = response.getBody().items();

        assertEquals(1, items.size());
        assertNotNull(items.get(0).id());
        assertNotNull(items.get(0).productId());
        assertNotNull(items.get(0).productName());
        assertNotNull(items.get(0).productPrice());
        assertTrue(items.get(0).quantity() > 0);
        assertNotNull(items.get(0).subtotal());
    }
}
