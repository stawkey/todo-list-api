package io.github.stawkey.todolist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request data")
public record LoginRequest(
        @Schema(description = "User's email address", example = "stawkey@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Schema(description = "User's password", example = "password123")
        @NotBlank(message = "Password is required")
        String password
) {
}