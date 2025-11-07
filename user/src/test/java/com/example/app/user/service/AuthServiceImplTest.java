package com.example.app.user.service;

import com.example.app.common.exception.BusinessException;
import com.example.app.common.security.JwtUtil;
import com.example.app.user.domain.AuthRequest;
import com.example.app.user.dto.AuthResponse;
import com.example.app.user.dto.UserResponse;
import com.example.app.user.entity.User;
import com.example.app.user.mapper.UserMapper;
import com.example.app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {AuthServiceImpl.class})
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key-for-unit-testing-only",
    "jwt.expiration=86400000"
})
class AuthServiceImplTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private AuthServiceImpl authService;

    private User user;
    private AuthRequest authRequest;
    private UUID userId;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        jwtToken = "test-jwt-token";

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setPasswordHash("encodedPassword");
        user.setEmail("test@example.com");

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");
    }

    @Test
    void testLogin_Success_ReturnsAuthResponse() {
        // Arrange
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId.toString());
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(userId, "testuser")).thenReturn(jwtToken);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        AuthResponse response = authService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertEquals("Bearer", response.getType());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtil).generateToken(userId, "testuser");
        verify(userMapper).toResponse(user);
    }

    @Test
    void testLogin_UserNotFound_ThrowsBusinessException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(authRequest));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any(), any());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void testLogin_InvalidPassword_ThrowsBusinessException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(authRequest));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtil, never()).generateToken(any(), any());
        verify(userMapper, never()).toResponse(any());
    }
}

