package com.ecommerce.project.security;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // ✅ enable method-level security
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        // Set session management to stateless
        return http// Enable CORS with default settings
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        exception ->
                                exception.authenticationEntryPoint(
                                        (request, response, ex) ->
                                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage())))
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers("/**")
                                        .permitAll()
                                        .requestMatchers("/swagger-ui.html")
                                        .permitAll()
                                        .requestMatchers("/swagger-resources/**")
                                        .permitAll()
                                        .requestMatchers("/swagger-ui/**")
                                        .permitAll()
                                        .requestMatchers("/v3/api-docs")
                                        .permitAll()
                                        .requestMatchers("/v3/api-docs/**")
                                        .permitAll()
                                        .requestMatchers("/swagger-resources/**")
                                        .permitAll()
                                        .requestMatchers("/configuration/ui")
                                        .permitAll()
                                        .requestMatchers("/webjars/**")
                                        .permitAll()
                                        .requestMatchers("/management/**")
                                        .permitAll()
                                        .requestMatchers("/v1/ping")
                                        .permitAll()
                                        .requestMatchers(
                                                "/v1/api/migration/**",
                                                "/v1/api/migration/apply-changelog",
                                                "/v1/api/migration/initialize-schema/**",
                                                "/v1/api/migration/load-master-org-data")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(perRequestJwtValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    // Used by Spring Security if CORS is enabled.
    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(
                List.of("Authorization")); // Add if you expose any custom headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

