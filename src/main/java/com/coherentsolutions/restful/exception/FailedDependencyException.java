package com.coherentsolutions.restful.exception;

public class FailedDependencyException extends RuntimeException {
    public FailedDependencyException(String message) {
        super(message);
    }
}
