package com.example.app.order.controller;

import com.example.app.inventory.entity.Inventory;
import com.example.app.inventory.repository.InventoryRepository;
import com.example.app.order.domain.OrderLineRequest;
import com.example.app.order.domain.OrderRequest;
import com.example.app.order.domain.OrderStatusChangeRequest;
import com.example.app.order.entity.Order;
import com.example.app.order.entity.OrderStatus;
import com.example.app.order.repository.OrderRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OrderController including end-to-end flow tests.
 * These tests use the local devdb database and should only run with the integration profile.
 * Ensure PostgreSQL is running and devdb database exists before running these tests.
 */
@SpringBootTest(classes = {com.example.app.order.config.TestApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles({"integration", "local"})
@Transactional
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
class OrderControllerIT {

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

    @Autowired
    private OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User user;
    private Product product1;
    private Product product2;
    private Inventory inventory1;
    private Inventory inventory2;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Create user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed");
        user = userRepository.save(user);

        // Create products
        product1 = new Product();
        product1.setSku("SKU-001");
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setAvailableQty(100);
        product1 = productRepository.save(product1);

        product2 = new Product();
        product2.setSku("SKU-002");
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("49.99"));
        product2.setAvailableQty(50);
        product2 = productRepository.save(product2);

        // Create inventory
        inventory1 = new Inventory();
        inventory1.setProductId(product1.getId());
        inventory1.setAvailableQty(100);
        inventory1.setReservedQty(0);
        inventory1 = inventoryRepository.save(inventory1);

        inventory2 = new Inventory();
        inventory2.setProductId(product2.getId());
        inventory2.setAvailableQty(50);
        inventory2.setReservedQty(0);
        inventory2 = inventoryRepository.save(inventory2);
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setUserId(user.getId());
        OrderLineRequest line1 = new OrderLineRequest();
        line1.setProductId(product1.getId());
        line1.setQuantity(2);
        request.setOrderLines(Collections.singletonList(line1));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.orderLines").isArray())
                .andExpect(jsonPath("$.orderLines.length()").value(1));
        
        // Verify in database
        Order saved = orderRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(user.getId(), saved.getUserId());
        assertEquals(OrderStatus.PENDING, saved.getStatus());
    }

    @Test
    void testCreateOrder_MultipleProducts() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setUserId(user.getId());
        OrderLineRequest line1 = new OrderLineRequest();
        line1.setProductId(product1.getId());
        line1.setQuantity(2);
        OrderLineRequest line2 = new OrderLineRequest();
        line2.setProductId(product2.getId());
        line2.setQuantity(3);
        request.setOrderLines(java.util.Arrays.asList(line1, line2));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderLines.length()").value(2));
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        // Create order via repository for testing
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setStatus(OrderStatus.PENDING);
        Order saved = orderRepository.save(order);

        mockMvc.perform(get("/api/v1/orders/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/orders/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOrders_WithFilters() throws Exception {
        // Create multiple orders
        Order order1 = new Order();
        order1.setUserId(user.getId());
        order1.setTotalAmount(new BigDecimal("99.99"));
        order1.setStatus(OrderStatus.PENDING);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUserId(user.getId());
        order2.setTotalAmount(new BigDecimal("199.99"));
        order2.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order2);

        // Filter by status
        mockMvc.perform(get("/api/v1/orders")
                .param("status", "PENDING")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));

        // Filter by userId
        mockMvc.perform(get("/api/v1/orders")
                .param("userId", user.getId().toString())
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testChangeOrderStatus_Success() throws Exception {
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalAmount(new BigDecimal("99.99"));
        order.setStatus(OrderStatus.PENDING);
        Order saved = orderRepository.save(order);

        OrderStatusChangeRequest request = new OrderStatusChangeRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        mockMvc.perform(post("/api/v1/orders/{id}/status", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
        
        // Verify in database
        Order updated = orderRepository.findById(saved.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(OrderStatus.CONFIRMED, updated.getStatus());
    }

    @Test
    void testChangeOrderStatus_InvalidTransition() throws Exception {
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalAmount(new BigDecimal("99.99"));
        order.setStatus(OrderStatus.DELIVERED); // Terminal state
        Order saved = orderRepository.save(order);

        OrderStatusChangeRequest request = new OrderStatusChangeRequest();
        request.setStatus(OrderStatus.PENDING);

        mockMvc.perform(post("/api/v1/orders/{id}/status", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testCreateOrder_InsufficientStock() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setUserId(user.getId());
        OrderLineRequest line1 = new OrderLineRequest();
        line1.setProductId(product1.getId());
        line1.setQuantity(200); // More than available
        request.setOrderLines(Collections.singletonList(line1));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testEndToEndOrderFlow() throws Exception {
        // Clear entity manager first to avoid tracking stale entities
        entityManager.clear();
        
        // Delete in order to respect foreign key constraints
        // Use deleteAllInBatch which uses SQL DELETE and won't fail if no rows exist
        inventoryRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        // Create user
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashed");
        User savedUser = userRepository.save(testUser);

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

