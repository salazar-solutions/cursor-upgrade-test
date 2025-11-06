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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private JwtAuthenticationFilter jwtAuthFilter;

    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("AdminSecurityConfiguration initialized");
        log.info("Active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        log.info("Default profiles: {}", Arrays.toString(environment.getDefaultProfiles()));
        log.info("========================================");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @org.springframework.context.annotation.Primary
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info(">>>>> CREATING SecurityFilterChain BEAN <<<<<");
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDevEnvironment = Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equals("local") ||
                        profile.equals("test") ||
                        profile.equals("integration"));

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

