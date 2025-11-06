package com.example.app.payment.service;

import com.example.app.order.entity.Order;
import com.example.app.order.repository.OrderRepository;
import com.example.app.payment.entity.Payment;
import com.example.app.payment.entity.PaymentStatus;
import com.example.app.payment.repository.PaymentRepository;
import com.example.app.user.entity.User;
import com.example.app.user.repository.UserRepository;
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Order order;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Create user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed");
        user = userRepository.save(user);

        // Create order
        order = new Order();
        order.setUserId(user.getId());
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setStatus(com.example.app.order.entity.OrderStatus.PENDING);
        order = orderRepository.save(order);
    }

    @Test
    void testProcessPayment_Success() {
        BigDecimal amount = new BigDecimal("199.98");
        
        Payment payment = paymentService.processPayment(order.getId(), amount);

        assertNotNull(payment);
        assertNotNull(payment.getId());
        assertEquals(order.getId(), payment.getOrderId());
        assertEquals(amount, payment.getAmount());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertNotNull(payment.getProviderRef());
        assertTrue(payment.getProviderRef().startsWith("PROV-REF-"));
    }

    @Test
    void testCreatePayment_WithInitialStatus() {
        BigDecimal amount = new BigDecimal("149.99");
        
        Payment payment = paymentService.createPayment(order.getId(), amount, PaymentStatus.PENDING);

        assertNotNull(payment);
        assertNotNull(payment.getId());
        assertEquals(order.getId(), payment.getOrderId());
        assertEquals(amount, payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
    }

    @Test
    void testGetPaymentById_Success() {
        BigDecimal amount = new BigDecimal("99.99");
        Payment createdPayment = paymentService.createPayment(order.getId(), amount, PaymentStatus.SUCCESS);
        createdPayment.setProviderRef("TEST-REF-123");
        paymentRepository.save(createdPayment);

        Payment retrievedPayment = paymentService.getPaymentById(createdPayment.getId());

        assertNotNull(retrievedPayment);
        assertEquals(createdPayment.getId(), retrievedPayment.getId());
        assertEquals(order.getId(), retrievedPayment.getOrderId());
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
        
        Payment payment = paymentService.processPayment(order.getId(), amount);

        assertNotNull(payment);
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertNotNull(payment.getProviderRef());
        
        // Verify payment is persisted
        Payment saved = paymentRepository.findById(payment.getId()).orElse(null);
        assertNotNull(saved);
        assertEquals(amount, saved.getAmount());
    }
}

