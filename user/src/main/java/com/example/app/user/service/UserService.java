package com.example.app.user.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.user.domain.UserRequest;
import com.example.app.user.dto.UserResponse;

import java.util.UUID;

/**
 * Service interface for user operations.
 */
public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(UUID id);
    UserResponse updateUser(UUID id, UserRequest request);
    PagedResponse<UserResponse> getAllUsers(int page, int size);
}

