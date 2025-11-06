package com.example.app.billing.adapter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Adapter interface for billing module integration.
 */
public interface BillingAdapter {
    UUID createPayment(UUID orderId, BigDecimal amount);
}

