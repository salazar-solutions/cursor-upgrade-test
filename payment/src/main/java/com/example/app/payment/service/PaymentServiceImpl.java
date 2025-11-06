package com.example.app.payment.service;

import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.payment.entity.Payment;
import com.example.app.payment.entity.PaymentStatus;
import com.example.app.payment.provider.PaymentProcessingException;
import com.example.app.payment.provider.PaymentProvider;
import com.example.app.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementation of payment service with retry logic.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PaymentProvider paymentProvider;
    
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 100;

    @Override
    public Payment processPayment(UUID orderId, BigDecimal amount) {
        // Create payment record with PROCESSING status
        Payment payment = createPayment(orderId, amount, PaymentStatus.PROCESSING);
        
        // Retry logic for payment processing
        String providerRef = null;
        PaymentStatus finalStatus = PaymentStatus.FAILED;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                providerRef = paymentProvider.processPayment(orderId, amount);
                finalStatus = PaymentStatus.SUCCESS;
                break;
            } catch (PaymentProcessingException e) {
                if (attempt == MAX_RETRIES) {
                    // Final attempt failed, keep FAILED status
                    finalStatus = PaymentStatus.FAILED;
                } else {
                    // Wait before retry
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        payment.setStatus(finalStatus);
        payment.setProviderRef(providerRef);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
    }

    @Override
    public Payment createPayment(UUID orderId, BigDecimal amount, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }
}

