package com.auth.authjwt.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redis;
    private static final String KEY_PREFIX = "blacklist:";

    public void blacklist(String token, long ttlSeconds) {
        redis.opsForValue().set(KEY_PREFIX + token, "1", ttlSeconds, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redis.hasKey(KEY_PREFIX + token);
    }
}
