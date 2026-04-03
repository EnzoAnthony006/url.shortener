package com.enzo.url.shortener.dto;

import java.time.LocalDateTime;

public record UrlResponse(
        Long id,
        String shortCode,
        String shortUrl,
        String originalUrl,
        Long accessCount,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {}
