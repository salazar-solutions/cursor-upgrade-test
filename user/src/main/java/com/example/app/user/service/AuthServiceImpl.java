package com.example.app.user.service;

import com.example.app.common.exception.BusinessException;
import com.example.app.common.security.JwtUtil;
import com.example.app.user.domain.AuthRequest;
import com.example.app.user.dto.AuthResponse;
import com.example.app.user.dto.UserResponse;
import com.example.app.user.entity.User;
import com.example.app.user.mapper.UserMapper;
import com.example.app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of authentication service with JWT token generation.
 * 
 * <p>This service authenticates users by validating username and password,
 * then generates a JWT token for use in subsequent API requests.
 * 
 * <p><b>Authentication Flow:</b>
 * <ol>
 *   <li>Lookup user by username</li>
 *   <li>Verify password using BCrypt password encoder</li>
 *   <li>Generate JWT token with user ID and username</li>
 *   <li>Return token and user information</li>
 * </ol>
 * 
 * <p><b>Security:</b> Invalid credentials result in a generic error message
 * ("Invalid username or password") to prevent username enumeration attacks.
 * 
 * @author Generated
 * @since 1.0.0
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setType("Bearer");
        response.setUser(userMapper.toResponse(user));
        
        return response;
    }
}

