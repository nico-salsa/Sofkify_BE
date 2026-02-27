package com.sofkify.userservice.domain.model;

import com.sofkify.userservice.domain.exception.UserValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Model Tests")
class UserTest {

    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUser() {
        User user = User.create("test@email.com", "password123", "John");

        assertNotNull(user.getId());
        assertEquals("test@email.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("John", user.getName());
        assertEquals(UserRole.CLIENTE, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should reject null email")
    void shouldRejectNullEmail() {
        assertThrows(UserValidationException.class, () -> User.create(null, "password123", "John"));
    }

    @Test
    @DisplayName("Should reject invalid email without @")
    void shouldRejectEmailWithoutAt() {
        assertThrows(UserValidationException.class, () -> User.create("invalid", "password123", "John"));
    }

    @Test
    @DisplayName("Should reject null password")
    void shouldRejectNullPassword() {
        assertThrows(UserValidationException.class, () -> User.create("a@b.com", null, "John"));
    }

    @Test
    @DisplayName("Should reject short password")
    void shouldRejectShortPassword() {
        assertThrows(UserValidationException.class, () -> User.create("a@b.com", "short", "John"));
    }

    @Test
    @DisplayName("Should reject null name")
    void shouldRejectNullName() {
        assertThrows(UserValidationException.class, () -> User.create("a@b.com", "password123", null));
    }

    @Test
    @DisplayName("Should reject empty name")
    void shouldRejectEmptyName() {
        assertThrows(UserValidationException.class, () -> User.create("a@b.com", "password123", ""));
        assertThrows(UserValidationException.class, () -> User.create("a@b.com", "password123", "   "));
    }

    @Test
    @DisplayName("Should update profile")
    void shouldUpdateProfile() {
        User user = User.create("old@email.com", "password123", "Old Name");
        user.updateProfile("New Name", "new@email.com");

        assertEquals("New Name", user.getName());
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    @DisplayName("Should deactivate user")
    void shouldDeactivateUser() {
        User user = User.create("a@b.com", "password123", "John");
        user.deactivate();
        assertEquals(UserStatus.INACTIVE, user.getStatus());
        assertFalse(user.isActive());
    }

    @Test
    @DisplayName("Should promote to admin")
    void shouldPromoteToAdmin() {
        User user = User.create("a@b.com", "password123", "John");
        assertTrue(user.isClient());
        assertFalse(user.isAdmin());

        user.promoteToAdmin();

        assertTrue(user.isAdmin());
        assertFalse(user.isClient());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("Should reject promotion of non-client")
    void shouldRejectPromotionOfNonClient() {
        User user = User.create("a@b.com", "password123", "John");
        user.promoteToAdmin();

        assertThrows(UserValidationException.class, user::promoteToAdmin);
    }

    @Test
    @DisplayName("isActive should return true for active user")
    void isActiveShouldReturnTrue() {
        User user = User.create("a@b.com", "password123", "John");
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("Builder should create user with all fields")
    void builderShouldCreateUser() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
            .id("id-123")
            .email("b@c.com")
            .password("pass12345")
            .name("Built")
            .role(UserRole.ADMIN)
            .status(UserStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();

        assertEquals("id-123", user.getId());
        assertEquals("b@c.com", user.getEmail());
        assertEquals("pass12345", user.getPassword());
        assertEquals("Built", user.getName());
        assertEquals(UserRole.ADMIN, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("UserRole enum values")
    void userRoleValues() {
        assertEquals(2, UserRole.values().length);
        assertNotNull(UserRole.valueOf("CLIENTE"));
        assertNotNull(UserRole.valueOf("ADMIN"));
    }

    @Test
    @DisplayName("UserStatus enum values")
    void userStatusValues() {
        assertEquals(3, UserStatus.values().length);
        assertNotNull(UserStatus.valueOf("ACTIVE"));
        assertNotNull(UserStatus.valueOf("INACTIVE"));
        assertNotNull(UserStatus.valueOf("BLOCKED"));
    }

    @Test
    @DisplayName("UserValidationException should hold message")
    void userValidationException() {
        UserValidationException ex = new UserValidationException("validation error");
        assertEquals("validation error", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }
}
