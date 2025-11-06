package com.example.app.inventory.controller;

import com.example.app.inventory.domain.ReleaseRequest;
import com.example.app.inventory.domain.ReserveRequest;
import com.example.app.inventory.dto.InventoryResponse;
import com.example.app.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {InventoryControllerTest.TestConfig.class})
class InventoryControllerTest {

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private InventoryController inventoryController;

    private UUID productId;
    private InventoryResponse inventoryResponse;
    private ReserveRequest reserveRequest;
    private ReleaseRequest releaseRequest;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        inventoryResponse = new InventoryResponse();
        inventoryResponse.setProductId(productId.toString());
        inventoryResponse.setAvailableQty(100);
        inventoryResponse.setReservedQty(0);

        reserveRequest = new ReserveRequest();
        reserveRequest.setQuantity(10);

        releaseRequest = new ReleaseRequest();
        releaseRequest.setQuantity(5);
    }

    @Test
    void testGetInventory_Success_ReturnsOk() {
        // Arrange
        when(inventoryService.getInventory(productId)).thenReturn(inventoryResponse);

        // Act
        ResponseEntity<InventoryResponse> response = inventoryController.getInventory(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId.toString(), response.getBody().getProductId());
        assertEquals(100, response.getBody().getAvailableQty());

        verify(inventoryService).getInventory(productId);
    }

    @Test
    void testReserveInventory_Success_ReturnsOk() {
        // Arrange
        InventoryResponse reservedResponse = new InventoryResponse();
        reservedResponse.setProductId(productId.toString());
        reservedResponse.setAvailableQty(90);
        reservedResponse.setReservedQty(10);

        when(inventoryService.reserveInventory(eq(productId), any(ReserveRequest.class)))
            .thenReturn(reservedResponse);

        // Act
        ResponseEntity<InventoryResponse> response = 
            inventoryController.reserveInventory(productId, reserveRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(90, response.getBody().getAvailableQty());
        assertEquals(10, response.getBody().getReservedQty());

        verify(inventoryService).reserveInventory(eq(productId), any(ReserveRequest.class));
    }

    @Test
    void testReleaseInventory_Success_ReturnsOk() {
        // Arrange
        InventoryResponse releasedResponse = new InventoryResponse();
        releasedResponse.setProductId(productId.toString());
        releasedResponse.setAvailableQty(95);
        releasedResponse.setReservedQty(5);

        when(inventoryService.releaseInventory(eq(productId), any(ReleaseRequest.class)))
            .thenReturn(releasedResponse);

        // Act
        ResponseEntity<InventoryResponse> response = 
            inventoryController.releaseInventory(productId, releaseRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(95, response.getBody().getAvailableQty());
        assertEquals(5, response.getBody().getReservedQty());

        verify(inventoryService).releaseInventory(eq(productId), any(ReleaseRequest.class));
    }

    @Configuration
    @Import(InventoryController.class)
    static class TestConfig {
        // Minimal configuration - only imports the controller under test
        // All dependencies are mocked via @MockBean
    }
}

