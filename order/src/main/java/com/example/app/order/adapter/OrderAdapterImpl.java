package com.example.app.order.adapter;

import com.example.app.billing.adapter.OrderAdapter;
import com.example.app.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementation of OrderAdapter for billing module.
 * This adapter allows the billing module to check order existence without creating
 * a circular dependency.
 */
@Component
public class OrderAdapterImpl implements OrderAdapter {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public boolean orderExists(UUID orderId) {
        if (orderId == null) {
            return false;
        }
        return orderRepository.existsById(orderId);
    }
}

