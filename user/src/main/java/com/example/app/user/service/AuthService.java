package com.example.app.user.service;

import com.example.app.user.domain.AuthRequest;
import com.example.app.user.dto.AuthResponse;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {
    AuthResponse login(AuthRequest request);
}

