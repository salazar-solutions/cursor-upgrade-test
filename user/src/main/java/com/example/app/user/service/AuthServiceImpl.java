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
 * Implementation of authentication service.
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

