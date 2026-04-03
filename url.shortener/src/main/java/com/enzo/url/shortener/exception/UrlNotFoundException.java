package com.enzo.url.shortener.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String shortCode) {
        super("URL não encontrada para o código: " + shortCode);
    }
}
