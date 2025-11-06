package com.example.app.inventory.exception;

import com.example.app.common.exception.BusinessException;

/**
 * Exception thrown when there is insufficient stock to reserve.
 */
public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String message) {
        super(message);
    }
}

