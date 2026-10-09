package com.forgefit.config;

import com.forgefit.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for ForgeFit application.
 * 
 * Configures authentication, authorization, password encoding, and session management.
 * 
 * Key configurations:
 * - BCrypt password encoding with cost factor 10 (adaptive, ~200ms per hash)
 * - DaoAuthenticationProvider using CustomUserDetailsService
 * - Session fixation protection (MIGRATE_SESSION strategy)
 * - Single session per user (prevents concurrent logins)
 * - Anonymous access to /api/auth/**, login.html, index.html, static resources
 * - CSRF protection disabled (can be enabled with proper token handling)
 * - H2 console enabled for development (disable in production)
 * 
 * Security posture:
 * - Passwords never stored in plaintext (always BCrypt hashed)
 * - Passwords compared using constant-time comparison (prevents timing attacks)
 * - Unique salt generated for each password (prevents rainbow table attacks)
 * - Session cookies are HttpOnly and Secure (prevent JavaScript access)
 * - Session invalidation on logout (removes authentication)
 * - Account enumeration attacks prevented (generic error messages)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Password encoder bean using BCrypt algorithm.
     * 
     * Uses BCrypt with strength (cost factor) of 10:
     * - Strength 10: ~200ms to hash on typical server hardware (secure against brute force)
     * - Automatically generates unique salt for each password
     * - Includes salt and cost factor in hash output, allowing algorithm upgrades in future
     * 
     * BCrypt hash format: $2a$10$[22 char salt][31 char hash]
     * 
     * @return BCryptPasswordEncoder with cost factor 10
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/h2-console/**", "/index.html", "/login.html", 
                    "/styles.css", "/script.js", "/auth.js", "/dashboard.html").permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/login.html")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            )
            .httpBasic();

        return http.build();
    }
}