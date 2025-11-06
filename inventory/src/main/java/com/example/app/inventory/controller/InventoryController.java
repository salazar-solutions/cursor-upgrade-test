package com.example.app.inventory.controller;

import com.example.app.inventory.domain.ReleaseRequest;
import com.example.app.inventory.domain.ReserveRequest;
import com.example.app.inventory.dto.InventoryResponse;
import com.example.app.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * REST controller for inventory management operations.
 * 
 * <p>This controller provides endpoints for:
 * <ul>
 *   <li>Inventory stock level queries</li>
 *   <li>Inventory reservation (for order processing)</li>
 *   <li>Inventory release (for order cancellation)</li>
 * </ul>
 * 
 * <p><b>Base Path:</b> /api/v1/inventory
 * 
 * <p><b>Stock Management:</b>
 * <ul>
 *   <li>Reservation decreases available stock and increases reserved stock</li>
 *   <li>Release increases available stock and decreases reserved stock</li>
 *   <li>Total stock = available + reserved</li>
 * </ul>
 * 
 * <p><b>Business Rules:</b>
 * <ul>
 *   <li>Cannot reserve more than available stock</li>
 *   <li>Cannot release more than reserved stock</li>
 * </ul>
 * 
 * @author Generated
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Inventory management API")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory for a product")
    @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Inventory not found")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable UUID productId) {
        InventoryResponse response = inventoryService.getInventory(productId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/reserve")
    @Operation(summary = "Reserve inventory for a product")
    @ApiResponse(responseCode = "200", description = "Inventory reserved successfully")
    @ApiResponse(responseCode = "422", description = "Insufficient stock")
    public ResponseEntity<InventoryResponse> reserveInventory(
            @PathVariable UUID productId,
            @Valid @RequestBody ReserveRequest request) {
        InventoryResponse response = inventoryService.reserveInventory(productId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/release")
    @Operation(summary = "Release reserved inventory for a product")
    @ApiResponse(responseCode = "200", description = "Inventory released successfully")
    @ApiResponse(responseCode = "422", description = "Cannot release more than reserved")
    public ResponseEntity<InventoryResponse> releaseInventory(
            @PathVariable UUID productId,
            @Valid @RequestBody ReleaseRequest request) {
        InventoryResponse response = inventoryService.releaseInventory(productId, request);
        return ResponseEntity.ok(response);
    }
}

