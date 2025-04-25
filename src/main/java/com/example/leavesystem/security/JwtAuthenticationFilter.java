package com.example.leavesystem.security;

import com.example.leavesystem.service.RedisTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
//Runs before every request made
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RedisTokenService redisTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                email = jwtUtil.getEmailFromToken(jwt);
            } catch (Exception e) {
                logger.error("Error extracting email from JWT", e);
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            String storedToken = redisTokenService.getToken(email);
            boolean isValidToken = jwt.equals(storedToken) && jwtUtil.validateToken(jwt, userDetails);

            if (isValidToken) {
                String role = jwtUtil.extractAllClaims(jwt).get("role", String.class);
                // Log the role to debug
                logger.debug("Role extracted from JWT: " + role);

                // Make sure the role format is correct - Spring Security expects "ROLE_" prefix
                List<GrantedAuthority> authorities;
                if (role.startsWith("ROLE_")) {
                    authorities = List.of(new SimpleGrantedAuthority(role));
                } else {
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Log the authorities to debug
                logger.debug("User authenticated: " + email + " with authorities: " + userDetails.getAuthorities());
            }
        }


        filterChain.doFilter(request, response);
    }
}
