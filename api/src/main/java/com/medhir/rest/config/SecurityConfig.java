package com.medhir.rest.config;

import com.medhir.rest.exception.CustomAccessDeniedHandler;
import com.medhir.rest.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible endpoints
                        .requestMatchers(
                                "/auth/**",
                                "/api/auth/**",
                                "/employee/id/*",
                                "/employee/update-request",
                                "/payslip/generate/**",
                                "/leads/**",
                                "/pipeline-stages/**",
                                "/api/attendance/upload"


                        ).permitAll()

                        .requestMatchers("/**").hasAnyAuthority("COMPANY_HEAD", "SUPERADMIN","EMPLOYEE","ADMIN","MANAGER")

                        // Only HR Admin can access /hradmin/**
                        .requestMatchers("/hradmin/**").hasAnyAuthority("HRADMIN", "SUPERADMIN")

                        // Only Super Admin can access /superadmin/**
                        .requestMatchers("/superadmin/**").hasAuthority("SUPERADMIN")

                        // Manager endpoints can be accessed by MANAGER and HRADMIN
                        .requestMatchers("/manager/**", "/employees/manager/**").hasAnyAuthority("MANAGER", "HRADMIN")

                        // Attendance endpoints can be accessed by MANAGER, HRADMIN, or SUPERADMIN
                        .requestMatchers("/api/attendance/**").hasAnyAuthority("EMPLOYEE", "MANAGER", "HRADMIN", "SUPERADMIN")

                        // Leave balance endpoints can be accessed by any authenticated user
                        .requestMatchers("/api/leave-balance/**").authenticated()

                        // Public holidays endpoints can be accessed by any authenticated user
                        .requestMatchers("/public-holidays/**").authenticated()

                        // Leave update status can be accessed by MANAGER or HRADMIN
                        .requestMatchers("/leave/update-status").hasAnyAuthority("MANAGER", "HRADMIN")

                        // Leave apply can be accessed by EMPLOYEE, MANAGER, or HRADMIN
                        .requestMatchers("/leave/apply").hasAnyAuthority("EMPLOYEE", "MANAGER", "HRADMIN")

                        // Leave endpoints can be accessed by EMPLOYEE, MANAGER, or HRADMIN
                        .requestMatchers("/leave/employee/**").hasAnyAuthority("EMPLOYEE", "MANAGER", "HRADMIN")
                        .requestMatchers(("employee/**")).hasAuthority("EMPLOYEE")

                        .requestMatchers("/expenses/**").hasAnyAuthority("EMPLOYEE","ACCOUNTANT")

                        // Income endpoints can be accessed by EMPLOYEE, MANAGER, or HRADMIN
                        .requestMatchers("/income/employee/**").hasAuthority("EMPLOYEE")
                        .requestMatchers("/income/manager/**").hasAuthority("MANAGER")
                        .requestMatchers("/income/**").hasAuthority("HRADMIN")
                        .requestMatchers("/vendors/**","/payments/**","/bills/**").hasAuthority("ACCOUNTANT")
                        // All other routes can be accessed by HR or Super Admin
                        .anyRequest().hasAnyAuthority("HRADMIN", "SUPERADMIN")
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint()) // 401 handler
                        .accessDeniedHandler(accessDeniedHandler())           // 403 handler
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(
                List.of("http://localhost:3000", "http://192.168.0.200:3000", "*.medhir.in")); config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // Allow cookies
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Using strength 12 for better security
    }
}