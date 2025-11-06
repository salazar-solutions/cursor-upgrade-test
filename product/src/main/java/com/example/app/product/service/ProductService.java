package com.example.app.product.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.product.domain.ProductRequest;
import com.example.app.product.dto.ProductResponse;

import java.util.UUID;

/**
 * Service interface for product operations.
 */
public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(UUID id);
    PagedResponse<ProductResponse> searchProducts(String search, int page, int size);
}

