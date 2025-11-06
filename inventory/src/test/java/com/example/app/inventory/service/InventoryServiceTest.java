package com.example.app.inventory.service;

import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.inventory.domain.ReleaseRequest;
import com.example.app.inventory.domain.ReserveRequest;
import com.example.app.inventory.dto.InventoryResponse;
import com.example.app.inventory.entity.Inventory;
import com.example.app.inventory.exception.InsufficientStockException;
import com.example.app.inventory.mapper.InventoryMapper;
import com.example.app.inventory.repository.InventoryRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    private InventoryServiceImpl inventoryService;
    private MeterRegistry meterRegistry;

    private UUID productId;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        inventoryService = new InventoryServiceImpl(meterRegistry);
        inventoryService.setInventoryRepository(inventoryRepository);
        inventoryService.setInventoryMapper(inventoryMapper);
        
        productId = UUID.randomUUID();
        inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailableQty(100);
        inventory.setReservedQty(0);
    }

    @Test
    void testReserveInventory_Success() {
        ReserveRequest request = new ReserveRequest();
        request.setQuantity(10);

        when(inventoryRepository.findByProductIdForUpdate(productId)).thenReturn(inventory);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(createInventoryResponse());

        InventoryResponse response = inventoryService.reserveInventory(productId, request);

        assertNotNull(response);
        assertEquals(90, inventory.getAvailableQty());
        assertEquals(10, inventory.getReservedQty());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void testReserveInventory_InsufficientStock() {
        ReserveRequest request = new ReserveRequest();
        request.setQuantity(150);

        when(inventoryRepository.findByProductIdForUpdate(productId)).thenReturn(inventory);

        assertThrows(InsufficientStockException.class, 
            () -> inventoryService.reserveInventory(productId, request));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testReleaseInventory_Success() {
        inventory.setReservedQty(20);
        inventory.setAvailableQty(80);
        
        ReleaseRequest request = new ReleaseRequest();
        request.setQuantity(10);

        when(inventoryRepository.findByProductIdForUpdate(productId)).thenReturn(inventory);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(createInventoryResponse());

        InventoryResponse response = inventoryService.releaseInventory(productId, request);

        assertNotNull(response);
        assertEquals(90, inventory.getAvailableQty());
        assertEquals(10, inventory.getReservedQty());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void testGetInventory() {
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        when(inventoryMapper.toResponse(inventory)).thenReturn(createInventoryResponse());

        InventoryResponse response = inventoryService.getInventory(productId);

        assertNotNull(response);
    }

    @Test
    void testGetInventory_NotFound() {
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> inventoryService.getInventory(productId));
    }

    private InventoryResponse createInventoryResponse() {
        InventoryResponse response = new InventoryResponse();
        response.setProductId(productId.toString());
        response.setAvailableQty(100);
        response.setReservedQty(0);
        return response;
    }
}

