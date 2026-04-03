package com.enzo.url.shortener.dto;

public record LoginResponse(
        String token,
        String email
) {}
