package com.example.app.product.mapper;

import com.example.app.product.dto.ProductResponse;
import com.example.app.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper productMapper;

    private Product product;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);
        productId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
        product.setSku("SKU-001");
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setAvailableQty(100);

        Date createdAt = new Date();
        Date updatedAt = new Date();
        product.setCreatedAt(createdAt);
        product.setUpdatedAt(updatedAt);
    }

    @Test
    void testToResponse_Success_MapsAllFields() {
        // Act
        ProductResponse response = productMapper.toResponse(product);

        // Assert
        assertNotNull(response);
        assertEquals(productId.toString(), response.getId());
        assertEquals("SKU-001", response.getSku());
        assertEquals("Test Product", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("99.99"), response.getPrice());
        assertEquals(100, response.getAvailableQty());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void testToResponse_DateConversion_ConvertsToInstant() {
        // Arrange
        Date testDate = new Date();
        product.setCreatedAt(testDate);
        product.setUpdatedAt(testDate);

        // Act
        ProductResponse response = productMapper.toResponse(product);

        // Assert
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(testDate.toInstant(), response.getCreatedAt());
        assertEquals(testDate.toInstant(), response.getUpdatedAt());
    }

    @Test
    void testToResponse_UUIDConversion_ConvertsToString() {
        // Act
        ProductResponse response = productMapper.toResponse(product);

        // Assert
        assertNotNull(response.getId());
        assertEquals(productId.toString(), response.getId());
        assertTrue(response.getId().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

}

