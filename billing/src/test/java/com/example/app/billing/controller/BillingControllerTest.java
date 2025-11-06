package com.example.app.billing.controller;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;
import com.example.app.billing.service.BillingService;
import com.example.app.payment.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {BillingControllerTest.TestConfig.class})
class BillingControllerTest {

    @MockBean
    private BillingService billingService;

    @Autowired
    private BillingController billingController;

    private UUID paymentId;
    private UUID orderId;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(orderId);
        paymentRequest.setAmount(new BigDecimal("99.99"));

        paymentResponse = new PaymentResponse();
        paymentResponse.setId(paymentId.toString());
        paymentResponse.setOrderId(orderId.toString());
        paymentResponse.setStatus(PaymentStatus.SUCCESS);
        paymentResponse.setAmount(new BigDecimal("99.99"));
    }

    @Test
    void testCreatePayment_Success_ReturnsCreated() {
        // Arrange
        when(billingService.createPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        // Act
        ResponseEntity<PaymentResponse> response = billingController.createPayment(paymentRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(paymentId.toString(), response.getBody().getId());
        assertEquals(PaymentStatus.SUCCESS, response.getBody().getStatus());

        verify(billingService).createPayment(any(PaymentRequest.class));
    }

    @Test
    void testGetPaymentById_Success_ReturnsOk() {
        // Arrange
        when(billingService.getPaymentById(paymentId)).thenReturn(paymentResponse);

        // Act
        ResponseEntity<PaymentResponse> response = billingController.getPaymentById(paymentId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(paymentId.toString(), response.getBody().getId());

        verify(billingService).getPaymentById(paymentId);
    }

    @Configuration
    @Import(BillingController.class)
    static class TestConfig {
        // Minimal configuration - only imports the controller under test
        // All dependencies are mocked via @MockBean
    }
}

