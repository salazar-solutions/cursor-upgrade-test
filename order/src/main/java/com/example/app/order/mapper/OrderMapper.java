package com.example.app.order.mapper;

import com.example.app.order.dto.OrderLineResponse;
import com.example.app.order.dto.OrderResponse;
import com.example.app.order.entity.Order;
import com.example.app.order.entity.OrderLine;
import com.example.app.order.repository.OrderLineRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for Order entity and DTOs.
 */
@Mapper(componentModel = "spring")
public abstract class OrderMapper {
    
    @org.springframework.beans.factory.annotation.Autowired
    protected OrderLineRepository orderLineRepository;

    @Mapping(target = "id", expression = "java(order.getId().toString())")
    @Mapping(target = "userId", expression = "java(order.getUserId().toString())")
    @Mapping(target = "createdAt", expression = "java(com.example.app.common.util.DateMapper.toInstant(order.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(com.example.app.common.util.DateMapper.toInstant(order.getUpdatedAt()))")
    @Mapping(target = "orderLines", expression = "java(mapOrderLines(order.getId()))")
    public abstract OrderResponse toResponse(Order order);

    protected List<OrderLineResponse> mapOrderLines(UUID orderId) {
        List<OrderLine> orderLines = orderLineRepository.findByOrderId(orderId);
        return orderLines.stream()
            .map(this::toOrderLineResponse)
            .collect(Collectors.toList());
    }

    private OrderLineResponse toOrderLineResponse(OrderLine orderLine) {
        OrderLineResponse response = new OrderLineResponse();
        response.setId(orderLine.getId().toString());
        response.setOrderId(orderLine.getOrderId().toString());
        response.setProductId(orderLine.getProductId().toString());
        response.setQuantity(orderLine.getQuantity());
        response.setPrice(orderLine.getPrice());
        return response;
    }
}

