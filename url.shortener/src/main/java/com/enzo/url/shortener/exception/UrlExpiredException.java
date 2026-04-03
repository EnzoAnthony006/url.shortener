package com.enzo.url.shortener.exception;

public class UrlExpiredException extends RuntimeException {

    public UrlExpiredException(String shortCode) {
        super("URL expirada para o código: " + shortCode);
    }
}
