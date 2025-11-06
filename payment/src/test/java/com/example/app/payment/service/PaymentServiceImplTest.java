package com.example.app.payment.service;

import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.payment.entity.Payment;
import com.example.app.payment.entity.PaymentStatus;
import com.example.app.payment.provider.PaymentProcessingException;
import com.example.app.payment.provider.PaymentProvider;
import com.example.app.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {PaymentServiceImpl.class})
class PaymentServiceImplTest {

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private PaymentProvider paymentProvider;

    @Autowired
    private PaymentServiceImpl paymentService;

    private UUID orderId;
    private UUID paymentId;
    private BigDecimal amount;
    private Payment payment;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        paymentId = UUID.randomUUID();
        amount = new BigDecimal("99.99");

        payment = new Payment();
        payment.setId(paymentId);
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PROCESSING);
    }

    @Test
    void testProcessPayment_Success_ReturnsPaymentWithSuccessStatus() throws PaymentProcessingException {
        // Arrange
        String providerRef = "PROV-REF-123";
        Payment savedPayment = new Payment();
        savedPayment.setId(paymentId);
        savedPayment.setOrderId(orderId);
        savedPayment.setAmount(amount);
        savedPayment.setStatus(PaymentStatus.SUCCESS);
        savedPayment.setProviderRef(providerRef);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId(paymentId);
            }
            return p;
        });
        when(paymentProvider.processPayment(orderId, amount)).thenReturn(providerRef);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        Payment result = paymentService.processPayment(orderId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(providerRef, result.getProviderRef());
        assertEquals(orderId, result.getOrderId());
        assertEquals(amount, result.getAmount());

        verify(paymentRepository, atLeast(2)).save(any(Payment.class));
        verify(paymentProvider).processPayment(orderId, amount);
    }

    @Test
    void testProcessPayment_FailureAfterMaxRetries_ReturnsPaymentWithFailedStatus() throws PaymentProcessingException {
        // Arrange
        Payment savedPayment = new Payment();
        savedPayment.setId(paymentId);
        savedPayment.setOrderId(orderId);
        savedPayment.setAmount(amount);
        savedPayment.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId(paymentId);
            }
            return p;
        });
        when(paymentProvider.processPayment(orderId, amount))
            .thenThrow(new PaymentProcessingException("Payment failed"));

        // Act
        Payment result = paymentService.processPayment(orderId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.FAILED, result.getStatus());
        assertNull(result.getProviderRef());

        // Verify retry logic - should be called 3 times (MAX_RETRIES)
        verify(paymentProvider, times(3)).processPayment(orderId, amount);
        verify(paymentRepository, atLeast(2)).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_SuccessOnSecondAttempt_ReturnsPaymentWithSuccessStatus() throws PaymentProcessingException {
        // Arrange
        String providerRef = "PROV-REF-123";
        Payment savedPayment = new Payment();
        savedPayment.setId(paymentId);
        savedPayment.setOrderId(orderId);
        savedPayment.setAmount(amount);
        savedPayment.setStatus(PaymentStatus.SUCCESS);
        savedPayment.setProviderRef(providerRef);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId(paymentId);
            }
            return p;
        });
        when(paymentProvider.processPayment(orderId, amount))
            .thenThrow(new PaymentProcessingException("First attempt failed"))
            .thenReturn(providerRef);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        Payment result = paymentService.processPayment(orderId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(providerRef, result.getProviderRef());

        // Verify retry logic - should be called 2 times (failed once, then succeeded)
        verify(paymentProvider, times(2)).processPayment(orderId, amount);
    }

    @Test
    void testProcessPayment_InterruptedDuringRetry_ReturnsPaymentWithFailedStatus() throws PaymentProcessingException {
        // Arrange
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId(paymentId);
            }
            return p;
        });
        when(paymentProvider.processPayment(orderId, amount))
            .thenThrow(new PaymentProcessingException("Payment failed"));

        // Interrupt the current thread to simulate interruption
        Thread.currentThread().interrupt();

        // Act
        Payment result = paymentService.processPayment(orderId, amount);

        // Assert
        assertNotNull(result);
        // Should stop retrying after interruption
        verify(paymentProvider, atMost(3)).processPayment(orderId, amount);

        // Clear interrupt flag
        Thread.interrupted();
    }

    @Test
    void testGetPaymentById_Success_ReturnsPayment() {
        // Arrange
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act
        Payment result = paymentService.getPaymentById(paymentId);

        // Assert
        assertNotNull(result);
        assertEquals(paymentId, result.getId());
        assertEquals(orderId, result.getOrderId());
        assertEquals(amount, result.getAmount());

        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void testGetPaymentById_NotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> paymentService.getPaymentById(paymentId));

        assertEquals("Payment not found with id: " + paymentId, exception.getMessage());
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void testCreatePayment_Success_ReturnsCreatedPayment() {
        // Arrange
        PaymentStatus status = PaymentStatus.PROCESSING;
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(paymentId);
            return p;
        });

        // Act
        Payment result = paymentService.createPayment(orderId, amount, status);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(amount, result.getAmount());
        assertEquals(status, result.getStatus());

        verify(paymentRepository).save(any(Payment.class));
    }
}

