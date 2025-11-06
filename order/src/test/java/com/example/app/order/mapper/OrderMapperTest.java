package com.example.app.order.mapper;

import com.example.app.order.dto.OrderLineResponse;
import com.example.app.order.dto.OrderResponse;
import com.example.app.order.entity.Order;
import com.example.app.order.entity.OrderLine;
import com.example.app.order.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;

    private Order order;
    private OrderLine orderLine;
    private UUID orderId;
    private UUID userId;
    private UUID productId;
    private UUID orderLineId;

    @BeforeEach
    void setUp() {
        orderMapper = Mappers.getMapper(OrderMapper.class);
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        orderLineId = UUID.randomUUID();

        order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setStatus(OrderStatus.PENDING);
        Date createdAt = new Date();
        Date updatedAt = new Date();
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);

        orderLine = new OrderLine();
        orderLine.setId(orderLineId);
        orderLine.setOrderId(orderId);
        orderLine.setProductId(productId);
        orderLine.setQuantity(2);
        orderLine.setPrice(new BigDecimal("99.99"));
    }

    @Test
    void testToResponse_Success_MapsAllFields() {
        // Act
        OrderResponse response = orderMapper.toResponse(order);

        // Assert
        assertNotNull(response);
        assertEquals(orderId.toString(), response.getId());
        assertEquals(userId.toString(), response.getUserId());
        assertEquals(new BigDecimal("199.98"), response.getTotalAmount());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void testToResponse_DateConversion_ConvertsToInstant() {
        // Arrange
        Date testDate = new Date();
        order.setCreatedAt(testDate);
        order.setUpdatedAt(testDate);

        // Act
        OrderResponse response = orderMapper.toResponse(order);

        // Assert
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(testDate.toInstant(), response.getCreatedAt());
        assertEquals(testDate.toInstant(), response.getUpdatedAt());
    }

    @Test
    void testToOrderLineResponse_Success_MapsAllFields() {
        // Act
        OrderLineResponse response = orderMapper.toOrderLineResponse(orderLine);

        // Assert
        assertNotNull(response);
        assertEquals(orderLineId.toString(), response.getId());
        assertEquals(orderId.toString(), response.getOrderId());
        assertEquals(productId.toString(), response.getProductId());
        assertEquals(2, response.getQuantity());
        assertEquals(new BigDecimal("99.99"), response.getPrice());
    }

    @Test
    void testToOrderLineResponseList_Success_MapsList() {
        // Arrange
        List<OrderLine> orderLines = new ArrayList<>();
        orderLines.add(orderLine);

        OrderLine orderLine2 = new OrderLine();
        orderLine2.setId(UUID.randomUUID());
        orderLine2.setOrderId(orderId);
        orderLine2.setProductId(UUID.randomUUID());
        orderLine2.setQuantity(1);
        orderLine2.setPrice(new BigDecimal("50.00"));
        orderLines.add(orderLine2);

        // Act
        List<OrderLineResponse> responses = orderMapper.toOrderLineResponseList(orderLines);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(orderLineId.toString(), responses.get(0).getId());
    }

    @Test
    void testToOrderLineResponseList_EmptyList_ReturnsEmptyList() {
        // Arrange
        List<OrderLine> emptyList = new ArrayList<>();

        // Act
        List<OrderLineResponse> responses = orderMapper.toOrderLineResponseList(emptyList);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

}

