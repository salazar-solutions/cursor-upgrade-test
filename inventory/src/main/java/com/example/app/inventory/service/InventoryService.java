package com.example.app.inventory.service;

import com.example.app.inventory.domain.ReleaseRequest;
import com.example.app.inventory.domain.ReserveRequest;
import com.example.app.inventory.dto.InventoryResponse;

import java.util.UUID;

/**
 * Service interface for inventory operations.
 */
public interface InventoryService {
    InventoryResponse getInventory(UUID productId);
    InventoryResponse reserveInventory(UUID productId, ReserveRequest request);
    InventoryResponse releaseInventory(UUID productId, ReleaseRequest request);
}

