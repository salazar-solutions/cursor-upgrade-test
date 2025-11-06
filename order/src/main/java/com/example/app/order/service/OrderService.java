package com.example.app.order.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.order.domain.OrderRequest;
import com.example.app.order.domain.OrderStatusChangeRequest;
import com.example.app.order.dto.OrderResponse;
import com.example.app.order.entity.OrderStatus;

import java.util.UUID;

/**
 * Service interface for order operations.
 */
public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(UUID id);
    PagedResponse<OrderResponse> getOrders(UUID userId, OrderStatus status, int page, int size);
    OrderResponse changeOrderStatus(UUID orderId, OrderStatusChangeRequest request);
}

