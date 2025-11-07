package com.example.app.order.controller;

import com.example.app.common.dto.PagedResponse;
import com.example.app.order.domain.OrderRequest;
import com.example.app.order.domain.OrderStatusChangeRequest;
import com.example.app.order.dto.OrderResponse;
import com.example.app.order.entity.OrderStatus;
import com.example.app.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {OrderController.class})
class OrderControllerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private OrderController orderController;

    private UUID orderId;
    private UUID userId;
    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private OrderStatusChangeRequest statusChangeRequest;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();

        orderRequest = new OrderRequest();
        orderRequest.setUserId(userId);

        orderResponse = new OrderResponse();
        orderResponse.setId(orderId.toString());
        orderResponse.setUserId(userId.toString());
        orderResponse.setStatus(OrderStatus.PENDING);
        orderResponse.setTotalAmount(new BigDecimal("99.99"));

        statusChangeRequest = new OrderStatusChangeRequest();
        statusChangeRequest.setStatus(OrderStatus.CONFIRMED);
    }

    @Test
    void testCreateOrder_Success_ReturnsCreated() {
        // Arrange
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        // Act
        ResponseEntity<OrderResponse> response = orderController.createOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderId.toString(), response.getBody().getId());
        assertEquals(OrderStatus.PENDING, response.getBody().getStatus());

        verify(orderService).createOrder(any(OrderRequest.class));
    }

    @Test
    void testGetOrderById_Success_ReturnsOk() {
        // Arrange
        when(orderService.getOrderById(orderId)).thenReturn(orderResponse);

        // Act
        ResponseEntity<OrderResponse> response = orderController.getOrderById(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderId.toString(), response.getBody().getId());

        verify(orderService).getOrderById(orderId);
    }

    @Test
    void testGetOrders_WithoutFilters_ReturnsOk() {
        // Arrange
        PagedResponse<OrderResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.singletonList(orderResponse));
        pagedResponse.setTotalElements(1);
        pagedResponse.setTotalPages(1);

        when(orderService.getOrders(eq(null), eq(null), eq(0), eq(20))).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<OrderResponse>> response = 
            orderController.getOrders(null, null, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(orderService).getOrders(eq(null), eq(null), eq(0), eq(20));
    }

    @Test
    void testGetOrders_WithUserIdFilter_ReturnsOk() {
        // Arrange
        PagedResponse<OrderResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.singletonList(orderResponse));
        pagedResponse.setTotalElements(1);
        pagedResponse.setTotalPages(1);

        when(orderService.getOrders(eq(userId), eq(null), eq(0), eq(20))).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<OrderResponse>> response = 
            orderController.getOrders(userId, null, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(orderService).getOrders(eq(userId), eq(null), eq(0), eq(20));
    }

    @Test
    void testGetOrders_WithStatusFilter_ReturnsOk() {
        // Arrange
        PagedResponse<OrderResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.singletonList(orderResponse));
        pagedResponse.setTotalElements(1);
        pagedResponse.setTotalPages(1);

        when(orderService.getOrders(eq(null), eq(OrderStatus.PENDING), eq(0), eq(20)))
            .thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<OrderResponse>> response = 
            orderController.getOrders(null, OrderStatus.PENDING, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(orderService).getOrders(eq(null), eq(OrderStatus.PENDING), eq(0), eq(20));
    }

    @Test
    void testGetOrders_WithBothFilters_ReturnsOk() {
        // Arrange
        PagedResponse<OrderResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.singletonList(orderResponse));
        pagedResponse.setTotalElements(1);
        pagedResponse.setTotalPages(1);

        when(orderService.getOrders(eq(userId), eq(OrderStatus.PENDING), eq(0), eq(20)))
            .thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<OrderResponse>> response = 
            orderController.getOrders(userId, OrderStatus.PENDING, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(orderService).getOrders(eq(userId), eq(OrderStatus.PENDING), eq(0), eq(20));
    }

    @Test
    void testChangeOrderStatus_Success_ReturnsOk() {
        // Arrange
        OrderResponse updatedResponse = new OrderResponse();
        updatedResponse.setId(orderId.toString());
        updatedResponse.setStatus(OrderStatus.CONFIRMED);

        when(orderService.changeOrderStatus(eq(orderId), any(OrderStatusChangeRequest.class)))
            .thenReturn(updatedResponse);

        // Act
        ResponseEntity<OrderResponse> response = 
            orderController.changeOrderStatus(orderId, statusChangeRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderStatus.CONFIRMED, response.getBody().getStatus());

        verify(orderService).changeOrderStatus(eq(orderId), any(OrderStatusChangeRequest.class));
    }
}

