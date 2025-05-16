package io.github.stawkey.todolist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration data")
public record RegisterRequest(
        @Schema(description = "User's name", example = "stawkey")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(description = "User's email address", example = "stawkey@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Schema(description = "User's password", example = "password123")
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {
}