package com.example.app.product.controller;

import com.example.app.common.dto.PagedResponse;
import com.example.app.product.domain.ProductRequest;
import com.example.app.product.dto.ProductResponse;
import com.example.app.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ProductControllerTest.TestConfig.class})
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @Autowired
    private ProductController productController;

    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        productRequest = new ProductRequest();
        productRequest.setSku("SKU-001");
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setAvailableQty(100);

        productResponse = new ProductResponse();
        productResponse.setId(productId.toString());
        productResponse.setSku("SKU-001");
        productResponse.setName("Test Product");
        productResponse.setPrice(new BigDecimal("99.99"));
    }

    @Test
    void testCreateProduct_Success_ReturnsCreated() {
        // Arrange
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(productResponse);

        // Act
        ResponseEntity<ProductResponse> response = productController.createProduct(productRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId.toString(), response.getBody().getId());
        assertEquals("SKU-001", response.getBody().getSku());

        verify(productService).createProduct(any(ProductRequest.class));
    }

    @Test
    void testGetProductById_Success_ReturnsOk() {
        // Arrange
        when(productService.getProductById(productId)).thenReturn(productResponse);

        // Act
        ResponseEntity<ProductResponse> response = productController.getProductById(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId.toString(), response.getBody().getId());

        verify(productService).getProductById(productId);
    }

    @Test
    void testSearchProducts_WithSearchTerm_ReturnsOk() {
        // Arrange
        PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.singletonList(productResponse));
        pagedResponse.setTotalElements(1);
        pagedResponse.setTotalPages(1);

        when(productService.searchProducts(eq("test"), eq(0), eq(20))).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<ProductResponse>> response = 
            productController.searchProducts("test", 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(productService).searchProducts(eq("test"), eq(0), eq(20));
    }

    @Test
    void testSearchProducts_WithoutSearchTerm_ReturnsOk() {
        // Arrange
        PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.singletonList(productResponse));
        pagedResponse.setTotalElements(1);
        pagedResponse.setTotalPages(1);

        when(productService.searchProducts(eq(null), eq(0), eq(20))).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<ProductResponse>> response = 
            productController.searchProducts(null, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(productService).searchProducts(eq(null), eq(0), eq(20));
    }

    @Test
    void testSearchProducts_WithCustomPagination_ReturnsOk() {
        // Arrange
        PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.emptyList());
        pagedResponse.setTotalElements(0);
        pagedResponse.setTotalPages(0);

        when(productService.searchProducts(eq("query"), eq(2), eq(10))).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<ProductResponse>> response = 
            productController.searchProducts("query", 2, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(productService).searchProducts(eq("query"), eq(2), eq(10));
    }

    @Configuration
    @Import(ProductController.class)
    static class TestConfig {
        // Minimal configuration - only imports the controller under test
        // All dependencies are mocked via @MockBean
    }
}

