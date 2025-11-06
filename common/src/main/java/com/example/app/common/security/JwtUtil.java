package com.example.app.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for JWT (JSON Web Token) generation, validation, and parsing.
 * 
 * <p>This component handles all JWT operations using HMAC-SHA512 algorithm.
 * Tokens include user ID (as subject) and username (as claim) and are signed
 * with a configurable secret key.
 * 
 * <p><b>Configuration:</b>
 * <ul>
 *   <li>{@code jwt.secret}: Secret key for signing tokens (required in production)</li>
 *   <li>{@code jwt.expiration}: Token expiration time in milliseconds (default: 86400000 = 24 hours)</li>
 * </ul>
 * 
 * <p><b>Security Note:</b> The default secret is for testing only. Always override
 * {@code jwt.secret} in production with a strong, randomly generated key.
 * 
 * <p><b>Example usage:</b>
 * <pre>{@code
 * // Generate token
 * String token = jwtUtil.generateToken(userId, "john.doe");
 * 
 * // Validate token
 * if (jwtUtil.validateToken(token)) {
 *     UUID userId = jwtUtil.getUserIdFromToken(token);
 *     String username = jwtUtil.getUsernameFromToken(token);
 * }
 * }</pre>
 * 
 * @author Generated
 * @since 1.0.0
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:test-secret-key-for-jwt-token-generation-in-test-profile-only-change-in-production}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours default
    private Long expiration;

    /**
     * Creates a signing key from the configured secret.
     * 
     * @return SecretKey for HMAC-SHA512 signing
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for the given user.
     * 
     * <p>The token includes:
     * <ul>
     *   <li>Subject: user ID (UUID as string)</li>
     *   <li>Claim: username</li>
     *   <li>Issued at: current timestamp</li>
     *   <li>Expiration: current time + configured expiration period</li>
     * </ul>
     * 
     * @param userId the user's unique identifier
     * @param username the user's username
     * @return signed JWT token string
     */
    public String generateToken(UUID userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("username", username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Extracts the user ID from a JWT token.
     * 
     * <p>The user ID is stored as the token's subject claim.
     * 
     * @param token the JWT token string
     * @return the user ID parsed from the token subject
     * @throws io.jsonwebtoken.JwtException if the token is invalid, expired, or malformed
     * @throws IllegalArgumentException if the subject cannot be parsed as a UUID
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Extracts the username from a JWT token.
     * 
     * <p>The username is stored as a custom claim in the token.
     * 
     * @param token the JWT token string
     * @return the username claim value, or null if not present
     * @throws io.jsonwebtoken.JwtException if the token is invalid, expired, or malformed
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        return claims.get("username", String.class);
    }

    /**
     * Validates a JWT token's signature and expiration.
     * 
     * <p>This method verifies:
     * <ul>
     *   <li>Token signature is valid (not tampered with)</li>
     *   <li>Token has not expired</li>
     *   <li>Token format is correct</li>
     * </ul>
     * 
     * <p>Note: This method does not check token revocation or blacklisting.
     * For production systems, consider implementing token revocation mechanisms.
     * 
     * @param token the JWT token string to validate
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

