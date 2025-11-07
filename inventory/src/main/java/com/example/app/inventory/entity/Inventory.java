package com.example.app.inventory.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Inventory entity for tracking product stock.
 */
@Entity
@Table(name = "inventory", schema = "cursordb", indexes = {
    @Index(name = "idx_inventory_product_id", columnList = "product_id")
})
public class Inventory {
    @Id
    @Column(name = "product_id", columnDefinition = "UUID")
    private UUID productId;

    @Column(name = "reserved_qty", nullable = false)
    private Integer reservedQty = 0;

    @Column(name = "available_qty", nullable = false)
    private Integer availableQty = 0;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(Integer reservedQty) {
        this.reservedQty = reservedQty;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }
}

