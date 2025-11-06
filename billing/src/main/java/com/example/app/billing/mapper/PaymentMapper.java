package com.example.app.billing.mapper;

import com.example.app.billing.dto.PaymentResponse;
import com.example.app.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for Payment entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "id", expression = "java(payment.getId().toString())")
    @Mapping(target = "orderId", expression = "java(payment.getOrderId().toString())")
    @Mapping(target = "createdAt", expression = "java(com.example.app.common.util.DateMapper.toInstant(payment.getCreatedAt()))")
    PaymentResponse toResponse(Payment payment);
}

