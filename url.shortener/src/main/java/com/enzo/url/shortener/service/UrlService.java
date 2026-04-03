package com.enzo.url.shortener.service;

import com.enzo.url.shortener.domain.UrlMapping;
import com.enzo.url.shortener.dto.CreateUrlRequest;
import com.enzo.url.shortener.dto.UrlResponse;
import com.enzo.url.shortener.exception.UrlExpiredException;
import com.enzo.url.shortener.exception.UrlNotFoundException;
import com.enzo.url.shortener.repository.UrlCacheRepository;
import com.enzo.url.shortener.repository.UrlMappingRepository;
import com.enzo.url.shortener.util.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import com.enzo.url.shortener.domain.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlMappingRepository urlMappingRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlResponse createUrl(CreateUrlRequest request, User user) {
        String shortCode = generateUniqueCode();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = request.expiresInDays() != null
                ? now.plusDays(request.expiresInDays())
                : null;

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl(request.originalUrl())
                .shortCode(shortCode)
                .user(user)
                .createdAt(now)
                .expiresAt(expiresAt)
                .accessCount(0L)
                .build();

        urlMappingRepository.save(mapping);

        long ttl = request.expiresInDays() != null
                ? request.expiresInDays() * 86400L
                : 86400L * 30;

        urlCacheRepository.save(shortCode, request.originalUrl(), ttl);

        return toResponse(mapping);
    }

    public String resolveUrl(String shortCode) {
        Optional<String> cached = urlCacheRepository.findByShortCode(shortCode);
        if (cached.isPresent()) {
            return cached.get();
        }

        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        if (mapping.getExpiresAt() != null && mapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException(shortCode);
        }

        urlCacheRepository.save(shortCode, mapping.getOriginalUrl(), 86400L);
        return mapping.getOriginalUrl();
    }

    public List<UrlResponse> listUserUrls(User user) {
        return urlMappingRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteUrl(String shortCode, User user) {
        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        if (!mapping.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Você não tem permissão para deletar esta URL");
        }

        urlMappingRepository.delete(mapping);
        urlCacheRepository.delete(shortCode);
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = shortCodeGenerator.generate();
        } while (urlMappingRepository.findByShortCode(code).isPresent());
        return code;
    }

    private UrlResponse toResponse(UrlMapping mapping) {
        return new UrlResponse(
                mapping.getId(),
                mapping.getShortCode(),
                baseUrl + "/" + mapping.getShortCode(),
                mapping.getOriginalUrl(),
                mapping.getAccessCount(),
                mapping.getCreatedAt(),
                mapping.getExpiresAt()
        );
    }
}
