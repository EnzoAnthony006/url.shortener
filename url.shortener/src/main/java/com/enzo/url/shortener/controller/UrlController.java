package com.enzo.url.shortener.controller;

import com.enzo.url.shortener.dto.CreateUrlRequest;
import com.enzo.url.shortener.dto.RegisterRequest;
import com.enzo.url.shortener.dto.UrlResponse;
import com.enzo.url.shortener.service.UrlAccessService;
import com.enzo.url.shortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.enzo.url.shortener.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final UrlAccessService urlAccessService;

    @PostMapping
    public ResponseEntity<UrlResponse> createUrl(@RequestBody @Valid CreateUrlRequest request,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.createUrl(request, user));
    }

    @GetMapping
    public ResponseEntity<List<UrlResponse>> listUrls(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(urlService.listUserUrls(user));
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode,
                                          @AuthenticationPrincipal User user) {
        urlService.deleteUrl(shortCode, user);
        urlAccessService.evictCache(shortCode);
        return ResponseEntity.noContent().build();
    }
}