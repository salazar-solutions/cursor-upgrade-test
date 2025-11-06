package com.example.app.billing.adapter;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;
import com.example.app.billing.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementation of BillingAdapter that bridges the billing module with external callers.
 * 
 * <p>This adapter converts simple parameters (orderId, amount) into the billing module's
 * internal request/response DTOs, allowing other modules to create payments without
 * depending on billing-specific classes.
 * 
 * <p><b>Integration Pattern:</b> This follows the Adapter pattern, providing a stable
 * interface for cross-module communication while allowing the billing module's internal
 * API to evolve independently.
 * 
 * @author Generated
 * @since 1.0.0
 */
@Component
public class BillingAdapterImpl implements BillingAdapter {
    
    @Autowired
    private BillingService billingService;

    /**
     * {@inheritDoc}
     * 
     * <p>Converts the parameters into a PaymentRequest, calls BillingService,
     * and extracts the payment ID from the PaymentResponse.
     */
    @Override
    public UUID createPayment(UUID orderId, BigDecimal amount) {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(amount);
        
        PaymentResponse response = billingService.createPayment(request);
        return UUID.fromString(response.getId());
    }
}

