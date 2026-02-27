package com.sofkify.userservice.application.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Application Exception Tests")
class UserExceptionTest {

    @Test
    @DisplayName("UserNotFoundException with message")
    void userNotFound() {
        UserNotFoundException ex = new UserNotFoundException("not found");
        assertEquals("not found", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("UserAlreadyExistsException with message")
    void userAlreadyExists() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("exists");
        assertEquals("exists", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("InvalidCredentialsException with message")
    void invalidCredentials() {
        InvalidCredentialsException ex = new InvalidCredentialsException("bad creds");
        assertEquals("bad creds", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("InvalidCredentialsException with cause")
    void invalidCredentialsWithCause() {
        Exception cause = new RuntimeException("root");
        InvalidCredentialsException ex = new InvalidCredentialsException("bad", cause);
        assertEquals("bad", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("AccountDisabledException with message")
    void accountDisabled() {
        AccountDisabledException ex = new AccountDisabledException("disabled");
        assertEquals("disabled", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("AccountDisabledException with cause")
    void accountDisabledWithCause() {
        Exception cause = new RuntimeException("root");
        AccountDisabledException ex = new AccountDisabledException("disabled", cause);
        assertEquals("disabled", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
