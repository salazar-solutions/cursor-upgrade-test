package com.example.app.user.controller;

import com.example.app.common.dto.PagedResponse;
import com.example.app.user.domain.AuthRequest;
import com.example.app.user.domain.UserRequest;
import com.example.app.user.dto.AuthResponse;
import com.example.app.user.dto.UserResponse;
import com.example.app.user.service.AuthService;
import com.example.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * REST controller for user management and authentication operations.
 * 
 * <p>This controller provides endpoints for:
 * <ul>
 *   <li>User CRUD operations (create, read, update, list)</li>
 *   <li>User authentication and JWT token generation</li>
 * </ul>
 * 
 * <p><b>Base Path:</b> /api/v1/users
 * 
 * <p><b>Authentication:</b> Most endpoints require JWT authentication in production.
 * The /auth/login endpoint is publicly accessible.
 * 
 * @author Generated
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management API")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<UserResponse> response = userService.getAllUsers(page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Authenticate user and get JWT token")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "422", description = "Invalid credentials")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

