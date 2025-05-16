package io.github.stawkey.todolist.dto.response;

import io.github.stawkey.todolist.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Registration response containing JWT token and user data")
public record RegisterResponse(@Schema(description = "JWT authentication token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJzdWIiOiJzdGF3a2V5QGV4YW1wbGUuY29tIiwiaWF0IjoxNjIxNTM0NTcxLCJleHAiOjE2MjE1MzgxNzF9" +
                ".this_would_be_the_signature") String token,

                               @Schema(description = "User data") UserDTO userDTO) {
}