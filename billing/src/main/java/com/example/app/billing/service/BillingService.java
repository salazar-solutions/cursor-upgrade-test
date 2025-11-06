package com.example.app.billing.service;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;

import java.util.UUID;

/**
 * Service interface for billing operations.
 */
public interface BillingService {
    PaymentResponse createPayment(PaymentRequest request);
    PaymentResponse getPaymentById(UUID id);
}

