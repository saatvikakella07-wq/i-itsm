package com.itsm.exception;

/**
 * Exception thrown when an authenticated user attempts an operation outside their authorized role.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
