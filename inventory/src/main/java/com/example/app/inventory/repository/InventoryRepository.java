package com.example.app.inventory.repository;

import com.example.app.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.UUID;

/**
 * Repository for Inventory entity.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Inventory findByProductIdForUpdate(@Param("productId") UUID productId);
}

