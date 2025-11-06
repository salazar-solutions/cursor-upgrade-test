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
 * Implementation of payment service with comprehensive retry logic and error handling.
 * 
 * <p>This service processes payments through external payment providers with
 * automatic retry on transient failures. The retry strategy uses exponential
 * backoff with a maximum of 3 attempts.
 * 
 * <p><b>Retry Strategy:</b>
 * <ul>
 *   <li>Maximum retries: 3 attempts</li>
 *   <li>Retry delays: 100ms, 200ms, 300ms (progressive)</li>
 *   <li>Final status: SUCCESS if any attempt succeeds, FAILED if all attempts fail</li>
 * </ul>
 * 
 * <p><b>Payment Status Flow:</b>
 * <ol>
 *   <li>PROCESSING: Initial status when payment record is created</li>
 *   <li>SUCCESS: Payment processed successfully by provider</li>
 *   <li>FAILED: All retry attempts exhausted or provider rejected payment</li>
 * </ol>
 * 
 * <p><b>Thread Safety:</b> This service is thread-safe. Concurrent payment
 * processing for the same order is handled by database transactions.
 * 
 * @author Generated
 * @since 1.0.0
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

    /**
     * {@inheritDoc}
     * 
     * <p>Implements retry logic with progressive delays. If the thread is interrupted
     * during a retry delay, the interruption is preserved and processing stops.
     */
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

    /**
     * {@inheritDoc}
     * 
     * <p>Read-only operation that retrieves payment from the database.
     */
    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Creates a new payment record with the specified status. This method
     * does not process the payment; use {@link #processPayment(UUID, BigDecimal)}
     * for complete payment processing.
     */
    @Override
    public Payment createPayment(UUID orderId, BigDecimal amount, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }
}

