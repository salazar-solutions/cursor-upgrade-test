package com.example.app.payment.service;

import com.example.app.payment.entity.Payment;
import com.example.app.payment.entity.PaymentStatus;
import com.example.app.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PaymentService.
 * These tests use the local devdb database and should only run with the integration profile.
 * Ensure PostgreSQL is running and devdb database exists before running these tests.
 */
@SpringBootTest(classes = {com.example.app.payment.config.TestApplication.class})
@ActiveProfiles({"integration", "local"})
@Transactional
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
class PaymentServiceIT {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private UUID orderId;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Use a random order ID for testing (payment service doesn't validate order existence)
        orderId = UUID.randomUUID();
    }

    @Test
    void testProcessPayment_Success() {
        BigDecimal amount = new BigDecimal("199.98");
        
        Payment payment = paymentService.processPayment(orderId, amount);

        assertNotNull(payment);
        assertNotNull(payment.getId());
        assertEquals(orderId, payment.getOrderId());
        assertEquals(amount, payment.getAmount());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertNotNull(payment.getProviderRef());
        assertTrue(payment.getProviderRef().startsWith("PROV-REF-"));
    }

    @Test
    void testCreatePayment_WithInitialStatus() {
        BigDecimal amount = new BigDecimal("149.99");
        
        Payment payment = paymentService.createPayment(orderId, amount, PaymentStatus.PENDING);

        assertNotNull(payment);
        assertNotNull(payment.getId());
        assertEquals(orderId, payment.getOrderId());
        assertEquals(amount, payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
    }

    @Test
    void testGetPaymentById_Success() {
        BigDecimal amount = new BigDecimal("99.99");
        Payment createdPayment = paymentService.createPayment(orderId, amount, PaymentStatus.SUCCESS);
        createdPayment.setProviderRef("TEST-REF-123");
        paymentRepository.save(createdPayment);

        Payment retrievedPayment = paymentService.getPaymentById(createdPayment.getId());

        assertNotNull(retrievedPayment);
        assertEquals(createdPayment.getId(), retrievedPayment.getId());
        assertEquals(orderId, retrievedPayment.getOrderId());
        assertEquals(amount, retrievedPayment.getAmount());
        assertEquals(PaymentStatus.SUCCESS, retrievedPayment.getStatus());
        assertEquals("TEST-REF-123", retrievedPayment.getProviderRef());
    }

    @Test
    void testGetPaymentById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        
        assertThrows(com.example.app.common.exception.EntityNotFoundException.class, () -> {
            paymentService.getPaymentById(nonExistentId);
        });
    }

    @Test
    void testProcessPayment_WithRetry() {
        BigDecimal amount = new BigDecimal("299.99");
        
        Payment payment = paymentService.processPayment(orderId, amount);

        assertNotNull(payment);
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertNotNull(payment.getProviderRef());
        
        // Verify payment is persisted
        Payment saved = paymentRepository.findById(payment.getId()).orElse(null);
        assertNotNull(saved);
        assertEquals(amount, saved.getAmount());
    }
}

