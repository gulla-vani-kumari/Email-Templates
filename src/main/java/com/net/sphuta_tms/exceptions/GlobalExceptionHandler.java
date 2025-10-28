package com.net.sphuta_tms.exceptions;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
/**
 * =============================================================
 * GlobalExceptionHandler
 * =============================================================
 * This class serves as a centralized exception handler for the application.
 * It captures specific exceptions thrown during request processing and
 * translates them into meaningful HTTP responses.
 *
 * Features:
 * - Handles IllegalArgumentException for bad requests (400).
 * - Handles MessagingException for email-related errors (500).
 * - Catches all other exceptions as internal server errors (500).
 *
 * Logging:
 * - Uses SLF4J for structured logging of exceptions.
 *
 * Usage:
 * - Annotated with @ControllerAdvice to apply globally across all controllers.
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid request: " + ex.getMessage());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<String> handleMessagingError(MessagingException ex) {
        log.error("Email sending error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send email: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralError(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: " + ex.getMessage());
    }
}
