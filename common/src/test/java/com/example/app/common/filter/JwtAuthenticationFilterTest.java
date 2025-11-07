package com.example.app.common.filter;

import com.example.app.common.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {JwtAuthenticationFilter.class})
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key-for-unit-testing-jwt-filter-must-be-at-least-512-bits-long-for-hs512-algorithm",
    "jwt.expiration=86400000"
})
class JwtAuthenticationFilterTest {

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private String validToken;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        validToken = "valid.jwt.token";
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidTokenInHeader_SetsAuthentication() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("testuser");

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("testuser", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).getUsernameFromToken(validToken);
        // MockFilterChain is not a mock, so we just verify it was called by checking no exception
    }

    @Test
    void testDoFilterInternal_InvalidToken_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String invalidToken = "invalid.token";
        request.addHeader("Authorization", "Bearer " + invalidToken);
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtUtil).validateToken(invalidToken);
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
    }

    @Test
    void testDoFilterInternal_MissingHeader_DoesNotSetAuthentication() throws ServletException, IOException {
        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtUtil, never()).validateToken(anyString());
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
    }

    @Test
    void testDoFilterInternal_HeaderWithoutBearer_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "InvalidFormat " + validToken);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    void testDoFilterInternal_ValidTokenButExceptionDuringExtraction_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(validToken)).thenThrow(new RuntimeException("Token extraction failed"));

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).getUsernameFromToken(validToken);
    }

    @Test
    void testDoFilterInternal_EmptyBearerToken_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer ");
        // Empty string after "Bearer " will be passed to validateToken
        when(jwtUtil.validateToken("")).thenReturn(false);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        // The filter will still call validateToken with empty string
        verify(jwtUtil).validateToken("");
    }
}

