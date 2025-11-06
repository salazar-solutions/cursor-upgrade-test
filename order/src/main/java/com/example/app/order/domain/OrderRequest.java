package com.example.app.order.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Request model for creating an order.
 */
public class OrderRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotEmpty(message = "Order lines are required")
    @Valid
    private List<OrderLineRequest> orderLines;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<OrderLineRequest> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLineRequest> orderLines) {
        this.orderLines = orderLines;
    }
}

