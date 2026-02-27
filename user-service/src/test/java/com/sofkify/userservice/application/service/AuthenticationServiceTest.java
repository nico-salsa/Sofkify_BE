package com.sofkify.userservice.application.service;

import com.sofkify.userservice.application.exception.AccountDisabledException;
import com.sofkify.userservice.application.exception.InvalidCredentialsException;
import com.sofkify.userservice.domain.model.User;
import com.sofkify.userservice.domain.model.UserRole;
import com.sofkify.userservice.domain.model.UserStatus;
import com.sofkify.userservice.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

    @Mock private UserRepositoryPort userRepository;
    private AuthenticationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AuthenticationService(userRepository);
    }

    private User activeUser() {
        return User.builder()
            .id("u1")
            .email("a@b.com")
            .password("password123")
            .name("Test")
            .role(UserRole.CLIENTE)
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("Should authenticate active user with correct password")
    void shouldAuthenticate() {
        User user = activeUser();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        User result = service.authenticate("a@b.com", "password123");
        assertEquals("a@b.com", result.getEmail());
    }

    @Test
    @DisplayName("Should throw when user not found")
    void shouldThrowWhenNotFound() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
            () -> service.authenticate("unknown@email.com", "pass"));
    }

    @Test
    @DisplayName("Should throw on wrong password")
    void shouldThrowOnWrongPassword() {
        User user = activeUser();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
            () -> service.authenticate("a@b.com", "wrongpassword"));
    }

    @Test
    @DisplayName("Should throw when account is disabled")
    void shouldThrowWhenDisabled() {
        User user = User.builder()
            .id("u1").email("a@b.com").password("password123").name("Test")
            .role(UserRole.CLIENTE).status(UserStatus.INACTIVE)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .build();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        assertThrows(AccountDisabledException.class,
            () -> service.authenticate("a@b.com", "password123"));
    }
}
