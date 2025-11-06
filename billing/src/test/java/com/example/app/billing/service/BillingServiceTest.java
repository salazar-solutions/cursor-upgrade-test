package com.example.app.billing.service;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;
import com.example.app.billing.mapper.PaymentMapper;
import com.example.app.payment.entity.Payment;
import com.example.app.payment.entity.PaymentStatus;
import com.example.app.payment.service.PaymentService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentMapper paymentMapper;

    private BillingServiceImpl billingService;
    private MeterRegistry meterRegistry;

    private PaymentRequest paymentRequest;
    private Payment payment;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        billingService = new BillingServiceImpl(meterRegistry);
        // Manually inject mocks
        billingService.setPaymentService(paymentService);
        billingService.setPaymentMapper(paymentMapper);

        paymentId = UUID.randomUUID();
        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(UUID.randomUUID());
        paymentRequest.setAmount(new BigDecimal("99.99"));

        payment = new Payment();
        payment.setId(paymentId);
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProviderRef("PROV-REF-123");
    }

    @Test
    void testCreatePayment_Success() {
        when(paymentService.processPayment(any(UUID.class), any(BigDecimal.class))).thenReturn(payment);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(createPaymentResponse());

        PaymentResponse response = billingService.createPayment(paymentRequest);

        assertNotNull(response);
        verify(paymentService).processPayment(any(UUID.class), any(BigDecimal.class));
    }

    @Test
    void testGetPaymentById() {
        when(paymentService.getPaymentById(paymentId)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(createPaymentResponse());

        PaymentResponse response = billingService.getPaymentById(paymentId);

        assertNotNull(response);
    }

    private PaymentResponse createPaymentResponse() {
        PaymentResponse response = new PaymentResponse();
        response.setId(paymentId.toString());
        response.setStatus(PaymentStatus.SUCCESS);
        return response;
    }
}

