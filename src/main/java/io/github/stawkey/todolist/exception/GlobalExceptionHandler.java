package io.github.stawkey.todolist.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.NOT_FOUND);
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.FORBIDDEN);
        return createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleIllegalExceptions(
            RuntimeException ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.CONFLICT);
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.valueOf(ex.getStatusCode().value()));
        return createErrorResponse(ex.getStatusCode().value(), ex.getReason(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, List<String>> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : "unknown_field";
            String message = error.getDefaultMessage();

            fieldErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(message);
        });

        logValidationErrors(fieldErrors, request);

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                fieldErrors,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    private void logException(Exception ex, HttpServletRequest request, HttpStatus status) {
        String username = request.getUserPrincipal() != null ?
                request.getUserPrincipal().getName() : "anonymous";

        String logMessage = String.format(
                "Exception: %s | Status: %s | URI: %s | User: %s | Message: %s",
                ex.getClass().getSimpleName(),
                status.value(),
                request.getRequestURI(),
                username,
                ex.getMessage()
        );

        if (status.is5xxServerError()) {
            logger.error(logMessage, ex);
        } else if (status.is4xxClientError()) {
            logger.warn(logMessage);
        } else {
            logger.info(logMessage);
        }
    }

    private void logValidationErrors(Map<String, List<String>> fieldErrors, HttpServletRequest request) {
        String username = request.getUserPrincipal() != null ?
                request.getUserPrincipal().getName() : "anonymous";

        StringBuilder errorDetails = new StringBuilder();
        fieldErrors.forEach((field, errors) ->
                errorDetails.append(field).append(": ")
                        .append(String.join(", ", errors))
                        .append("; ")
        );

        logger.warn(
                "Validation error | Status: {} | URI: {} | User: {} | Timestamp: {} | Errors: {}",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                username,
                new Date(),
                errorDetails
        );
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse errorResponse = new ErrorResponse(status, message, path);
        return new ResponseEntity<>(errorResponse, status);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(int statusCode, String message, String path) {
        HttpStatus status = HttpStatus.valueOf(statusCode);
        return createErrorResponse(status, message, path);
    }
}