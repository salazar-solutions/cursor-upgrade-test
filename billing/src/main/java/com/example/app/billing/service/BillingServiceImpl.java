package com.example.app.billing.service;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;
import com.example.app.billing.mapper.PaymentMapper;
import com.example.app.payment.entity.Payment;
import com.example.app.payment.service.PaymentService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of billing service.
 * Uses PaymentService from payment module for payment processing.
 */
@Service
@Transactional
public class BillingServiceImpl implements BillingService {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    private final Counter paymentsAttemptedCounter;

    @Autowired
    public BillingServiceImpl(MeterRegistry meterRegistry) {
        this.paymentsAttemptedCounter = Counter.builder("payments.attempted")
            .description("Number of payment attempts")
            .register(meterRegistry);
    }
    
    // Setters for testing
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    public void setPaymentMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        paymentsAttemptedCounter.increment();
        
        // Use PaymentService from payment module to process payment
        Payment payment = paymentService.processPayment(request.getOrderId(), request.getAmount());

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentService.getPaymentById(id);
        return paymentMapper.toResponse(payment);
    }
}

