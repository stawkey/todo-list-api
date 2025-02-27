package io.github.stawkey.todolist.dto.response;

import io.github.stawkey.todolist.dto.UserDTO;

public record RegisterResponse(String token, UserDTO userDTO) {
}
