package com.example.app.common.filter;

import com.example.app.common.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet filter that extracts and validates JWT tokens from the Authorization header
 * and sets up Spring Security authentication context.
 * 
 * <p>This filter processes requests with the "Bearer &lt;token&gt;" format in the
 * Authorization header. If a valid token is found, it creates an authentication
 * object and sets it in the SecurityContext, allowing downstream filters and
 * controllers to access the authenticated user.
 * 
 * <p><b>Behavior:</b>
 * <ul>
 *   <li>Extracts token from "Authorization: Bearer &lt;token&gt;" header</li>
 *   <li>Validates token signature and expiration</li>
 *   <li>Creates UsernamePasswordAuthenticationToken with username and ROLE_USER authority</li>
 *   <li>Sets authentication in SecurityContext for the current request</li>
 *   <li>Silently fails if token is missing or invalid (does not block request)</li>
 * </ul>
 * 
 * <p><b>Security Note:</b> This filter does not enforce authentication. It only sets
 * the authentication context if a valid token is present. Actual authorization
 * is handled by Spring Security configuration in {@link SecurityConfig}.
 * 
 * @author Generated
 * @since 1.0.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Processes the request to extract and validate JWT token.
     * 
     * <p>If a valid token is found in the Authorization header, sets up Spring Security
     * authentication context. The filter chain continues regardless of token presence
     * or validity.
     * 
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param chain the filter chain to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                try {
                    String username = jwtUtil.getUsernameFromToken(token);
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    logger.warn("JWT token validation failed", e);
                }
            }
        }

        chain.doFilter(request, response);
    }
}

