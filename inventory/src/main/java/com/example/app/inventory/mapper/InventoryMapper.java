package com.example.app.inventory.mapper;

import com.example.app.inventory.dto.InventoryResponse;
import com.example.app.inventory.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for Inventory entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper {
    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    @Mapping(target = "productId", expression = "java(inventory.getProductId().toString())")
    InventoryResponse toResponse(Inventory inventory);
}

