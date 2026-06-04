package com.sched.api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Helper method to standardize the ResponseEntity creation
    private ResponseEntity<ErrorDetails> buildResponse(
            HttpStatus status,
            Object message
    ) {
        ErrorDetails details = new ErrorDetails(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(details);
    }

    // (500) - Handles unexpected exceptions not explicitly mapped by other handlers.
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleGeneralException() {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal error occurred.");
    }

    // (400) - Handles requests with missing or malformed JSON bodies.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleMissingBody() {
        return buildResponse(HttpStatus.BAD_REQUEST, "The request body is required or malformed.");
    }

    // (404) - Handles cases where a requested resource does not exist.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleNotFound() {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found.");
    }

    // (409) - Handles database integrity violations such as duplicate keys or invalid relations.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleConflict() {
        return buildResponse(HttpStatus.CONFLICT, "A data integrity conflict occurred.");
    }

    // (401) - Handles authentication failures caused by invalid login credentials.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity handleAuthenticationError() {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
    }

    // (403) - Handles authorization failures when the user lacks required permissions.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handleAccessDenied() {
        return buildResponse(HttpStatus.FORBIDDEN, "You do not have permission to access this resource.");
    }

    // (400) - Handles business rules related to insufficient inventory quantity.
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity handleInsufficientStock() {
        return buildResponse(HttpStatus.BAD_REQUEST, "Insufficient product stock.");
    }

    // (400) - Handles operations involving products that still contain stock.
    @ExceptionHandler(ProductHasStockException.class)
    public ResponseEntity handleProductHasStock() {
        return buildResponse(HttpStatus.BAD_REQUEST, "Product still has stock available.");
    }
}