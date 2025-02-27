package io.github.stawkey.todolist.dto;

import io.github.stawkey.todolist.entity.User;

public record UserDTO(Integer id, String name, String email) {
    public static UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
