package com.enzo.url.shortener.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUrlRequest(
        @NotBlank(message = "URL é obrigatória")
        @org.hibernate.validator.constraints.URL(message = "URL inválida")
        String originalUrl,

        Integer expiresInDays
) {}
