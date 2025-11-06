package com.example.app.inventory.mapper;

import com.example.app.inventory.dto.InventoryResponse;
import com.example.app.inventory.entity.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {InventoryMapperImpl.class})
class InventoryMapperTest {

    @Autowired
    private InventoryMapper inventoryMapper;

    private Inventory inventory;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailableQty(100);
        inventory.setReservedQty(20);
    }

    @Test
    void testToResponse_Success_MapsAllFields() {
        // Act
        InventoryResponse response = inventoryMapper.toResponse(inventory);

        // Assert
        assertNotNull(response);
        assertEquals(productId.toString(), response.getProductId());
        assertEquals(100, response.getAvailableQty());
        assertEquals(20, response.getReservedQty());
    }

    @Test
    void testToResponse_UUIDConversion_ConvertsToString() {
        // Act
        InventoryResponse response = inventoryMapper.toResponse(inventory);

        // Assert
        assertNotNull(response.getProductId());
        assertEquals(productId.toString(), response.getProductId());
        assertTrue(response.getProductId().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    void testToResponse_ZeroQuantities_MapsCorrectly() {
        // Arrange
        inventory.setAvailableQty(0);
        inventory.setReservedQty(0);

        // Act
        InventoryResponse response = inventoryMapper.toResponse(inventory);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getAvailableQty());
        assertEquals(0, response.getReservedQty());
    }

}

