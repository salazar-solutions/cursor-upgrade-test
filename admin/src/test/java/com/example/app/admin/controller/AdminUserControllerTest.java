package com.example.app.admin.controller;

import com.example.app.common.dto.PagedResponse;
import com.example.app.user.dto.UserResponse;
import com.example.app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {AdminUserController.class})
class AdminUserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private AdminUserController adminUserController;

    private UUID userId;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userResponse = new UserResponse();
        userResponse.setId(userId.toString());
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
    }

    @Test
    void testListUsers_Success_ReturnsOk() {
        // Arrange
        PagedResponse<UserResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.singletonList(userResponse));
        pagedResponse.setTotalElements(1);
        pagedResponse.setTotalPages(1);

        when(userService.getAllUsers(0, 20)).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<UserResponse>> response = adminUserController.listUsers(0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(userService).getAllUsers(0, 20);
    }

    @Test
    void testListUsers_WithCustomPagination_ReturnsOk() {
        // Arrange
        PagedResponse<UserResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.emptyList());
        pagedResponse.setTotalElements(0);
        pagedResponse.setTotalPages(0);

        when(userService.getAllUsers(1, 10)).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<UserResponse>> response = adminUserController.listUsers(1, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userService).getAllUsers(1, 10);
    }

    @Test
    void testDisableUser_Success_ReturnsOk() {
        // Act
        ResponseEntity<Map<String, String>> response = adminUserController.disableUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertTrue(response.getBody().containsKey("userId"));
        assertEquals(userId.toString(), response.getBody().get("userId"));
        assertEquals("User disable functionality to be implemented", response.getBody().get("message"));
    }
}

