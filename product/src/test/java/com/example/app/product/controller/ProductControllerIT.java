package com.example.app.product.controller;

import com.example.app.product.domain.ProductRequest;
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
 * Integration tests for ProductController.
 * These tests use the local devdb database and should only run with the integration profile.
 * Ensure PostgreSQL is running and devdb database exists before running these tests.
 */
@SpringBootTest(classes = {com.example.app.product.config.TestApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles({"integration", "local"})
@Transactional
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setSku("SKU-001");
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setPrice(new BigDecimal("99.99"));
        request.setAvailableQty(100);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.sku").value("SKU-001"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.availableQty").value(100));
        
        // Verify in database
        Product saved = productRepository.findBySku("SKU-001").orElse(null);
        assertNotNull(saved);
        assertEquals("Test Product", saved.getName());
    }

    @Test
    void testGetProductById_Success() throws Exception {
        Product product = new Product();
        product.setSku("SKU-001");
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setAvailableQty(100);
        Product saved = productRepository.save(product);

        mockMvc.perform(get("/api/v1/products/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.sku").value("SKU-001"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/products/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchProducts_WithSearchTerm() throws Exception {
        // Create products
        Product product1 = new Product();
        product1.setSku("SKU-001");
        product1.setName("Laptop Computer");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setAvailableQty(10);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setSku("SKU-002");
        product2.setName("Desktop Computer");
        product2.setPrice(new BigDecimal("799.99"));
        product2.setAvailableQty(5);
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setSku("SKU-003");
        product3.setName("Mouse");
        product3.setPrice(new BigDecimal("19.99"));
        product3.setAvailableQty(50);
        productRepository.save(product3);

        // Search for "Computer"
        mockMvc.perform(get("/api/v1/products")
                .param("search", "Computer")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testSearchProducts_WithoutSearchTerm() throws Exception {
        // Create multiple products
        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setSku("SKU-" + String.format("%03d", i));
            product.setName("Product " + i);
            product.setPrice(new BigDecimal("99.99"));
            product.setAvailableQty(100);
            productRepository.save(product);
        }

        mockMvc.perform(get("/api/v1/products")
                .param("page", "0")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    @Test
    void testCreateProduct_ValidationError() throws Exception {
        ProductRequest request = new ProductRequest();
        // Missing required fields
        request.setSku("");

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_DuplicateSku() throws Exception {
        // Create first product
        Product product = new Product();
        product.setSku("SKU-001");
        product.setName("Original Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setAvailableQty(100);
        productRepository.save(product);
        entityManager.flush();
        entityManager.clear();

        // Try to create duplicate SKU
        ProductRequest request = new ProductRequest();
        request.setSku("SKU-001");
        request.setName("Duplicate Product");
        request.setPrice(new BigDecimal("199.99"));
        request.setAvailableQty(50);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
