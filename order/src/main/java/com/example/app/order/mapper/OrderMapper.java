package com.example.app.order.mapper;

import com.example.app.order.dto.OrderLineResponse;
import com.example.app.order.dto.OrderResponse;
import com.example.app.order.entity.Order;
import com.example.app.order.entity.OrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for Order entity and DTOs.
 * MapStruct mappers should only handle mapping logic, not repository access.
 * Repository access should be done in the service layer.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    @Mapping(target = "id", expression = "java(order.getId().toString())")
    @Mapping(target = "userId", expression = "java(order.getUserId().toString())")
    @Mapping(target = "paymentId", expression = "java(order.getPaymentId() != null ? order.getPaymentId().toString() : null)")
    @Mapping(target = "createdAt", expression = "java(com.example.app.common.util.DateMapper.toInstant(order.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(com.example.app.common.util.DateMapper.toInstant(order.getUpdatedAt()))")
    @Mapping(target = "orderLines", ignore = true)
    OrderResponse toResponse(Order order);

    @Mapping(target = "id", expression = "java(orderLine.getId().toString())")
    @Mapping(target = "orderId", expression = "java(orderLine.getOrderId().toString())")
    @Mapping(target = "productId", expression = "java(orderLine.getProductId().toString())")
    OrderLineResponse toOrderLineResponse(OrderLine orderLine);

    List<OrderLineResponse> toOrderLineResponseList(List<OrderLine> orderLines);
}

