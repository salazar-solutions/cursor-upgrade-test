package com.example.app.order.dto;

import com.example.app.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for order information.
 */
public class OrderResponse {
    private String id;
    private String userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderLineResponse> orderLines;
    private Instant createdAt;
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderLineResponse> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLineResponse> orderLines) {
        this.orderLines = orderLines;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

