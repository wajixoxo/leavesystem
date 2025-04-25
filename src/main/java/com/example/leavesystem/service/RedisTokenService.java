package com.example.leavesystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "jwt:token:";
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    public void storeToken(String username, String token, Duration expiration) {
        String key = TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(key, token, expiration);
    }

    public String getToken(String username) {
        String key = TOKEN_PREFIX + username;
        Object token = redisTemplate.opsForValue().get(key);
        return token == null ? null : token.toString();
    }

    public void invalidateToken(String token) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "blacklisted", Duration.ofDays(1));
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    public void deleteToken(String username) {
        String key = TOKEN_PREFIX + username;
        redisTemplate.delete(key);
    }
}