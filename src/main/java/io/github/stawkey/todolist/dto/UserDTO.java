package io.github.stawkey.todolist.dto;

import io.github.stawkey.todolist.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User data transfer object")
public record UserDTO(@Schema(description = "User ID", example = "1") Integer id,

                      @Schema(description = "User's name", example = "stawkey") String name,

                      @Schema(description = "User's email address", example = "stawkey@example.com") String email) {
    public static UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}