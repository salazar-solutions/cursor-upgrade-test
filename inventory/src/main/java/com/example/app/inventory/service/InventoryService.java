package com.example.app.inventory.service;

import com.example.app.inventory.domain.ReleaseRequest;
import com.example.app.inventory.domain.ReserveRequest;
import com.example.app.inventory.dto.InventoryResponse;

import java.util.UUID;

/**
 * Service interface for inventory management operations.
 * 
 * <p>This service manages product stock levels, including reservation and release
 * operations for order processing. Inventory reservations are used to hold stock
 * during order processing and can be released if orders are cancelled.
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface InventoryService {
    /**
     * Retrieves current inventory information for a product.
     * 
     * @param productId the product's UUID
     * @return InventoryResponse containing available stock, reserved stock, and total stock
     * @throws com.example.app.common.exception.EntityNotFoundException if product inventory not found
     */
    InventoryResponse getInventory(UUID productId);
    
    /**
     * Reserves inventory stock for a product (typically for order processing).
     * 
     * <p>This operation decreases available stock and increases reserved stock.
     * If insufficient stock is available, a BusinessException is thrown.
     * 
     * @param productId the product's UUID
     * @param request the reservation request containing quantity to reserve
     * @return InventoryResponse with updated stock levels
     * @throws com.example.app.common.exception.EntityNotFoundException if product inventory not found
     * @throws com.example.app.common.exception.BusinessException if insufficient stock available
     */
    InventoryResponse reserveInventory(UUID productId, ReserveRequest request);
    
    /**
     * Releases previously reserved inventory back to available stock.
     * 
     * <p>This operation increases available stock and decreases reserved stock.
     * Typically used when orders are cancelled or inventory reservations expire.
     * 
     * @param productId the product's UUID
     * @param request the release request containing quantity to release
     * @return InventoryResponse with updated stock levels
     * @throws com.example.app.common.exception.EntityNotFoundException if product inventory not found
     * @throws com.example.app.common.exception.BusinessException if attempting to release more than reserved
     */
    InventoryResponse releaseInventory(UUID productId, ReleaseRequest request);
}

