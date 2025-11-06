package com.example.app.billing.mapper;

import com.example.app.billing.dto.PaymentResponse;
import com.example.app.payment.entity.Payment;
import com.example.app.payment.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {PaymentMapperImpl.class})
class PaymentMapperTest {

    @Autowired
    private PaymentMapper paymentMapper;

    private Payment payment;
    private UUID paymentId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        payment = new Payment();
        payment.setId(paymentId);
        payment.setOrderId(orderId);
        payment.setAmount(new BigDecimal("99.99"));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProviderRef("PROV-REF-123");
        Date createdAt = new Date();
        payment.setCreatedAt(createdAt);
    }

    @Test
    void testToResponse_Success_MapsAllFields() {
        // Act
        PaymentResponse response = paymentMapper.toResponse(payment);

        // Assert
        assertNotNull(response);
        assertEquals(paymentId.toString(), response.getId());
        assertEquals(orderId.toString(), response.getOrderId());
        assertEquals(new BigDecimal("99.99"), response.getAmount());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals("PROV-REF-123", response.getProviderRef());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void testToResponse_DateConversion_ConvertsToInstant() {
        // Arrange
        Date testDate = new Date();
        payment.setCreatedAt(testDate);

        // Act
        PaymentResponse response = paymentMapper.toResponse(payment);

        // Assert
        assertNotNull(response.getCreatedAt());
        assertEquals(testDate.toInstant(), response.getCreatedAt());
    }

    @Test
    void testToResponse_UUIDConversion_ConvertsToString() {
        // Act
        PaymentResponse response = paymentMapper.toResponse(payment);

        // Assert
        assertNotNull(response.getId());
        assertNotNull(response.getOrderId());
        assertEquals(paymentId.toString(), response.getId());
        assertEquals(orderId.toString(), response.getOrderId());
    }

    @Test
    void testToResponse_DifferentStatuses_MapsCorrectly() {
        // Arrange
        payment.setStatus(PaymentStatus.FAILED);

        // Act
        PaymentResponse response = paymentMapper.toResponse(payment);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getStatus());
    }

}

