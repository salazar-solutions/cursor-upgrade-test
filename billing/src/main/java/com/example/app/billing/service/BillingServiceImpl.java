package com.example.app.billing.service;

import com.example.app.billing.adapter.OrderAdapter;
import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;
import com.example.app.billing.mapper.PaymentMapper;
import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.payment.entity.Payment;
import com.example.app.payment.service.PaymentService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of billing service that orchestrates payment processing.
 * 
 * <p>This service acts as an adapter layer between the billing module and the
 * payment module, providing payment orchestration with metrics tracking.
 * 
 * <p><b>Metrics:</b> Payment attempts are tracked via Micrometer counter "payments.attempted".
 * 
 * <p><b>Integration:</b> Delegates actual payment processing to {@link PaymentService}
 * from the payment module, which handles retry logic and provider communication.
 * 
 * @author Generated
 * @since 1.0.0
 */
@Service
@Transactional
public class BillingServiceImpl implements BillingService {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired(required = false)
    private OrderAdapter orderAdapter;
    
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

    /**
     * {@inheritDoc}
     * 
     * <p>Increments the payment attempt counter, then delegates to PaymentService
     * for actual processing. The payment may be in PROCESSING, SUCCESS, or FAILED
     * status depending on the processing result.
     */
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        // Validate that the order exists (if adapter is available)
        if (orderAdapter != null && !orderAdapter.orderExists(request.getOrderId())) {
            throw new EntityNotFoundException("Order not found with id: " + request.getOrderId());
        }
        
        paymentsAttemptedCounter.increment();
        
        // Use PaymentService from payment module to process payment
        Payment payment = paymentService.processPayment(request.getOrderId(), request.getAmount());

        return paymentMapper.toResponse(payment);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Read-only operation that retrieves payment information from the payment module.
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentService.getPaymentById(id);
        return paymentMapper.toResponse(payment);
    }
}

