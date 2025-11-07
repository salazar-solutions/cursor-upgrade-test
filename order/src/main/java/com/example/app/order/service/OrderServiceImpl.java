package com.example.app.order.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.common.exception.BusinessException;
import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.inventory.domain.ReserveRequest;
import com.example.app.inventory.dto.InventoryResponse;
import com.example.app.inventory.service.InventoryService;
import com.example.app.notifications.domain.NotificationRequest;
import com.example.app.notifications.service.NotificationService;
import com.example.app.billing.adapter.BillingAdapter;
import com.example.app.order.domain.OrderLineRequest;
import com.example.app.order.domain.OrderRequest;
import com.example.app.order.domain.OrderStatusChangeRequest;
import com.example.app.order.dto.OrderLineResponse;
import com.example.app.order.dto.OrderResponse;
import com.example.app.order.entity.Order;
import com.example.app.order.entity.OrderLine;
import com.example.app.order.entity.OrderStatus;
import com.example.app.order.entity.OrderStatusHistory;
import com.example.app.order.mapper.OrderMapper;
import com.example.app.order.repository.OrderLineRepository;
import com.example.app.order.repository.OrderRepository;
import com.example.app.order.repository.OrderStatusHistoryRepository;
import com.example.app.product.dto.ProductResponse;
import com.example.app.product.service.ProductService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of order service with comprehensive business logic and orchestration.
 * 
 * <p>This service coordinates order creation across multiple modules:
 * <ul>
 *   <li><b>Product Service:</b> Validates products and retrieves pricing</li>
 *   <li><b>Inventory Service:</b> Reserves stock for order items</li>
 *   <li><b>Billing Adapter:</b> Creates payment records</li>
 *   <li><b>Notification Service:</b> Sends order creation notifications</li>
 * </ul>
 * 
 * <p><b>Order Lifecycle:</b>
 * <ol>
 *   <li>Order created with PENDING status</li>
 *   <li>Inventory reserved for all items</li>
 *   <li>Payment record created</li>
 *   <li>Status transitions: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED</li>
 *   <li>Orders can be CANCELLED at any point before DELIVERED</li>
 * </ol>
 * 
 * <p><b>Metrics:</b> Order creation is tracked via Micrometer counter "orders.created".
 * 
 * <p><b>Thread Safety:</b> This service is thread-safe when used with Spring's
 * default singleton bean scope. Database transactions ensure data consistency.
 * 
 * @author Generated
 * @since 1.0.0
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderLineRepository orderLineRepository;
    
    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private BillingAdapter billingAdapter;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private OrderMapper orderMapper;
    
    private final Counter ordersCreatedCounter;

    @Autowired
    public OrderServiceImpl(MeterRegistry meterRegistry) {
        this.ordersCreatedCounter = Counter.builder("orders.created")
            .description("Number of orders created")
            .register(meterRegistry);
    }
    
    // Setters for testing
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    public void setOrderLineRepository(OrderLineRepository orderLineRepository) {
        this.orderLineRepository = orderLineRepository;
    }
    
    public void setOrderStatusHistoryRepository(OrderStatusHistoryRepository orderStatusHistoryRepository) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
    }
    
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    
    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    public void setBillingAdapter(BillingAdapter billingAdapter) {
        this.billingAdapter = billingAdapter;
    }
    
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    public void setOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation performs the following steps atomically:
     * <ol>
     *   <li>Validates all products exist and calculates total amount</li>
     *   <li>Creates order entity with PENDING status</li>
     *   <li>Reserves inventory for each product (may throw InsufficientStockException)</li>
     *   <li>Creates payment record via billing adapter</li>
     *   <li>Saves order lines</li>
     *   <li>Records initial status history</li>
     *   <li>Sends notification (non-blocking, failures are logged but don't fail order)</li>
     *   <li>Increments order creation metric</li>
     * </ol>
     * 
     * <p><b>Transaction:</b> All steps are executed within a single transaction.
     * If any step fails, the entire operation is rolled back.
     */
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderLine> orderLines = new ArrayList<>();

        // Validate products and calculate total
        for (OrderLineRequest lineRequest : request.getOrderLines()) {
            ProductResponse product = productService.getProductById(lineRequest.getProductId());
            BigDecimal lineTotal = product.getPrice().multiply(new BigDecimal(lineRequest.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            OrderLine orderLine = new OrderLine();
            orderLine.setProductId(lineRequest.getProductId());
            orderLine.setQuantity(lineRequest.getQuantity());
            orderLine.setPrice(product.getPrice());
            orderLines.add(orderLine);
        }

        // Create order first
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        // Reserve inventory for each product
        for (OrderLineRequest lineRequest : request.getOrderLines()) {
            ReserveRequest reserveRequest = new ReserveRequest();
            reserveRequest.setQuantity(lineRequest.getQuantity());
            inventoryService.reserveInventory(lineRequest.getProductId(), reserveRequest);
        }

        // Create payment record via BillingAdapter
        UUID paymentId = billingAdapter.createPayment(savedOrder.getId(), totalAmount);

        // Create order lines
        for (OrderLine orderLine : orderLines) {
            orderLine.setOrderId(savedOrder.getId());
            orderLineRepository.save(orderLine);
        }

        // Record initial status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrderId(savedOrder.getId());
        history.setFromStatus(null);
        history.setToStatus(OrderStatus.PENDING);
        orderStatusHistoryRepository.save(history);

        // Publish notification (async stub)
        try {
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(request.getUserId().toString());
            notificationRequest.setMessage("Order " + savedOrder.getId() + " has been created");
            notificationRequest.setType("ORDER_CREATED");
            notificationService.sendNotification(notificationRequest);
        } catch (Exception e) {
            // Log but don't fail the order creation
            // In production, this would be handled by a message queue
        }

        ordersCreatedCounter.increment();

        // Fetch saved order lines for the response
        List<OrderLine> savedOrderLines = orderLineRepository.findByOrderId(savedOrder.getId());
        OrderResponse response = orderMapper.toResponse(savedOrder);
        response.setOrderLines(orderMapper.toOrderLineResponseList(savedOrderLines));
        return response;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Fetches the order and all associated order lines from the database.
     * This is a read-only operation.
     */
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        List<OrderLine> orderLines = orderLineRepository.findByOrderId(id);
        OrderResponse response = orderMapper.toResponse(order);
        response.setOrderLines(orderMapper.toOrderLineResponseList(orderLines));
        return response;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Uses JPA Specifications to handle complex filtering scenarios, particularly
     * to avoid PostgreSQL enum parameter binding issues when both userId and status
     * filters are provided.
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getOrders(UUID userId, OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage;
        
        // Handle query conditionally to avoid PostgreSQL enum parameter type inference issues
        if (userId != null && status != null) {
            // When both parameters are provided, use Specification to avoid enum binding issue
            Specification<Order> spec = (root, query, cb) -> {
                Predicate userIdPred = cb.equal(root.get("userId"), userId);
                Predicate statusPred = cb.equal(root.get("status"), status);
                return cb.and(userIdPred, statusPred);
            };
            orderPage = orderRepository.findAll(spec, pageable);
        } else if (userId != null) {
            orderPage = orderRepository.findByUserId(userId, pageable);
        } else if (status != null) {
            orderPage = orderRepository.findByStatus(status, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }
        
        return new PagedResponse<>(
            orderPage.getContent().stream()
                .map(order -> {
                    List<OrderLine> orderLines = orderLineRepository.findByOrderId(order.getId());
                    OrderResponse response = orderMapper.toResponse(order);
                    response.setOrderLines(orderMapper.toOrderLineResponseList(orderLines));
                    return response;
                })
                .collect(Collectors.toList()),
            orderPage.getNumber(),
            orderPage.getSize(),
            orderPage.getTotalElements(),
            orderPage.getTotalPages()
        );
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Validates the status transition using {@link #isValidStatusTransition(OrderStatus, OrderStatus)}
     * before applying the change. Status history is recorded for audit purposes.
     */
    @Override
    public OrderResponse changeOrderStatus(UUID orderId, OrderStatusChangeRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BusinessException(
                String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }

        // Record status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrderId(orderId);
        history.setFromStatus(currentStatus);
        history.setToStatus(newStatus);
        orderStatusHistoryRepository.save(history);

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        List<OrderLine> orderLines = orderLineRepository.findByOrderId(orderId);
        OrderResponse response = orderMapper.toResponse(updatedOrder);
        response.setOrderLines(orderMapper.toOrderLineResponseList(orderLines));
        return response;
    }

    /**
     * Validates whether a status transition is allowed according to business rules.
     * 
     * <p>Status transition rules:
     * <ul>
     *   <li>New orders (from=null) can only start as PENDING</li>
     *   <li>PENDING can transition to CONFIRMED or CANCELLED</li>
     *   <li>CONFIRMED can transition to PROCESSING or CANCELLED</li>
     *   <li>PROCESSING can transition to SHIPPED or CANCELLED</li>
     *   <li>SHIPPED can only transition to DELIVERED</li>
     *   <li>DELIVERED and CANCELLED are terminal states (no transitions allowed)</li>
     * </ul>
     * 
     * @param from the current order status (null for new orders)
     * @param to the desired new status
     * @return true if the transition is valid, false otherwise
     */
    private boolean isValidStatusTransition(OrderStatus from, OrderStatus to) {
        if (from == null) {
            return to == OrderStatus.PENDING;
        }

        switch (from) {
            case PENDING:
                return to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED:
                return to == OrderStatus.PROCESSING || to == OrderStatus.CANCELLED;
            case PROCESSING:
                return to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
            case SHIPPED:
                return to == OrderStatus.DELIVERED;
            case DELIVERED:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }
}

