package com.example.app.user.service;

import com.example.app.common.dto.PagedResponse;
import com.example.app.common.exception.BusinessException;
import com.example.app.common.exception.EntityNotFoundException;
import com.example.app.user.domain.UserRequest;
import com.example.app.user.dto.UserResponse;
import com.example.app.user.entity.Role;
import com.example.app.user.entity.User;
import com.example.app.user.mapper.UserMapper;
import com.example.app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of user service with validation and business logic.
 * 
 * <p>This service handles user CRUD operations with the following validations:
 * <ul>
 *   <li>Username uniqueness check on create and update</li>
 *   <li>Email uniqueness check on create and update</li>
 *   <li>Password hashing using BCrypt before storage</li>
 * </ul>
 * 
 * <p><b>Password Security:</b> Passwords are never stored in plain text.
 * They are hashed using BCrypt before persistence. Plain passwords are never
 * returned in responses.
 * 
 * <p><b>Default Role:</b> New users are assigned the USER role by default.
 * 
 * @author Generated
 * @since 1.0.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleStr : request.getRoles()) {
                try {
                    roles.add(Role.valueOf(roleStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new BusinessException("Invalid role: " + roleStr);
                }
            }
        } else {
            roles.add(Role.USER);
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID id, UserRequest request) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleStr : request.getRoles()) {
                try {
                    roles.add(Role.valueOf(roleStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new BusinessException("Invalid role: " + roleStr);
                }
            }
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        
        return new PagedResponse<>(
            userPage.getContent().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList()),
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements(),
            userPage.getTotalPages()
        );
    }
}

