package com.example.app.product.controller;

import com.example.app.common.dto.PagedResponse;
import com.example.app.product.domain.ProductRequest;
import com.example.app.product.dto.ProductResponse;
import com.example.app.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * REST controller for product operations.
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management API")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @PostMapping
    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Search products with pagination")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<ProductResponse> response = productService.searchProducts(search, page, size);
        return ResponseEntity.ok(response);
    }
}

