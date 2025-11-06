package com.example.app.payment.provider;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Interface for payment provider implementations.
 */
public interface PaymentProvider {
    /**
     * Process a payment and return provider reference.
     * @param orderId Order ID
     * @param amount Payment amount
     * @return Provider reference string
     * @throws PaymentProcessingException if payment processing fails
     */
    String processPayment(UUID orderId, BigDecimal amount) throws PaymentProcessingException;
}

