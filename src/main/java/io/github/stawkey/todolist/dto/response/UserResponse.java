package io.github.stawkey.todolist.dto.response;

import io.github.stawkey.todolist.entity.User;

public record UserResponse(Integer id, String name, String email) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
