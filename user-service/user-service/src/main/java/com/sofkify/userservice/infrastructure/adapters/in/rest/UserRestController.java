package com.sofkify.userservice.infrastructure.adapters.in.rest;

import com.sofkify.userservice.application.dto.CreateUserRequest;
import com.sofkify.userservice.application.dto.UpdateUserRequest;
import com.sofkify.userservice.application.dto.UserResponse;
import com.sofkify.userservice.application.exception.UserNotFoundException;
import com.sofkify.userservice.domain.model.User;
import com.sofkify.userservice.domain.ports.in.UserServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserServicePort userService;

    public UserRestController(UserServicePort userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User createdUser = userService.createUser(request.getEmail(), request.getPassword(), request.getName());
        UserResponse response = mapToUserResponse(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + id));
        UserResponse response = mapToUserResponse(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        UserResponse response = mapToUserResponse(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateProfile(id, request.getName(), request.getEmail());
        UserResponse response = mapToUserResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/promote")
    public ResponseEntity<UserResponse> promoteToAdmin(@PathVariable String id) {
        User promotedUser = userService.promoteToAdmin(id);
        UserResponse response = mapToUserResponse(promotedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable String id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}