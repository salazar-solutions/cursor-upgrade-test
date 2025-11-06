package com.example.app.common.config;

import com.example.app.common.filter.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Spring Security configuration that provides environment-aware security settings.
 * 
 * <p>This configuration adapts security behavior based on active Spring profiles:
 * <ul>
 *   <li><b>Development profiles</b> (local, test, integration): Security disabled, all requests permitted</li>
 *   <li><b>Production profiles</b>: JWT authentication enabled, protected endpoints require valid token</li>
 * </ul>
 * 
 * <p><b>Security Features:</b>
 * <ul>
 *   <li>Stateless session management (no server-side sessions)</li>
 *   <li>CSRF protection disabled (stateless API)</li>
 *   <li>JWT authentication filter integration (production only)</li>
 *   <li>Public endpoints: /api/auth/**, /api/public/**</li>
 * </ul>
 * 
 * <p><b>Configuration:</b>
 * <p>The security filter chain is configured based on active profiles. In development,
 * security is completely disabled for easier testing. In production, JWT authentication
 * is enforced via {@link JwtAuthenticationFilter}.
 * 
 * <p><b>Warning:</b> Security is automatically disabled in development environments.
 * Never deploy with local/test/integration profiles active in production.
 * 
 * @author Generated
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Logs security configuration initialization details.
     */
    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("AdminSecurityConfiguration initialized");
        log.info("Active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        log.info("Default profiles: {}", Arrays.toString(environment.getDefaultProfiles()));
        log.info("========================================");
    }

    /**
     * Provides BCrypt password encoder for hashing user passwords.
     * 
     * <p>BCrypt is a strong, adaptive hashing algorithm suitable for password storage.
     * The encoder uses a strength of 10 (default), which provides good security
     * with acceptable performance.
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the Spring Security filter chain based on active profiles.
     * 
     * <p>In development (local/test/integration profiles), security is disabled.
     * In production, JWT authentication is enforced with the following rules:
     * <ul>
     *   <li>Public endpoints: /api/auth/**, /api/public/**</li>
     *   <li>All other endpoints require authentication</li>
     *   <li>JWT filter processes Authorization headers</li>
     * </ul>
     * 
     * @param http HttpSecurity builder for configuring security
     * @return configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    @org.springframework.context.annotation.Primary
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info(">>>>> CREATING SecurityFilterChain BEAN <<<<<");
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDevEnvironment = Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equals("local") ||
                        profile.equals("test") ||
                        profile.equals("integration") ||
                        profile.equals("regression"));

        log.info("Active profiles: {}", Arrays.toString(activeProfiles));
        log.info("Is dev environment: {}", isDevEnvironment);

        if (isDevEnvironment) {
            // Development mode - no security
            log.warn("⚠️ SECURITY DISABLED - Development mode active");
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        } else {
            // Production mode - JWT security
            log.info("🔒 SECURITY ENABLED - JWT authentication active");
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/api/auth/**", "/api/public/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            if (jwtAuthFilter != null) {
                log.info("JWT Authentication Filter added to security chain");
                http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            } else {
                log.error("❌ WARNING: JwtAuthenticationFilter not found!");
            }
        }

        log.info(">>>>> SecurityFilterChain BEAN CREATED SUCCESSFULLY <<<<<");
        return http.build();
    }
}

