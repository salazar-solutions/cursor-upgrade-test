package com.example.app.product.service;

import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.product.domain.ProductRequest;
import com.example.app.product.dto.ProductResponse;
import com.example.app.product.entity.Product;
import com.example.app.product.mapper.ProductMapper;
import com.example.app.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private InventoryInitializationService inventoryInitializationService;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;
    private Product product;
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

        product = new Product();
        product.setId(productId);
        product.setSku("SKU-001");
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setAvailableQty(100);
    }

    @Test
    void testCreateProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(createProductResponse());
        doNothing().when(inventoryInitializationService).ensureInventoryExists(any(UUID.class), anyInt());

        ProductResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        verify(productRepository).save(any(Product.class));
        verify(inventoryInitializationService).ensureInventoryExists(any(UUID.class), anyInt());
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(createProductResponse());

        ProductResponse response = productService.getProductById(productId);

        assertNotNull(response);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(productId));
    }

    @Test
    void testSearchProducts() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        
        when(productRepository.searchProducts(anyString(), eq(pageable))).thenReturn(productPage);
        when(productMapper.toResponse(any(Product.class))).thenReturn(createProductResponse());

        com.example.app.common.dto.PagedResponse<ProductResponse> response = 
            productService.searchProducts("test", 0, 20);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    private ProductResponse createProductResponse() {
        ProductResponse response = new ProductResponse();
        response.setId(productId.toString());
        response.setSku("SKU-001");
        response.setName("Test Product");
        return response;
    }
}

