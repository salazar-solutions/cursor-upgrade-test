package com.example.app.common.exception;

/**
 * Exception thrown when an entity is not found.
 */
public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}

