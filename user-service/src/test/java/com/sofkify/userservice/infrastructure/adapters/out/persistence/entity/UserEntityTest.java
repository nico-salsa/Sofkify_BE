package com.sofkify.userservice.infrastructure.adapters.out.persistence.entity;

import com.sofkify.userservice.domain.model.UserRole;
import com.sofkify.userservice.domain.model.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserEntity Tests")
class UserEntityTest {

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgs() {
        LocalDateTime now = LocalDateTime.now();
        UserEntity entity = new UserEntity("id-1", "a@b.com", "pass", "Name",
            UserRole.CLIENTE, UserStatus.ACTIVE, now, now);

        assertEquals("id-1", entity.getId());
        assertEquals("a@b.com", entity.getEmail());
        assertEquals("pass", entity.getPassword());
        assertEquals("Name", entity.getName());
        assertEquals(UserRole.CLIENTE, entity.getRole());
        assertEquals(UserStatus.ACTIVE, entity.getStatus());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        UserEntity entity = new UserEntity();
        LocalDateTime now = LocalDateTime.now();

        entity.setId("id-2");
        entity.setEmail("b@c.com");
        entity.setPassword("pass2");
        entity.setName("Name2");
        entity.setRole(UserRole.ADMIN);
        entity.setStatus(UserStatus.INACTIVE);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertEquals("id-2", entity.getId());
        assertEquals("b@c.com", entity.getEmail());
        assertEquals("pass2", entity.getPassword());
        assertEquals("Name2", entity.getName());
        assertEquals(UserRole.ADMIN, entity.getRole());
        assertEquals(UserStatus.INACTIVE, entity.getStatus());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }
}
