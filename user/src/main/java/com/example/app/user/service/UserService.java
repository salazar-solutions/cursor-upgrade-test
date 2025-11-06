package com.example.app.user.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.user.domain.UserRequest;
import com.example.app.user.dto.UserResponse;

import java.util.UUID;

/**
 * Service interface for user management operations.
 * 
 * <p>This service provides CRUD operations for user entities, including creation,
 * retrieval, update, and paginated listing. All operations are transactional
 * and include validation and business rule enforcement.
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface UserService {
    /**
     * Creates a new user with the provided information.
     * 
     * <p>The user is validated for uniqueness (username, email) and stored with
     * a hashed password. The returned response includes the generated user ID.
     * 
     * @param request the user creation request containing username, email, password, etc.
     * @return UserResponse containing the created user's information
     * @throws com.example.app.common.exception.BusinessException if username or email already exists
     * @throws javax.validation.ConstraintViolationException if validation fails
     */
    UserResponse createUser(UserRequest request);
    
    /**
     * Retrieves a user by their unique identifier.
     * 
     * @param id the user's UUID
     * @return UserResponse containing the user's information
     * @throws com.example.app.common.exception.EntityNotFoundException if user is not found
     */
    UserResponse getUserById(UUID id);
    
    /**
     * Updates an existing user's information.
     * 
     * <p>Only provided fields are updated. Username and email uniqueness is
     * validated if changed.
     * 
     * @param id the user's UUID
     * @param request the update request containing fields to modify
     * @return UserResponse containing the updated user's information
     * @throws com.example.app.common.exception.EntityNotFoundException if user is not found
     * @throws com.example.app.common.exception.BusinessException if updated username/email conflicts
     */
    UserResponse updateUser(UUID id, UserRequest request);
    
    /**
     * Retrieves a paginated list of all users.
     * 
     * @param page the zero-based page number
     * @param size the number of users per page
     * @return PagedResponse containing users and pagination metadata
     */
    PagedResponse<UserResponse> getAllUsers(int page, int size);
}

