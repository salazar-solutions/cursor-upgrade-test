package com.example.app.billing.adapter;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;
import com.example.app.billing.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementation of BillingAdapter for order module integration.
 */
@Component
public class BillingAdapterImpl implements BillingAdapter {
    
    @Autowired
    private BillingService billingService;

    @Override
    public UUID createPayment(UUID orderId, BigDecimal amount) {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(amount);
        
        PaymentResponse response = billingService.createPayment(request);
        return UUID.fromString(response.getId());
    }
}

