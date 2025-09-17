package com.ecommerce.project.security;

import com.ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // ✅ Enable method-level security
public class WebSecurityConfig {

    @Autowired UserDetailsServiceImpl userDetailsService;

    @Autowired private AuthTokenFilter authTokenFilter;

    @Autowired private AuthEntryPointJwt unauthorizedHandler;

    // Configure AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // BCryptPasswordEncoder for password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless authentication
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .exceptionHandling(
                        exception ->
                                exception.authenticationEntryPoint(
                                        (request, response, ex) ->
                                                response.sendError(
                                                        HttpServletResponse.SC_UNAUTHORIZED,
                                                        ex.getMessage()))) // Unauthorized handler
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // Permit login/signup routes
                                        .requestMatchers("/api/auth/**")
                                        .permitAll() // Public: login and signup

                                        // Permit Swagger-related routes (optional)
                                        .requestMatchers(
                                                "/swagger-ui.html",
                                                "/swagger-resources/**",
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**")
                                        .permitAll()

                                        // All other routes must be authenticated (including product routes, admin
                                        // routes, etc.)
                                        .anyRequest()
                                        .authenticated() // All other routes require authentication
                )
                .addFilterBefore(
                        authTokenFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter
                .build();
    }

    // CORS Configuration to allow cross-origin requests
    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of("*")); // Allow all origins (be more restrictive in production)
        configuration.setAllowedMethods(List.of("*")); // Allow all methods (GET, POST, etc.)
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setExposedHeaders(List.of("Authorization")); // Expose Authorization header
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(
                "/**", configuration); // Apply the CORS configuration to all paths
        return source;
    }
}
