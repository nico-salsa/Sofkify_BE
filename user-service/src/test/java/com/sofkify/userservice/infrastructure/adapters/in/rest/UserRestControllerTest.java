package com.sofkify.userservice.infrastructure.adapters.in.rest;

import com.sofkify.userservice.application.dto.*;
import com.sofkify.userservice.domain.model.User;
import com.sofkify.userservice.domain.model.UserRole;
import com.sofkify.userservice.domain.model.UserStatus;
import com.sofkify.userservice.domain.ports.in.UserServicePort;
import com.sofkify.userservice.infrastructure.mapper.LoginResponseMapper;
import com.sofkify.userservice.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserRestController Tests")
class UserRestControllerTest {

    @Mock private UserServicePort userService;
    @Mock private UserMapper userMapper;
    @Mock private LoginResponseMapper loginResponseMapper;

    private UserRestController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new UserRestController(userService, userMapper, loginResponseMapper);
    }

    private User testUser() {
        return User.builder()
            .id("u1").email("a@b.com").password("pass12345").name("Test")
            .role(UserRole.CLIENTE).status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .build();
    }

    private UserResponse testResponse() {
        return new UserResponse("u1", "a@b.com", "Test", "CLIENTE", "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create user")
    void shouldCreateUser() {
        User user = testUser();
        UserResponse resp = testResponse();
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("a@b.com"); req.setPassword("pass12345"); req.setName("Test");

        when(userService.createUser("a@b.com", "pass12345", "Test")).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(resp);

        var response = controller.createUser(req);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get user by id")
    void shouldGetUserById() {
        User user = testUser();
        UserResponse resp = testResponse();

        when(userService.findById("u1")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(resp);

        var response = controller.getUserById("u1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get user by email")
    void shouldGetUserByEmail() {
        User user = testUser();
        UserResponse resp = testResponse();

        when(userService.findByEmail("a@b.com")).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(resp);

        var response = controller.getUserByEmail("a@b.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() {
        User user = testUser();
        UserResponse resp = testResponse();
        UpdateUserRequest req = new UpdateUserRequest();
        req.setName("New"); req.setEmail("new@e.com");

        when(userService.updateProfile("u1", "New", "new@e.com")).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(resp);

        var response = controller.updateUser("u1", req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should promote to admin")
    void shouldPromoteToAdmin() {
        User user = testUser();
        UserResponse resp = testResponse();

        when(userService.promoteToAdmin("u1")).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(resp);

        var response = controller.promoteToAdmin("u1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should deactivate user")
    void shouldDeactivateUser() {
        var response = controller.deactivateUser("u1");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deactivateUser("u1");
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() {
        User user = testUser();
        LoginRequest req = new LoginRequest("a@b.com", "pass12345");
        LoginResponse loginResp = new LoginResponse(true, "ok", "u1", "a@b.com", "Test", "CLIENTE");

        when(userService.authenticateUser("a@b.com", "pass12345")).thenReturn(Optional.of(user));
        when(loginResponseMapper.toDto(user)).thenReturn(loginResp);

        var response = controller.login(req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Should return unauthorized on failed login")
    void shouldReturnUnauthorized() {
        LoginRequest req = new LoginRequest("bad@e.com", "wrong");
        when(userService.authenticateUser("bad@e.com", "wrong")).thenReturn(Optional.empty());

        var response = controller.login(req);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }
}
