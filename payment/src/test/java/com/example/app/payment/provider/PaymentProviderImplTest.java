package com.example.app.payment.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {PaymentProviderImpl.class})
class PaymentProviderImplTest {

    @Autowired
    private PaymentProviderImpl paymentProvider;

    private UUID orderId;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        amount = new BigDecimal("99.99");
    }

    @Test
    void testProcessPayment_Success_ReturnsProviderReference() throws PaymentProcessingException {
        // Act
        String result = paymentProvider.processPayment(orderId, amount);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("PROV-REF-"));
        assertTrue(result.contains(orderId.toString().substring(0, 8).toUpperCase()));
        assertTrue(result.length() > 20); // Should contain timestamp
    }

    @Test
    void testProcessPayment_NullOrderId_ThrowsPaymentProcessingException() {
        // Act & Assert
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class,
            () -> paymentProvider.processPayment(null, amount));

        assertEquals("Order ID cannot be null", exception.getMessage());
    }

    @Test
    void testProcessPayment_NullAmount_ThrowsPaymentProcessingException() {
        // Act & Assert
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class,
            () -> paymentProvider.processPayment(orderId, null));

        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void testProcessPayment_ZeroAmount_ThrowsPaymentProcessingException() {
        // Arrange
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // Act & Assert
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class,
            () -> paymentProvider.processPayment(orderId, zeroAmount));

        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void testProcessPayment_NegativeAmount_ThrowsPaymentProcessingException() {
        // Arrange
        BigDecimal negativeAmount = new BigDecimal("-10.00");

        // Act & Assert
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class,
            () -> paymentProvider.processPayment(orderId, negativeAmount));

        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void testProcessPayment_DifferentOrderIds_ReturnsDifferentReferences() throws PaymentProcessingException {
        // Arrange
        UUID orderId2 = UUID.randomUUID();

        // Act
        String ref1 = paymentProvider.processPayment(orderId, amount);
        String ref2 = paymentProvider.processPayment(orderId2, amount);

        // Assert
        assertNotNull(ref1);
        assertNotNull(ref2);
        assertNotEquals(ref1, ref2);
    }
}

