package com.sofkify.userservice.infrastructure.mapper;

import com.sofkify.userservice.application.dto.LoginResponse;
import com.sofkify.userservice.application.dto.UserResponse;
import com.sofkify.userservice.domain.model.User;
import com.sofkify.userservice.domain.model.UserRole;
import com.sofkify.userservice.domain.model.UserStatus;
import com.sofkify.userservice.infrastructure.adapters.out.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Mapper Tests")
class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
    }

    private User testUser() {
        return User.builder()
            .id("u1").email("a@b.com").password("pass12345").name("Test")
            .role(UserRole.CLIENTE).status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .build();
    }

    private UserEntity testEntity() {
        return new UserEntity("u1", "a@b.com", "pass12345", "Test",
            UserRole.CLIENTE, UserStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Should map entity to domain")
    void shouldMapToDomain() {
        UserEntity entity = testEntity();
        User user = mapper.toDomain(entity);

        assertEquals(entity.getId(), user.getId());
        assertEquals(entity.getEmail(), user.getEmail());
        assertEquals(entity.getPassword(), user.getPassword());
        assertEquals(entity.getName(), user.getName());
        assertEquals(entity.getRole(), user.getRole());
        assertEquals(entity.getStatus(), user.getStatus());
    }

    @Test
    @DisplayName("Should return null for null entity")
    void shouldReturnNullForNullEntity() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("Should map domain to entity")
    void shouldMapToEntity() {
        User user = testUser();
        UserEntity entity = mapper.toEntity(user);

        assertEquals(user.getId(), entity.getId());
        assertEquals(user.getEmail(), entity.getEmail());
        assertEquals(user.getPassword(), entity.getPassword());
        assertEquals(user.getName(), entity.getName());
        assertEquals(user.getRole(), entity.getRole());
        assertEquals(user.getStatus(), entity.getStatus());
    }

    @Test
    @DisplayName("Should return null for null user")
    void shouldReturnNullForNullUser() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should map domain to DTO")
    void shouldMapToDto() {
        User user = testUser();
        UserResponse dto = mapper.toDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getName(), dto.getName());
        assertEquals("CLIENTE", dto.getRole());
        assertEquals("ACTIVE", dto.getStatus());
    }

    @Test
    @DisplayName("Should return null DTO for null user")
    void shouldReturnNullDtoForNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    @DisplayName("Should map domain list")
    void shouldMapDomainList() {
        List<UserEntity> entities = List.of(testEntity());
        List<User> users = mapper.toDomainList(entities);
        assertEquals(1, users.size());
    }

    @Test
    @DisplayName("Should map entity list")
    void shouldMapEntityList() {
        List<User> users = List.of(testUser());
        List<UserEntity> entities = mapper.toEntityList(users);
        assertEquals(1, entities.size());
    }
}
