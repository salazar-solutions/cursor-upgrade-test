package com.example.app.billing.controller;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.payment.entity.Payment;
import com.example.app.payment.entity.PaymentStatus;
import com.example.app.payment.repository.PaymentRepository;
import com.example.app.user.entity.User;
import com.example.app.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for BillingController.
 * These tests use the local devdb database and should only run with the integration profile.
 * Ensure PostgreSQL is running and devdb database exists before running these tests.
 */
@SpringBootTest(classes = {com.example.app.billing.config.TestApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles({"integration", "local"})
@Transactional
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
class BillingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private UUID orderId;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        userRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Create user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed");
        user = userRepository.save(user);

        // Generate a test order ID (order entity not needed for billing tests)
        orderId = UUID.randomUUID();
    }

    @Test
    void testCreatePayment_Success() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(new BigDecimal("199.98"));

        mockMvc.perform(post("/api/v1/billing/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.amount").value(199.98))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
        
        // Verify in database
        Payment saved = paymentRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(orderId, saved.getOrderId());
        assertEquals(PaymentStatus.SUCCESS, saved.getStatus());
    }

    @Test
    void testGetPaymentById_Success() throws Exception {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(new BigDecimal("199.98"));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProviderRef("MOCK-REF-123");
        Payment saved = paymentRepository.save(payment);

        mockMvc.perform(get("/api/v1/billing/payments/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.amount").value(199.98))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetPaymentById_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/billing/payments/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatePayment_ValidationError() throws Exception {
        PaymentRequest request = new PaymentRequest();
        // Missing required fields
        request.setOrderId(null);

        mockMvc.perform(post("/api/v1/billing/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePayment_WithRetry() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(new BigDecimal("299.99"));

        // Payment should be created successfully
        mockMvc.perform(post("/api/v1/billing/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
        
        // Verify payment record exists
        Payment saved = paymentRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(new BigDecimal("299.99"), saved.getAmount());
    }

    @Test
    void testCreatePayment_InvalidAmount_Zero() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(BigDecimal.ZERO); // Invalid: must be greater than 0

        mockMvc.perform(post("/api/v1/billing/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePayment_InvalidAmount_Negative() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(new BigDecimal("-10.00")); // Invalid: negative amount

        mockMvc.perform(post("/api/v1/billing/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePayment_InvalidAmount_Null() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(null); // Invalid: null amount

        mockMvc.perform(post("/api/v1/billing/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

