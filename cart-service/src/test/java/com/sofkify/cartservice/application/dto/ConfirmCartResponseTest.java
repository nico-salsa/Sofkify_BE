package com.sofkify.cartservice.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfirmCartResponse DTO Tests")
class ConfirmCartResponseTest {

    @Test
    @DisplayName("Should create successful response with confirmedAt")
    void shouldCreateSuccessfulResponse() {
        UUID cartId = UUID.randomUUID();
        ConfirmCartResponse response = new ConfirmCartResponse(cartId, true, "Cart confirmed");

        assertEquals(cartId, response.getCartId());
        assertTrue(response.isSuccess());
        assertEquals("Cart confirmed", response.getMessage());
        assertNotNull(response.getConfirmedAt());
    }

    @Test
    @DisplayName("Should create failure response without confirmedAt")
    void shouldCreateFailureResponse() {
        UUID cartId = UUID.randomUUID();
        ConfirmCartResponse response = new ConfirmCartResponse(cartId, false, "Insufficient stock");

        assertEquals(cartId, response.getCartId());
        assertFalse(response.isSuccess());
        assertEquals("Insufficient stock", response.getMessage());
        assertNull(response.getConfirmedAt());
    }

    @Test
    @DisplayName("Should throw when cartId is null")
    void shouldThrowWhenCartIdIsNull() {
        assertThrows(NullPointerException.class,
            () -> new ConfirmCartResponse(null, true, "msg"));
    }

    @Test
    @DisplayName("Should throw when message is null")
    void shouldThrowWhenMessageIsNull() {
        assertThrows(NullPointerException.class,
            () -> new ConfirmCartResponse(UUID.randomUUID(), true, null));
    }

    @Test
    @DisplayName("Should be equal when same cartId, success, and message")
    void shouldBeEqualWhenSameData() {
        UUID cartId = UUID.randomUUID();
        ConfirmCartResponse a = new ConfirmCartResponse(cartId, true, "OK");
        ConfirmCartResponse b = new ConfirmCartResponse(cartId, true, "OK");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when different success")
    void shouldNotBeEqualWhenDifferentSuccess() {
        UUID cartId = UUID.randomUUID();
        ConfirmCartResponse a = new ConfirmCartResponse(cartId, true, "msg");
        ConfirmCartResponse b = new ConfirmCartResponse(cartId, false, "msg");

        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        ConfirmCartResponse r = new ConfirmCartResponse(UUID.randomUUID(), true, "OK");
        assertEquals(r, r);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        ConfirmCartResponse r = new ConfirmCartResponse(UUID.randomUUID(), true, "OK");
        assertNotEquals(null, r);
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
        ConfirmCartResponse r = new ConfirmCartResponse(UUID.randomUUID(), true, "OK");
        assertNotEquals("string", r);
    }

    @Test
    @DisplayName("Should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        UUID cartId = UUID.randomUUID();
        ConfirmCartResponse r = new ConfirmCartResponse(cartId, true, "Done");

        String str = r.toString();
        assertTrue(str.contains(cartId.toString()));
        assertTrue(str.contains("Done"));
    }
}
