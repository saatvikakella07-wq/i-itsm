package com.itsm.exception;

/**
 * Exception thrown when a requested resource (Incident, User, Service, etc.) cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
