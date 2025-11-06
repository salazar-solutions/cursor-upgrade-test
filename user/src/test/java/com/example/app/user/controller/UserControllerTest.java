package com.example.app.user.controller;

import com.example.app.common.dto.PagedResponse;
import com.example.app.user.domain.AuthRequest;
import com.example.app.user.domain.UserRequest;
import com.example.app.user.dto.AuthResponse;
import com.example.app.user.dto.UserResponse;
import com.example.app.user.service.AuthService;
import com.example.app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {UserController.class})
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserController userController;

    private UserRequest userRequest;
    private UserResponse userResponse;
    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");

        userResponse = new UserResponse();
        userResponse.setId(userId.toString());
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        authResponse = new AuthResponse();
        authResponse.setToken("test-token");
        authResponse.setType("Bearer");
        authResponse.setUser(userResponse);
    }

    @Test
    void testCreateUser_Success_ReturnsCreated() {
        // Arrange
        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.createUser(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId.toString(), response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());

        verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    void testGetUserById_Success_ReturnsOk() {
        // Arrange
        when(userService.getUserById(userId)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId.toString(), response.getBody().getId());

        verify(userService).getUserById(userId);
    }

    @Test
    void testUpdateUser_Success_ReturnsOk() {
        // Arrange
        when(userService.updateUser(eq(userId), any(UserRequest.class))).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.updateUser(userId, userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId.toString(), response.getBody().getId());

        verify(userService).updateUser(eq(userId), any(UserRequest.class));
    }

    @Test
    void testGetAllUsers_Success_ReturnsOk() {
        // Arrange
        PagedResponse<UserResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.singletonList(userResponse));
        pagedResponse.setTotalElements(1);
        pagedResponse.setTotalPages(1);

        when(userService.getAllUsers(0, 20)).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<UserResponse>> response = userController.getAllUsers(0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(userService).getAllUsers(0, 20);
    }

    @Test
    void testGetAllUsers_WithCustomPagination_ReturnsOk() {
        // Arrange
        PagedResponse<UserResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(java.util.Collections.emptyList());
        pagedResponse.setTotalElements(0);
        pagedResponse.setTotalPages(0);

        when(userService.getAllUsers(1, 10)).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponse<UserResponse>> response = userController.getAllUsers(1, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userService).getAllUsers(1, 10);
    }

    @Test
    void testLogin_Success_ReturnsOk() {
        // Arrange
        when(authService.login(any(AuthRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = userController.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-token", response.getBody().getToken());
        assertEquals("Bearer", response.getBody().getType());
        assertNotNull(response.getBody().getUser());

        verify(authService).login(any(AuthRequest.class));
    }
}

