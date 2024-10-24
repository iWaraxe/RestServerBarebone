package com.coherentsolutions.restful.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

// Import your custom exceptions
import com.coherentsolutions.restful.exception.BadRequestException;
import com.coherentsolutions.restful.exception.FailedDependencyException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(FailedDependencyException.class)
    public ResponseEntity<String> handleFailedDependencyException(FailedDependencyException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllUncaughtException(Exception ex, WebRequest request) {
        // Log the exception (optional)
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}
