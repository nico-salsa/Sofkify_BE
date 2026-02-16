package com.sofkify.userservice.infrastructure.mapper;

import com.sofkify.userservice.application.dto.LoginResponse;
import com.sofkify.userservice.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class LoginResponseMapper {
    public LoginResponse toDto(User user) {
        return new LoginResponse(
                true,
                "Login exitoso",
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }
}
