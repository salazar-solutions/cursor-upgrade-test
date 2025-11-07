package com.example.app.user.service;

import com.example.app.common.exception.BusinessException;
import com.example.app.common.exception.DuplicateResourceException;
import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.user.domain.UserRequest;
import com.example.app.user.entity.Role;
import com.example.app.user.entity.User;
import com.example.app.user.mapper.UserMapper;
import com.example.app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("encodedPassword");
        user.setRoles(new HashSet<>(Collections.singletonList(Role.USER)));
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(createUserResponse());

        com.example.app.user.dto.UserResponse response = userService.createUser(userRequest);

        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(createUserResponse());

        com.example.app.user.dto.UserResponse response = userService.getUserById(userId);

        assertNotNull(response);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void testUpdateUser_Success() {
        userRequest.setUsername("newusername");
        userRequest.setEmail("newemail@example.com");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(createUserResponse());

        com.example.app.user.dto.UserResponse response = userService.updateUser(userId, userRequest);

        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testGetAllUsers() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponse(any(User.class))).thenReturn(createUserResponse());

        com.example.app.common.dto.PagedResponse<com.example.app.user.dto.UserResponse> response = 
            userService.getAllUsers(0, 20);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    private com.example.app.user.dto.UserResponse createUserResponse() {
        com.example.app.user.dto.UserResponse response = new com.example.app.user.dto.UserResponse();
        response.setId(userId.toString());
        response.setUsername("testuser");
        response.setEmail("test@example.com");
        return response;
    }
}

