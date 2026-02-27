package com.sofkify.userservice.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User DTO Tests")
class UserDtoTest {

    @Test
    @DisplayName("CreateUserRequest getters/setters")
    void createUserRequest() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("a@b.com");
        req.setPassword("password123");
        req.setName("Test");

        assertEquals("a@b.com", req.getEmail());
        assertEquals("password123", req.getPassword());
        assertEquals("Test", req.getName());
    }

    @Test
    @DisplayName("UpdateUserRequest getters/setters")
    void updateUserRequest() {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setName("New Name");
        req.setEmail("new@email.com");

        assertEquals("New Name", req.getName());
        assertEquals("new@email.com", req.getEmail());
    }

    @Test
    @DisplayName("UserResponse constructor and getters")
    void userResponse() {
        LocalDateTime now = LocalDateTime.now();
        UserResponse resp = new UserResponse("id", "e@e.com", "Name", "ADMIN", "ACTIVE", now, now);

        assertEquals("id", resp.getId());
        assertEquals("e@e.com", resp.getEmail());
        assertEquals("Name", resp.getName());
        assertEquals("ADMIN", resp.getRole());
        assertEquals("ACTIVE", resp.getStatus());
        assertEquals(now, resp.getCreatedAt());
        assertEquals(now, resp.getUpdatedAt());
    }

    @Test
    @DisplayName("LoginRequest constructors and getters/setters")
    void loginRequest() {
        LoginRequest req1 = new LoginRequest();
        req1.setEmail("a@b.com");
        req1.setPassword("pass");
        assertEquals("a@b.com", req1.getEmail());
        assertEquals("pass", req1.getPassword());

        LoginRequest req2 = new LoginRequest("x@y.com", "p123");
        assertEquals("x@y.com", req2.getEmail());
        assertEquals("p123", req2.getPassword());
    }

    @Test
    @DisplayName("LoginResponse constructors and getters/setters")
    void loginResponse() {
        LoginResponse resp1 = new LoginResponse();
        resp1.setSuccess(true);
        resp1.setMessage("ok");
        resp1.setUserId("u1");
        resp1.setEmail("a@b.com");
        resp1.setName("N");
        resp1.setRole("ADMIN");

        assertTrue(resp1.isSuccess());
        assertEquals("ok", resp1.getMessage());
        assertEquals("u1", resp1.getUserId());
        assertEquals("a@b.com", resp1.getEmail());
        assertEquals("N", resp1.getName());
        assertEquals("ADMIN", resp1.getRole());

        LoginResponse resp2 = new LoginResponse(false, "fail");
        assertFalse(resp2.isSuccess());
        assertEquals("fail", resp2.getMessage());

        LoginResponse resp3 = new LoginResponse(true, "Login exitoso", "u1", "a@b.com", "N", "CLIENTE");
        assertTrue(resp3.isSuccess());
        assertEquals("u1", resp3.getUserId());
    }

    @Test
    @DisplayName("PromoteUserRequest getters/setters")
    void promoteUserRequest() {
        PromoteUserRequest req = new PromoteUserRequest();
        req.setUserId("user-1");
        assertEquals("user-1", req.getUserId());
    }
}
