package com.example.app.order.domain;

import com.example.app.order.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Request model for changing order status.
 */
public class OrderStatusChangeRequest {
    @NotNull(message = "Status is required")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}

