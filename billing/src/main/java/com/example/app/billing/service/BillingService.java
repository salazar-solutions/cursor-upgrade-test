package com.example.app.billing.service;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;

import java.util.UUID;

/**
 * Service interface for billing and payment orchestration operations.
 * 
 * <p>This service acts as a facade for payment processing, coordinating with
 * the payment module to process payments and track payment status. It provides
 * a higher-level API for order-related payment operations.
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface BillingService {
    /**
     * Creates and processes a payment for an order.
     * 
     * <p>This operation delegates to the payment module to process the payment
     * and returns a payment response with status and provider reference.
     * 
     * @param request the payment request containing order ID and amount
     * @return PaymentResponse containing payment details and status
     * @throws com.example.app.common.exception.EntityNotFoundException if order not found
     */
    PaymentResponse createPayment(PaymentRequest request);
    
    /**
     * Retrieves a payment by its unique identifier.
     * 
     * @param id the payment's UUID
     * @return PaymentResponse containing payment details
     * @throws com.example.app.common.exception.EntityNotFoundException if payment is not found
     */
    PaymentResponse getPaymentById(UUID id);
}

