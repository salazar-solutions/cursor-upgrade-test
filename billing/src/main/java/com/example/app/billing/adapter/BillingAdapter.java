package com.example.app.billing.adapter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Adapter interface for integrating the billing module with other modules (e.g., order module).
 * 
 * <p>This adapter provides a simplified interface for creating payments from external modules.
 * It abstracts the billing module's internal structure (PaymentRequest, PaymentResponse)
 * and provides a straightforward API for payment creation.
 * 
 * <p><b>Usage:</b> Typically used by orchestration modules (like order) that need to
 * create payments as part of their workflow without directly depending on billing DTOs.
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface BillingAdapter {
    /**
     * Creates a payment for an order.
     * 
     * <p>This method creates a PaymentRequest internally and delegates to BillingService
     * to process the payment. Returns the payment ID for tracking.
     * 
     * @param orderId the order ID associated with this payment
     * @param amount the payment amount
     * @return the UUID of the created payment
     * @throws com.example.app.common.exception.EntityNotFoundException if order not found
     */
    UUID createPayment(UUID orderId, BigDecimal amount);
}

