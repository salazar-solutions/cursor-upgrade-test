package com.example.app.payment.service;

import com.example.app.payment.entity.Payment;
import com.example.app.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service interface for payment operations.
 */
public interface PaymentService {
    /**
     * Process a payment for an order.
     * @param orderId Order ID
     * @param amount Payment amount
     * @return Processed Payment entity
     */
    Payment processPayment(UUID orderId, BigDecimal amount);
    
    /**
     * Get payment by ID.
     * @param id Payment ID
     * @return Payment entity
     */
    Payment getPaymentById(UUID id);
    
    /**
     * Create a payment record with initial status.
     * @param orderId Order ID
     * @param amount Payment amount
     * @param status Initial payment status
     * @return Created Payment entity
     */
    Payment createPayment(UUID orderId, BigDecimal amount, PaymentStatus status);
}

