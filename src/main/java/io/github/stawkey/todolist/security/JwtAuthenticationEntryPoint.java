package io.github.stawkey.todolist.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stawkey.todolist.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        logger.warn("Unauthorized access attempt | URI: {} | Error: {}",
                request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String userFriendlyMessage = getMessage(request);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                userFriendlyMessage,
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private String getMessage(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            return "Please log in to access this resource. Authentication token is missing.";
        }

        if (authHeader.startsWith("Bearer ")) {
            if (authHeader.length() < 10) {
                return "Invalid authentication token format. Please log in again.";
            }
            return "Your session has expired or the authentication token is invalid. Please log in again.";
        }

        return "Authentication failed. Please provide a valid Bearer token.";
    }
}