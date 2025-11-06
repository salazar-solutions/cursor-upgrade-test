package com.example.app.product.mapper;

import com.example.app.product.dto.ProductResponse;
import com.example.app.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for Product entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", expression = "java(product.getId().toString())")
    @Mapping(target = "createdAt", expression = "java(com.example.app.common.util.DateMapper.toInstant(product.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(com.example.app.common.util.DateMapper.toInstant(product.getUpdatedAt()))")
    ProductResponse toResponse(Product product);
}

