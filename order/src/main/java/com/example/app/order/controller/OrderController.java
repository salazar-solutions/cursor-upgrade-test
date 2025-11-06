package com.example.app.order.controller;

import com.example.app.common.dto.PagedResponse;
import com.example.app.order.domain.OrderRequest;
import com.example.app.order.domain.OrderStatusChangeRequest;
import com.example.app.order.dto.OrderResponse;
import com.example.app.order.entity.OrderStatus;
import com.example.app.order.service.OrderService;
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
 * REST controller for order operations.
 */
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management API")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "422", description = "Business error (e.g., insufficient stock)")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get orders with optional filters")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<PagedResponse<OrderResponse>> getOrders(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<OrderResponse> response = orderService.getOrders(userId, status, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "Change order status")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "422", description = "Invalid status transition")
    public ResponseEntity<OrderResponse> changeOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusChangeRequest request) {
        OrderResponse response = orderService.changeOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }
}

