package com.example.app.billing.adapter;

import java.util.UUID;

/**
 * Adapter interface for order validation operations.
 * This interface is implemented by the order module to provide order-related functionality
 * to the billing module, avoiding circular dependencies.
 */
public interface OrderAdapter {
    /**
     * Checks if an order exists with the given ID.
     * 
     * @param orderId the order ID to check
     * @return true if the order exists, false otherwise
     */
    boolean orderExists(UUID orderId);
}

