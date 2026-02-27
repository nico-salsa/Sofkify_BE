package com.sofkify.userservice.application.service;

import com.sofkify.userservice.application.exception.AccountDisabledException;
import com.sofkify.userservice.application.exception.InvalidCredentialsException;
import com.sofkify.userservice.application.exception.UserAlreadyExistsException;
import com.sofkify.userservice.application.exception.UserNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private AuthenticationService authenticationService;
    private UserService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UserService(userRepository, authenticationService);
    }

    private User testUser() {
        return User.builder()
            .id("user-1")
            .email("test@email.com")
            .password("password123")
            .name("Test User")
            .role(UserRole.CLIENTE)
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() {
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.createUser("new@email.com", "password123", "New User");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should reject duplicate email")
    void shouldRejectDuplicateEmail() {
        when(userRepository.existsByEmail("dup@email.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
            () -> service.createUser("dup@email.com", "password123", "User"));
    }

    @Test
    @DisplayName("Should find by email")
    void shouldFindByEmail() {
        User user = testUser();
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        User result = service.findByEmail("test@email.com");
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    @DisplayName("Should throw when email not found")
    void shouldThrowWhenEmailNotFound() {
        when(userRepository.findByEmail("none@email.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findByEmail("none@email.com"));
    }

    @Test
    @DisplayName("Should find by id")
    void shouldFindById() {
        User user = testUser();
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        Optional<User> result = service.findById("user-1");
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Should update profile")
    void shouldUpdateProfile() {
        User user = testUser();
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.updateProfile("user-1", "New Name", "new@email.com");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should reject updating to existing email")
    void shouldRejectExistingEmail() {
        User user = testUser();
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@email.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
            () -> service.updateProfile("user-1", "Name", "taken@email.com"));
    }

    @Test
    @DisplayName("Should throw when updating non-existing user")
    void shouldThrowUpdatingNonExisting() {
        when(userRepository.findById("none")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> service.updateProfile("none", "Name", "e@e.com"));
    }

    @Test
    @DisplayName("Should deactivate user")
    void shouldDeactivateUser() {
        User user = testUser();
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.deactivateUser("user-1");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw when deactivating non-existing")
    void shouldThrowDeactivatingNonExisting() {
        when(userRepository.findById("none")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.deactivateUser("none"));
    }

    @Test
    @DisplayName("Should check existsByEmail")
    void shouldCheckExistsByEmail() {
        when(userRepository.existsByEmail("a@b.com")).thenReturn(true);
        assertTrue(service.existsByEmail("a@b.com"));

        when(userRepository.existsByEmail("c@d.com")).thenReturn(false);
        assertFalse(service.existsByEmail("c@d.com"));
    }

    @Test
    @DisplayName("Should promote to admin")
    void shouldPromoteToAdmin() {
        User user = testUser();
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.promoteToAdmin("user-1");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw promoting non-existing user")
    void shouldThrowPromotingNonExisting() {
        when(userRepository.findById("none")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.promoteToAdmin("none"));
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    void shouldAuthenticateUser() {
        User user = testUser();
        when(authenticationService.authenticate("test@email.com", "password123")).thenReturn(user);

        Optional<User> result = service.authenticateUser("test@email.com", "password123");
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty on invalid credentials")
    void shouldReturnEmptyOnInvalidCredentials() {
        when(authenticationService.authenticate("bad@email.com", "wrong"))
            .thenThrow(new InvalidCredentialsException("bad"));

        Optional<User> result = service.authenticateUser("bad@email.com", "wrong");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty on disabled account")
    void shouldReturnEmptyOnDisabledAccount() {
        when(authenticationService.authenticate("dis@email.com", "pass"))
            .thenThrow(new AccountDisabledException("disabled"));

        Optional<User> result = service.authenticateUser("dis@email.com", "pass");
        assertTrue(result.isEmpty());
    }
}
