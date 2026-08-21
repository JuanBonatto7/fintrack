package com.juanbonatto.fintrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }

    @ExceptionHandler(BudgetAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleBudgetAlreadyExists(BudgetAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación", fieldErrors);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, Map<String, String> fieldErrors) {
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(apiError);
    }
}
