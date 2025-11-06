package com.example.app.order.controller;

import com.example.app.inventory.entity.Inventory;
import com.example.app.inventory.repository.InventoryRepository;
import com.example.app.order.domain.OrderRequest;
import com.example.app.order.domain.OrderLineRequest;
import com.example.app.product.entity.Product;
import com.example.app.product.repository.ProductRepository;
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
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration test for the order flow.
 * This test uses the local devdb database and should only run with the integration profile.
 * Ensure PostgreSQL is running and devdb database exists before running this test.
 */
@SpringBootTest(classes = {com.example.app.order.config.TestApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles({"integration", "local"})
@Transactional
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
class OrderFlowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear entity manager first to avoid tracking stale entities
        entityManager.clear();
        
        // Delete in order to respect foreign key constraints
        // Use deleteAllInBatch which uses SQL DELETE and won't fail if no rows exist
        inventoryRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void testEndToEndOrderFlow() throws Exception {
        // Create user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed");
        User savedUser = userRepository.save(user);

        // Create product
        Product product = new Product();
        product.setSku("SKU-001");
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setAvailableQty(100);
        Product savedProduct = productRepository.save(product);

        // Create inventory
        Inventory inventory = new Inventory();
        inventory.setProductId(savedProduct.getId());
        inventory.setAvailableQty(100);
        inventory.setReservedQty(0);
        inventoryRepository.save(inventory);

        // Create order
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(savedUser.getId());
        OrderLineRequest lineRequest = new OrderLineRequest();
        lineRequest.setProductId(savedProduct.getId());
        lineRequest.setQuantity(2);
        orderRequest.setOrderLines(Collections.singletonList(lineRequest));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}

