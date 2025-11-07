package com.example.app.product.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.common.exception.BusinessException;
import com.example.app.common.exception.DuplicateResourceException;
import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.product.domain.ProductRequest;
import com.example.app.product.dto.ProductResponse;
import com.example.app.product.entity.Product;
import com.example.app.product.mapper.ProductMapper;
import com.example.app.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of product service with validation and search functionality.
 * 
 * <p>This service handles product catalog operations with the following features:
 * <ul>
 *   <li>SKU uniqueness validation on product creation</li>
 *   <li>Case-insensitive product search by name or description</li>
 *   <li>Pagination support for product listings</li>
 * </ul>
 * 
 * <p><b>Search Behavior:</b> Product search matches against both name and description
 * fields using case-insensitive contains matching. Empty or null search terms return
 * all products.
 * 
 * @author Generated
 * @since 1.0.0
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private InventoryInitializationService inventoryInitializationService;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product savedProduct;
        
        // Check if product already exists
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU already exists");
        }

        // Create new product
        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailableQty(request.getAvailableQty());

        savedProduct = productRepository.save(product);
        
        // Automatically create inventory record for the product (in separate transaction)
        // This ensures the product is immediately available for ordering
        inventoryInitializationService.ensureInventoryExists(savedProduct.getId(), savedProduct.getAvailableQty());
        
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.searchProducts(search, pageable);
        
        return new PagedResponse<>(
            productPage.getContent().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList()),
            productPage.getNumber(),
            productPage.getSize(),
            productPage.getTotalElements(),
            productPage.getTotalPages()
        );
    }
}

