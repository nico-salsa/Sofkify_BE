package com.sofkify.cartservice.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfirmCartRequest DTO Tests")
class ConfirmCartRequestTest {

    @Test
    @DisplayName("Should create request with valid data")
    void shouldCreateWithValidData() {
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        ConfirmCartRequest request = new ConfirmCartRequest(cartId, customerId);

        assertEquals(cartId, request.getCartId());
        assertEquals(customerId, request.getCustomerId());
    }

    @Test
    @DisplayName("Should throw when cartId is null")
    void shouldThrowWhenCartIdIsNull() {
        assertThrows(NullPointerException.class,
            () -> new ConfirmCartRequest(null, UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should throw when customerId is null")
    void shouldThrowWhenCustomerIdIsNull() {
        assertThrows(NullPointerException.class,
            () -> new ConfirmCartRequest(UUID.randomUUID(), null));
    }

    @Test
    @DisplayName("Should be equal when same cartId and customerId")
    void shouldBeEqualWhenSameIds() {
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        ConfirmCartRequest a = new ConfirmCartRequest(cartId, customerId);
        ConfirmCartRequest b = new ConfirmCartRequest(cartId, customerId);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when different cartId")
    void shouldNotBeEqualWhenDifferentCartId() {
        UUID customerId = UUID.randomUUID();

        ConfirmCartRequest a = new ConfirmCartRequest(UUID.randomUUID(), customerId);
        ConfirmCartRequest b = new ConfirmCartRequest(UUID.randomUUID(), customerId);

        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("Should not be equal when different customerId")
    void shouldNotBeEqualWhenDifferentCustomerId() {
        UUID cartId = UUID.randomUUID();

        ConfirmCartRequest a = new ConfirmCartRequest(cartId, UUID.randomUUID());
        ConfirmCartRequest b = new ConfirmCartRequest(cartId, UUID.randomUUID());

        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        ConfirmCartRequest request = new ConfirmCartRequest(UUID.randomUUID(), UUID.randomUUID());
        assertEquals(request, request);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        ConfirmCartRequest request = new ConfirmCartRequest(UUID.randomUUID(), UUID.randomUUID());
        assertNotEquals(null, request);
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
        ConfirmCartRequest request = new ConfirmCartRequest(UUID.randomUUID(), UUID.randomUUID());
        assertNotEquals("string", request);
    }

    @Test
    @DisplayName("Should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        ConfirmCartRequest request = new ConfirmCartRequest(cartId, customerId);

        String str = request.toString();
        assertTrue(str.contains(cartId.toString()));
        assertTrue(str.contains(customerId.toString()));
    }
}
