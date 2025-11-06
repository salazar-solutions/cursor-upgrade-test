package com.example.app.payment.service;

import com.example.app.payment.entity.Payment;
import com.example.app.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service interface for payment processing operations.
 * 
 * <p>This service handles the complete payment lifecycle, including payment creation,
 * processing with retry logic, and status management. Payments are processed through
 * external payment providers with automatic retry on failure.
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface PaymentService {
    /**
     * Processes a payment for an order with automatic retry logic.
     * 
     * <p>This method:
     * <ol>
     *   <li>Creates a payment record with PROCESSING status</li>
     *   <li>Attempts to process payment through the payment provider (up to 3 retries)</li>
     *   <li>Updates payment status to SUCCESS or FAILED based on result</li>
     *   <li>Stores provider reference if successful</li>
     * </ol>
     * 
     * <p>Retry behavior: On failure, waits progressively longer (100ms, 200ms, 300ms)
     * before retrying. After 3 failed attempts, payment is marked as FAILED.
     * 
     * @param orderId the order ID associated with this payment
     * @param amount the payment amount (must be positive)
     * @return Payment entity with final status (SUCCESS or FAILED)
     * @throws com.example.app.payment.provider.PaymentProcessingException if all retry attempts fail
     */
    Payment processPayment(UUID orderId, BigDecimal amount);
    
    /**
     * Retrieves a payment by its unique identifier.
     * 
     * @param id the payment's UUID
     * @return Payment entity with current status and details
     * @throws com.example.app.common.exception.EntityNotFoundException if payment is not found
     */
    Payment getPaymentById(UUID id);
    
    /**
     * Creates a payment record with a specified initial status.
     * 
     * <p>This is a lower-level method for creating payment records without
     * processing. Typically used internally by {@link #processPayment(UUID, BigDecimal)}.
     * 
     * @param orderId the order ID associated with this payment
     * @param amount the payment amount
     * @param status the initial payment status (typically PROCESSING)
     * @return created Payment entity
     */
    Payment createPayment(UUID orderId, BigDecimal amount, PaymentStatus status);
}

