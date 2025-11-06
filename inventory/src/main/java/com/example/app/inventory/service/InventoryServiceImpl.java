package com.example.app.inventory.service;

import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.inventory.domain.ReleaseRequest;
import com.example.app.inventory.domain.ReserveRequest;
import com.example.app.inventory.dto.InventoryResponse;
import com.example.app.inventory.entity.Inventory;
import com.example.app.inventory.exception.InsufficientStockException;
import com.example.app.inventory.mapper.InventoryMapper;
import com.example.app.inventory.repository.InventoryRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of inventory service with atomic operations.
 */
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    private final Counter reservationsFailedCounter;

    @Autowired
    public InventoryServiceImpl(MeterRegistry meterRegistry) {
        this.reservationsFailedCounter = Counter.builder("inventory.reservations.failed")
            .description("Number of failed inventory reservations")
            .register(meterRegistry);
    }
    
    // Setters for testing
    public void setInventoryRepository(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    public void setInventoryMapper(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(UUID productId) {
        Inventory inventory = inventoryRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product: " + productId));
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public InventoryResponse reserveInventory(UUID productId, ReserveRequest request) {
        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId);
        
        if (inventory == null) {
            throw new EntityNotFoundException("Inventory not found for product: " + productId);
        }

        int requestedQty = request.getQuantity();
        int availableQty = inventory.getAvailableQty();
        
        if (availableQty < requestedQty) {
            reservationsFailedCounter.increment();
            throw new InsufficientStockException(
                String.format("Insufficient stock. Available: %d, Requested: %d", availableQty, requestedQty)
            );
        }

        inventory.setAvailableQty(availableQty - requestedQty);
        inventory.setReservedQty(inventory.getReservedQty() + requestedQty);
        
        Inventory updated = inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(updated);
    }

    @Override
    public InventoryResponse releaseInventory(UUID productId, ReleaseRequest request) {
        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId);
        
        if (inventory == null) {
            throw new EntityNotFoundException("Inventory not found for product: " + productId);
        }

        int releaseQty = request.getQuantity();
        int reservedQty = inventory.getReservedQty();
        
        if (reservedQty < releaseQty) {
            throw new InsufficientStockException(
                String.format("Cannot release more than reserved. Reserved: %d, Requested: %d", reservedQty, releaseQty)
            );
        }

        inventory.setReservedQty(reservedQty - releaseQty);
        inventory.setAvailableQty(inventory.getAvailableQty() + releaseQty);
        
        Inventory updated = inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(updated);
    }
}
