package com.example.app.order.service;

import com.example.app.billing.adapter.BillingAdapter;
import com.example.app.order.domain.OrderLineRequest;
import com.example.app.order.domain.OrderRequest;
import com.example.app.order.domain.OrderStatusChangeRequest;
import com.example.app.order.entity.Order;
import com.example.app.order.entity.OrderStatus;
import com.example.app.order.mapper.OrderMapper;
import com.example.app.order.repository.OrderLineRepository;
import com.example.app.order.repository.OrderRepository;
import com.example.app.order.repository.OrderStatusHistoryRepository;
import com.example.app.inventory.domain.ReserveRequest;
import com.example.app.inventory.service.InventoryService;
import com.example.app.notifications.service.NotificationService;
import com.example.app.product.dto.ProductResponse;
import com.example.app.product.service.ProductService;
import com.example.app.user.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLineRepository orderLineRepository;

    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Mock
    private ProductService productService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private BillingAdapter billingAdapter;

    @Mock
    private NotificationService notificationService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserRepository userRepository;

    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private Order order;
    private UUID orderId;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        orderService = new OrderServiceImpl(meterRegistry);
        orderService.setOrderRepository(orderRepository);
        orderService.setOrderLineRepository(orderLineRepository);
        orderService.setOrderStatusHistoryRepository(orderStatusHistoryRepository);
        orderService.setProductService(productService);
        orderService.setInventoryService(inventoryService);
        orderService.setBillingAdapter(billingAdapter);
        orderService.setNotificationService(notificationService);
        orderService.setOrderMapper(orderMapper);
        ReflectionTestUtils.setField(orderService, "userRepository", userRepository);

        orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        orderRequest = new OrderRequest();
        orderRequest.setUserId(userId);
        OrderLineRequest lineRequest = new OrderLineRequest();
        lineRequest.setProductId(productId);
        lineRequest.setQuantity(2);
        orderRequest.setOrderLines(Collections.singletonList(lineRequest));

        order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setStatus(OrderStatus.PENDING);
    }

    @Test
    void testCreateOrder_Success() {
        UUID productId = orderRequest.getOrderLines().get(0).getProductId();
        ProductResponse product = new ProductResponse();
        product.setId(productId.toString());
        product.setPrice(new BigDecimal("99.99"));

        when(userRepository.existsById(any(UUID.class))).thenReturn(true);
        when(productService.getProductById(productId)).thenReturn(product);
        when(inventoryService.reserveInventory(any(UUID.class), any(ReserveRequest.class)))
            .thenReturn(null);
        when(billingAdapter.createPayment(any(UUID.class), any(BigDecimal.class)))
            .thenReturn(UUID.randomUUID());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderLineRepository.save(any(com.example.app.order.entity.OrderLine.class)))
            .thenReturn(new com.example.app.order.entity.OrderLine());
        when(orderStatusHistoryRepository.save(any(com.example.app.order.entity.OrderStatusHistory.class)))
            .thenReturn(new com.example.app.order.entity.OrderStatusHistory());
        when(orderMapper.toResponse(any(Order.class))).thenReturn(createOrderResponse());

        com.example.app.order.dto.OrderResponse response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(inventoryService).reserveInventory(any(UUID.class), any(ReserveRequest.class));
    }

    @Test
    void testChangeOrderStatus_Success() {
        OrderStatusChangeRequest request = new OrderStatusChangeRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderStatusHistoryRepository.save(any(com.example.app.order.entity.OrderStatusHistory.class)))
            .thenReturn(new com.example.app.order.entity.OrderStatusHistory());
        when(orderMapper.toResponse(any(Order.class))).thenReturn(createOrderResponse());

        com.example.app.order.dto.OrderResponse response = 
            orderService.changeOrderStatus(orderId, request);

        assertNotNull(response);
        verify(orderStatusHistoryRepository).save(any());
    }

    @Test
    void testChangeOrderStatus_InvalidTransition() {
        order.setStatus(OrderStatus.DELIVERED);
        OrderStatusChangeRequest request = new OrderStatusChangeRequest();
        request.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(com.example.app.common.exception.BusinessException.class,
            () -> orderService.changeOrderStatus(orderId, request));
    }

    private com.example.app.order.dto.OrderResponse createOrderResponse() {
        com.example.app.order.dto.OrderResponse response = new com.example.app.order.dto.OrderResponse();
        response.setId(orderId.toString());
        response.setStatus(OrderStatus.PENDING);
        return response;
    }
}
