package com.enzo.url.shortener.service;

import com.enzo.url.shortener.repository.UrlCacheRepository;
import com.enzo.url.shortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlAccessService {

    private final UrlMappingRepository urlMappingRepository;
    private final UrlCacheRepository urlCacheRepository;

    public void incrementAcess(String shortCode) {
        urlMappingRepository.findByShortCode(shortCode).ifPresent(urlMapping -> {
            urlMapping.setAccessCount(urlMapping.getAccessCount() + 1);
            urlMappingRepository.save(urlMapping);
        });
    }

    public void evictCache(String shortCode) {
        urlCacheRepository.delete(shortCode);
    }
}
