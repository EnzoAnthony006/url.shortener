package com.enzo.url.shortener.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String shortCode, String originalUrl, long ttlInSeconds) {
        redisTemplate.opsForValue().set(shortCode, originalUrl, ttlInSeconds, TimeUnit.SECONDS);
    }

    public Optional<String> findByShortCode(String shortCode) {
        String value = redisTemplate.opsForValue().get(shortCode);
        return Optional.ofNullable(value);
    }

    public void delete(String shortCode) {
        redisTemplate.delete(shortCode);
    }
}
