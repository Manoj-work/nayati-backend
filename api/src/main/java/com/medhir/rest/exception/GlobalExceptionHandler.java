package com.medhir.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest request) {
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Validation errors occurred",
                validationErrors,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "You do not have permission to access this resource",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        log.error("Authentication failed: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Invalid credentials",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not allowed: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                String.format("Request method '%s' is not supported for this endpoint.", ex.getMethod()),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handlePermissionDeniedException(PermissionDeniedException ex, HttpServletRequest request) {
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred : "  + ex.getMessage() ,
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
