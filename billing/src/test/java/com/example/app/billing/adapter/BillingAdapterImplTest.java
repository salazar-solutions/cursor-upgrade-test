package com.example.app.billing.adapter;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;
import com.example.app.billing.service.BillingService;
import com.example.app.payment.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {BillingAdapterImpl.class})
class BillingAdapterImplTest {

    @MockBean
    private BillingService billingService;

    @Autowired
    private BillingAdapterImpl billingAdapter;

    private UUID orderId;
    private BigDecimal amount;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        amount = new BigDecimal("99.99");

        paymentResponse = new PaymentResponse();
        paymentResponse.setId(orderId.toString());
        paymentResponse.setStatus(PaymentStatus.SUCCESS);
        paymentResponse.setAmount(amount);
    }

    @Test
    void testCreatePayment_Success_ReturnsPaymentId() {
        // Arrange
        PaymentRequest expectedRequest = new PaymentRequest();
        expectedRequest.setOrderId(orderId);
        expectedRequest.setAmount(amount);

        when(billingService.createPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        // Act
        UUID result = billingAdapter.createPayment(orderId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result);

        verify(billingService).createPayment(argThat(request -> 
            request.getOrderId().equals(orderId) && 
            request.getAmount().equals(amount)
        ));
    }

    @Test
    void testCreatePayment_VerifyRequestConstruction() {
        // Arrange
        when(billingService.createPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        // Act
        billingAdapter.createPayment(orderId, amount);

        // Assert
        verify(billingService).createPayment(argThat(request -> {
            assertNotNull(request);
            assertEquals(orderId, request.getOrderId());
            assertEquals(amount, request.getAmount());
            return true;
        }));
    }

    @Test
    void testCreatePayment_VerifyUUIDConversion() {
        // Arrange
        UUID differentOrderId = UUID.randomUUID();
        PaymentResponse responseWithDifferentId = new PaymentResponse();
        responseWithDifferentId.setId(differentOrderId.toString());
        responseWithDifferentId.setStatus(PaymentStatus.SUCCESS);

        when(billingService.createPayment(any(PaymentRequest.class))).thenReturn(responseWithDifferentId);

        // Act
        UUID result = billingAdapter.createPayment(orderId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(differentOrderId, result);
    }
}

