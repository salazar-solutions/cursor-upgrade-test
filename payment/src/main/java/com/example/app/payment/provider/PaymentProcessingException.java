package com.example.app.payment.provider;

/**
 * Exception thrown when payment processing fails.
 */
public class PaymentProcessingException extends Exception {
    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

