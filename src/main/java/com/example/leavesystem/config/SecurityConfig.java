package com.example.leavesystem.config;

import com.example.leavesystem.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/authenticate/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // More specific first
                        .requestMatchers("/users/me").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers("/users").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/users/department/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/users/role/**").hasRole("ADMIN")
                        .requestMatchers("/users/assign-department/**").hasRole("ADMIN")
                        .requestMatchers("/users/create-department").hasRole("ADMIN")
                        .requestMatchers("/users/delete-user/**").hasRole("ADMIN")

                        .requestMatchers("/leave/apply").hasAnyRole("EMPLOYEE", "MANAGER")
                        .requestMatchers("/leave/status/employee/**").hasRole("EMPLOYEE")
                        .requestMatchers("/leave/approval/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/leave/status/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/reports/**").hasAnyRole("MANAGER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
