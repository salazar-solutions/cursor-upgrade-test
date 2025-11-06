package com.example.app.payment.provider;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Interface for external payment provider integrations.
 * 
 * <p>This interface abstracts communication with external payment gateways
 * or payment processors. Implementations handle provider-specific protocols,
 * authentication, and error handling.
 * 
 * <p><b>Provider Reference:</b> Successful payments return a provider reference
 * (transaction ID, authorization code, etc.) that can be used for refunds,
 * disputes, or reconciliation.
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface PaymentProvider {
    /**
     * Processes a payment through the external payment provider.
     * 
     * <p>This method communicates with the payment provider's API to authorize
     * and process the payment. The provider reference returned can be used for
     * future operations (refunds, queries, etc.).
     * 
     * @param orderId the order ID associated with this payment
     * @param amount the payment amount (must be positive)
     * @return provider reference string (transaction ID, authorization code, etc.)
     * @throws PaymentProcessingException if the payment provider rejects the payment
     *         or a communication error occurs
     */
    String processPayment(UUID orderId, BigDecimal amount) throws PaymentProcessingException;
}

