package com.example.app.user.mapper;

import com.example.app.common.util.DateMapper;
import com.example.app.user.dto.UserResponse;
import com.example.app.user.entity.Role;
import com.example.app.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for User entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", expression = "java(user.getId().toString())")
    @Mapping(target = "createdAt", expression = "java(com.example.app.common.util.DateMapper.toInstant(user.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(com.example.app.common.util.DateMapper.toInstant(user.getUpdatedAt()))")
    @Mapping(target = "roles", expression = "java(convertRoles(user.getRoles()))")
    UserResponse toResponse(User user);

    default Set<String> convertRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
            .map(Role::name)
            .collect(Collectors.toSet());
    }
}

