package io.github.stawkey.todolist.controller;

import io.github.stawkey.todolist.dto.request.RegisterRequest;
import io.github.stawkey.todolist.dto.response.RegisterResponse;
import io.github.stawkey.todolist.dto.UserDTO;
import io.github.stawkey.todolist.dto.request.LoginRequest;
import io.github.stawkey.todolist.dto.response.LoginResponse;
import io.github.stawkey.todolist.security.JwtUtil;
import io.github.stawkey.todolist.service.UserService;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> createAuthenticationToken(@Valid @RequestBody LoginRequest loginRequest) {
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

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDTO savedUser = userService.save(registerRequest);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.email());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new RegisterResponse(jwt, savedUser));
    }
}
