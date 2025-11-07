package com.example.app.admin.controller;

import com.example.app.common.dto.PagedResponse;
import com.example.app.user.dto.UserResponse;
import com.example.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for admin user operations.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "Admin Users", description = "User administration API")
public class AdminUserController {
    
    @Autowired
    private com.example.app.user.service.UserService userService;

    @GetMapping
    @Operation(summary = "List all users (admin)")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<PagedResponse<UserResponse>> listUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        PagedResponse<UserResponse> response = userService.getAllUsers(page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "Disable a user (admin)")
    @ApiResponse(responseCode = "200", description = "User disabled successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Map<String, String>> disableUser(@PathVariable("id") UUID id) {
        // In a full implementation, this would update a user's enabled/disabled status
        // For now, return a placeholder response
        Map<String, String> response = new HashMap<>();
        response.put("message", "User disable functionality to be implemented");
        response.put("userId", id.toString());
        return ResponseEntity.ok(response);
    }
}

