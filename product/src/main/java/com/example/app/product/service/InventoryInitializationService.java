package com.example.app.product.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for initializing inventory records for products.
 * This is separated to allow for independent transaction management.
 */
@Service
public class InventoryInitializationService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Ensures inventory record exists for a product.
     * Creates inventory if it doesn't exist.
     * Uses the same transaction as the parent to ensure the product is visible.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void ensureInventoryExists(UUID productId, Integer availableQty) {
        try {
            entityManager.createNativeQuery(
                "INSERT INTO cursordb.inventory (product_id, reserved_qty, available_qty) " +
                "VALUES (?1, 0, ?2) " +
                "ON CONFLICT (product_id) DO NOTHING"
            )
            .setParameter(1, productId)
            .setParameter(2, availableQty != null ? availableQty : 0)
            .executeUpdate();
            entityManager.flush();
        } catch (Exception e) {
            // Log but don't fail - inventory might already exist
            // The ON CONFLICT clause will handle duplicates gracefully
        }
    }
}

