package com.sofkify.userservice.infrastructure.mapper;

import com.sofkify.userservice.application.dto.LoginResponse;
import com.sofkify.userservice.domain.model.User;
import com.sofkify.userservice.domain.model.UserRole;
import com.sofkify.userservice.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginResponseMapper Tests")
class LoginResponseMapperTest {

    private LoginResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LoginResponseMapper();
    }

    @Test
    @DisplayName("Should map User to LoginResponse")
    void shouldMapToLoginResponse() {
        User user = User.builder()
            .id("u1").email("a@b.com").password("pass12345").name("Test")
            .role(UserRole.CLIENTE).status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .build();

        LoginResponse resp = mapper.toDto(user);

        assertTrue(resp.isSuccess());
        assertEquals("Login exitoso", resp.getMessage());
        assertEquals("u1", resp.getUserId());
        assertEquals("a@b.com", resp.getEmail());
        assertEquals("Test", resp.getName());
        assertEquals("CLIENTE", resp.getRole());
    }
}
