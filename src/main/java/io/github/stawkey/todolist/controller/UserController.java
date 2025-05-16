package io.github.stawkey.todolist.controller;

import io.github.stawkey.todolist.dto.request.RegisterRequest;
import io.github.stawkey.todolist.dto.response.RegisterResponse;
import io.github.stawkey.todolist.dto.UserDTO;
import io.github.stawkey.todolist.dto.request.LoginRequest;
import io.github.stawkey.todolist.dto.response.LoginResponse;
import io.github.stawkey.todolist.security.JwtUtil;
import io.github.stawkey.todolist.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication API", description = "API for user authentication and registration")
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, UserDetailsService userDetailsService,
                          AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Logs in a user and returns a JWT token")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully logged in, returns JWT token",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class),
                    examples = @ExampleObject(value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")})
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> createAuthenticationToken(
            @Parameter(description = "User login credentials", required = true,
                    schema = @Schema(implementation = LoginRequest.class), examples = @ExampleObject(
                    value = "{\"email\":\"stawkey@example.com\"," + "\"password\":\"password123\"}")) @Valid
            @RequestBody LoginRequest loginRequest) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @Operation(summary = "Registers a new user and returns a JWT token and user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered, returns JWT token and user data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterResponse.class), examples = @ExampleObject(
                            value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.." +
                                    ".\",\"user\":{\"id\":1,\"name\":\"stawkey\",\"email\":\"stawkey@example" +
                                    ".com\"}}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., email already exists)")})
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Parameter(description = "Registration data for the new user", required = true,
                    schema = @Schema(implementation = RegisterRequest.class), examples = @ExampleObject(
                    value = "{\"name\":\"stawkey\",\"email\":\"stawkey@example.com\"," +
                            "\"password\":\"password123\"}")) @Valid @RequestBody RegisterRequest registerRequest) {

        UserDTO savedUser = userService.save(registerRequest);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.email());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new RegisterResponse(jwt, savedUser));
    }
}