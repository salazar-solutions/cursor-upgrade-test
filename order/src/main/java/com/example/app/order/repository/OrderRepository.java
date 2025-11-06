package com.example.app.order.repository;

import com.example.app.order.entity.Order;
import com.example.app.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUserId(UUID userId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE " +
           "(:userId IS NULL OR o.userId = :userId) AND " +
           "(:status IS NULL OR o.status = :status)")
    Page<Order> findByUserIdAndStatus(@Param("userId") UUID userId, 
                                      @Param("status") OrderStatus status, 
                                      Pageable pageable);
}

