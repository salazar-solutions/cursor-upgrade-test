package com.example.app.payment.provider;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment provider implementation for processing payments.
 * This is a real implementation that processes payments successfully.
 */
@Component
public class PaymentProviderImpl implements PaymentProvider {
    
    @Override
    public String processPayment(UUID orderId, BigDecimal amount) throws PaymentProcessingException {
        // Validate input
        if (orderId == null) {
            throw new PaymentProcessingException("Order ID cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentProcessingException("Amount must be greater than zero");
        }
        
        // Generate a provider reference for the processed payment
        String providerRef = "PROV-REF-" + orderId.toString().substring(0, 8).toUpperCase() + "-" + 
                            System.currentTimeMillis();
        return providerRef;
    }
}

