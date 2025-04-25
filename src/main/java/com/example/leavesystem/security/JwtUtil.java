package com.example.leavesystem.security;

import com.example.leavesystem.entity.User;
import com.example.leavesystem.service.RedisTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RedisTokenService redisTokenService;

    @Value("${jwt.secret:your-very-strong-secret-key-is-32charss}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    // Make sure jwtSecret is a Base64-encoded string like "my-secret-key-123" → encoded
    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    private JwtParser getParser(){
        return Jwts.parserBuilder().setSigningKey(getSecretKey()).build();
    }

    //Creates tokens when a user logs in
    public String generateToken(User user) {
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        redisTokenService.storeToken(user.getEmail(), token, Duration.ofMillis(jwtExpirationMs));
        return token;
    }

    //Validates that the token hasn't been tampered or expired
    public boolean validateToken(String token) {
        try {
            getParser().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //Extracts the user's email from the token
    public String getEmailFromToken(String token) {
        return getParser().parseClaimsJws(token).getBody().getSubject();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        // Check if token is blacklisted
        if (redisTokenService.isTokenBlacklisted(token)) {
            return false;
        }

        final String username = getEmailFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public void invalidateToken(String token) {
        redisTokenService.invalidateToken(token);
    }

}
