package com.example.app.order.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.order.domain.OrderRequest;
import com.example.app.order.domain.OrderStatusChangeRequest;
import com.example.app.order.dto.OrderResponse;
import com.example.app.order.entity.OrderStatus;

import java.util.UUID;

/**
 * Service interface for order management operations.
 * 
 * <p>This service orchestrates the complete order lifecycle, including creation,
 * status management, and inventory coordination. Orders integrate with multiple
 * modules: inventory (stock reservation), billing (payment processing), and
 * notifications (order updates).
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface OrderService {
    /**
     * Creates a new order with the specified products and quantities.
     * 
     * <p>This operation:
     * <ul>
     *   <li>Validates product existence and retrieves prices</li>
     *   <li>Calculates total order amount</li>
     *   <li>Reserves inventory for each product</li>
     *   <li>Creates payment record via billing adapter</li>
     *   <li>Sends order creation notification</li>
     * </ul>
     * 
     * <p>The order is created with PENDING status and can be transitioned to
     * CONFIRMED, PROCESSING, SHIPPED, or CANCELLED.
     * 
     * @param request the order creation request containing user ID and order lines
     * @return OrderResponse containing the created order with all details
     * @throws com.example.app.common.exception.EntityNotFoundException if product not found
     * @throws com.example.app.common.exception.BusinessException if insufficient stock
     */
    OrderResponse createOrder(OrderRequest request);
    
    /**
     * Retrieves an order by its unique identifier.
     * 
     * @param id the order's UUID
     * @return OrderResponse containing order details and order lines
     * @throws com.example.app.common.exception.EntityNotFoundException if order is not found
     */
    OrderResponse getOrderById(UUID id);
    
    /**
     * Retrieves a paginated list of orders with optional filtering.
     * 
     * <p>Filters can be applied by user ID, order status, or both. If no filters
     * are provided, all orders are returned.
     * 
     * @param userId optional user ID filter (null to include all users)
     * @param status optional order status filter (null to include all statuses)
     * @param page the zero-based page number
     * @param size the number of orders per page
     * @return PagedResponse containing orders and pagination metadata
     */
    PagedResponse<OrderResponse> getOrders(UUID userId, OrderStatus status, int page, int size);
    
    /**
     * Changes an order's status, enforcing valid state transitions.
     * 
     * <p>Valid transitions:
     * <ul>
     *   <li>PENDING → CONFIRMED, CANCELLED</li>
     *   <li>CONFIRMED → PROCESSING, CANCELLED</li>
     *   <li>PROCESSING → SHIPPED, CANCELLED</li>
     *   <li>SHIPPED → DELIVERED</li>
     *   <li>DELIVERED, CANCELLED → (no transitions allowed)</li>
     * </ul>
     * 
     * <p>Status changes are recorded in order status history for audit purposes.
     * 
     * @param orderId the order's UUID
     * @param request the status change request containing the new status
     * @return OrderResponse containing the updated order
     * @throws com.example.app.common.exception.EntityNotFoundException if order is not found
     * @throws com.example.app.common.exception.BusinessException if status transition is invalid
     */
    OrderResponse changeOrderStatus(UUID orderId, OrderStatusChangeRequest request);
}

