package com.example.app.product.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.common.exception.BusinessException;
import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.product.domain.ProductRequest;
import com.example.app.product.dto.ProductResponse;
import com.example.app.product.entity.Product;
import com.example.app.product.mapper.ProductMapper;
import com.example.app.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of product service.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new BusinessException("SKU already exists");
        }

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailableQty(request.getAvailableQty());

        Product savedProduct = productRepository.save(product);
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

