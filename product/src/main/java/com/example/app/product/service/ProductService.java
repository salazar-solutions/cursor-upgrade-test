package com.example.app.product.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.product.domain.ProductRequest;
import com.example.app.product.dto.ProductResponse;

import java.util.UUID;

/**
 * Service interface for product catalog management operations.
 * 
 * <p>This service provides CRUD operations for products, including creation,
 * retrieval, and search functionality. Products are used throughout the system
 * for order processing and inventory management.
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface ProductService {
    /**
     * Creates a new product in the catalog.
     * 
     * @param request the product creation request containing name, description, price, etc.
     * @return ProductResponse containing the created product with generated ID
     * @throws com.example.app.common.exception.BusinessException if product name already exists
     */
    ProductResponse createProduct(ProductRequest request);
    
    /**
     * Retrieves a product by its unique identifier.
     * 
     * @param id the product's UUID
     * @return ProductResponse containing product details
     * @throws com.example.app.common.exception.EntityNotFoundException if product is not found
     */
    ProductResponse getProductById(UUID id);
    
    /**
     * Searches for products by name or description with pagination.
     * 
     * <p>The search is case-insensitive and matches products whose name or description
     * contains the search term.
     * 
     * @param search the search term (null or empty returns all products)
     * @param page the zero-based page number
     * @param size the number of products per page
     * @return PagedResponse containing matching products and pagination metadata
     */
    PagedResponse<ProductResponse> searchProducts(String search, int page, int size);
}

