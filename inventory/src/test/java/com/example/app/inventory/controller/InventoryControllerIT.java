package com.example.app.inventory.controller;

import com.example.app.inventory.domain.ReleaseRequest;
import com.example.app.inventory.domain.ReserveRequest;
import com.example.app.inventory.entity.Inventory;
import com.example.app.inventory.repository.InventoryRepository;
import com.example.app.product.entity.Product;
import com.example.app.product.repository.ProductRepository;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for InventoryController.
 * These tests use the local devdb database and should only run with the integration profile.
 * Ensure PostgreSQL is running and devdb database exists before running these tests.
 */
@SpringBootTest(classes = {com.example.app.inventory.config.TestApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles({"integration", "local"})
@Transactional
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
class InventoryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Product product;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Create product
        product = new Product();
        product.setSku("SKU-001");
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setAvailableQty(100);
        product = productRepository.save(product);

        // Create inventory
        inventory = new Inventory();
        inventory.setProductId(product.getId());
        inventory.setAvailableQty(100);
        inventory.setReservedQty(0);
        inventory = inventoryRepository.save(inventory);
    }

    @Test
    void testGetInventory_Success() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{productId}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(product.getId().toString()))
                .andExpect(jsonPath("$.availableQty").value(100))
                .andExpect(jsonPath("$.reservedQty").value(0));
    }

    @Test
    void testGetInventory_NotFound() throws Exception {
        UUID nonExistentProductId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/inventory/{productId}", nonExistentProductId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testReserveInventory_Success() throws Exception {
        ReserveRequest request = new ReserveRequest();
        request.setQuantity(10);

        mockMvc.perform(post("/api/v1/inventory/{productId}/reserve", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQty").value(90))
                .andExpect(jsonPath("$.reservedQty").value(10));
        
        // Verify in database
        Inventory updated = inventoryRepository.findById(product.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(90, updated.getAvailableQty());
        assertEquals(10, updated.getReservedQty());
    }

    @Test
    void testReserveInventory_InsufficientStock() throws Exception {
        ReserveRequest request = new ReserveRequest();
        request.setQuantity(150); // More than available

        mockMvc.perform(post("/api/v1/inventory/{productId}/reserve", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testReleaseInventory_Success() throws Exception {
        // First reserve some inventory
        inventory.setAvailableQty(80);
        inventory.setReservedQty(20);
        inventoryRepository.save(inventory);

        ReleaseRequest request = new ReleaseRequest();
        request.setQuantity(10);

        mockMvc.perform(post("/api/v1/inventory/{productId}/release", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQty").value(90))
                .andExpect(jsonPath("$.reservedQty").value(10));
        
        // Verify in database
        Inventory updated = inventoryRepository.findById(product.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(90, updated.getAvailableQty());
        assertEquals(10, updated.getReservedQty());
    }

    @Test
    void testReleaseInventory_MoreThanReserved() throws Exception {
        inventory.setAvailableQty(80);
        inventory.setReservedQty(20);
        inventoryRepository.save(inventory);

        ReleaseRequest request = new ReleaseRequest();
        request.setQuantity(30); // More than reserved

        mockMvc.perform(post("/api/v1/inventory/{productId}/release", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testReserveAndReleaseFlow() throws Exception {
        // Reserve
        ReserveRequest reserveRequest = new ReserveRequest();
        reserveRequest.setQuantity(20);
        mockMvc.perform(post("/api/v1/inventory/{productId}/reserve", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isOk());

        // Verify state
        Inventory afterReserve = inventoryRepository.findById(product.getId()).orElse(null);
        assertNotNull(afterReserve);
        assertEquals(80, afterReserve.getAvailableQty());
        assertEquals(20, afterReserve.getReservedQty());

        // Release part
        ReleaseRequest releaseRequest = new ReleaseRequest();
        releaseRequest.setQuantity(10);
        mockMvc.perform(post("/api/v1/inventory/{productId}/release", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(releaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQty").value(90))
                .andExpect(jsonPath("$.reservedQty").value(10));
    }
}

