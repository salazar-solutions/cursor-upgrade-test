package com.example.app.user.service;

import com.example.app.user.domain.AuthRequest;
import com.example.app.user.dto.AuthResponse;

/**
 * Service interface for user authentication operations.
 * 
 * <p>This service handles user login and JWT token generation. Authentication
 * is performed by validating credentials against stored user data.
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface AuthService {
    /**
     * Authenticates a user and generates a JWT token.
     * 
     * <p>The method validates the provided username and password. If valid,
     * a JWT token is generated and returned for use in subsequent API requests.
     * 
     * @param request the authentication request containing username and password
     * @return AuthResponse containing the JWT token and user information
     * @throws com.example.app.common.exception.BusinessException if credentials are invalid
     */
    AuthResponse login(AuthRequest request);
}

