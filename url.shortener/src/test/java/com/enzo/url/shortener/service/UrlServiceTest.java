package com.enzo.url.shortener.service;

import com.enzo.url.shortener.domain.UrlMapping;
import com.enzo.url.shortener.domain.User;
import com.enzo.url.shortener.dto.CreateUrlRequest;
import com.enzo.url.shortener.dto.UrlResponse;
import com.enzo.url.shortener.exception.UrlNotFoundException;
import com.enzo.url.shortener.repository.UrlCacheRepository;
import com.enzo.url.shortener.repository.UrlMappingRepository;
import com.enzo.url.shortener.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @InjectMocks
    private UrlService urlService;

    @Value("${app.base-url}")
    private String baseUrl;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@email.com")
                .password("hash")
                .build();

        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:8080");
    }

    @Test
    void createUrl_shouldReturnUrlResponse_whenRequestIsValid() {
        CreateUrlRequest request = new CreateUrlRequest("https://google.com", 7);

        when(shortCodeGenerator.generate()).thenReturn("abc123");
        when(urlMappingRepository.findByShortCode("abc123")).thenReturn(Optional.empty());
        when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(i -> i.getArgument(0));

        UrlResponse response = urlService.createUrl(request, user);

        assertNotNull(response);
        assertEquals("abc123", response.shortCode());
        assertEquals("https://google.com", response.originalUrl());
        assertEquals("http://localhost:8080/abc123", response.shortUrl());
        verify(urlCacheRepository).save(eq("abc123"), eq("https://google.com"), anyLong());
    }

    @Test
    void resolveUrl_shouldReturnFromCache_whenCacheHit() {
        when(urlCacheRepository.findByShortCode("abc123"))
                .thenReturn(Optional.of("https://google.com"));

        String result = urlService.resolveUrl("abc123");

        assertEquals("https://google.com", result);
        verify(urlMappingRepository, never()).findByShortCode(any());
    }

    @Test
    void resolveUrl_shouldReturnFromDb_whenCacheMiss() {
        UrlMapping mapping = UrlMapping.builder()
                .shortCode("abc123")
                .originalUrl("https://google.com")
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .accessCount(0L)
                .build();

        when(urlCacheRepository.findByShortCode("abc123")).thenReturn(Optional.empty());
        when(urlMappingRepository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));

        String result = urlService.resolveUrl("abc123");

        assertEquals("https://google.com", result);
        verify(urlCacheRepository).save(eq("abc123"), eq("https://google.com"), anyLong());
    }

    @Test
    void resolveUrl_shouldThrowUrlNotFoundException_whenCodeNotExists() {
        when(urlCacheRepository.findByShortCode("xyz999")).thenReturn(Optional.empty());
        when(urlMappingRepository.findByShortCode("xyz999")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.resolveUrl("xyz999"));
    }

    @Test
    void deleteUrl_shouldThrowException_whenUserIsNotOwner() {
        User otherUser = User.builder().id(2L).email("other@email.com").password("hash").build();

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("abc123")
                .originalUrl("https://google.com")
                .user(otherUser)
                .createdAt(LocalDateTime.now())
                .accessCount(0L)
                .build();

        when(urlMappingRepository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));

        assertThrows(RuntimeException.class, () -> urlService.deleteUrl("abc123", user));
        verify(urlMappingRepository, never()).delete(any());
    }
}
