package com.meli.urlshortener.controllers;

import com.meli.urlshortener.dto.UrlStatsDTO;
import com.meli.urlshortener.services.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class UrlShortenerController {
    @Autowired
    private UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String longUrl) {
        String shortUrl = urlShortenerService.shortenUrl(longUrl);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> getOriginalUrl(@PathVariable String shortUrl) {
        return urlShortenerService.getOriginalUrl(shortUrl)
                .map(urlMapping -> ResponseEntity.ok(urlMapping.getLongUrl()))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{shortUrl}")
    public ResponseEntity<?> deleteUrl(@PathVariable String shortUrl) {
        urlShortenerService.deleteUrl(shortUrl);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<List<UrlStatsDTO>> getStats() {
        List<UrlStatsDTO> urlMappings = urlShortenerService.getUrlStats();
        return ResponseEntity.ok(urlMappings);
    }

    @GetMapping("/redirect/{shortUrl}")
    public ResponseEntity<?> redirect(@PathVariable String shortUrl) {
        return urlShortenerService.getOriginalUrl(shortUrl)
                .map(urlMapping -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create(urlMapping.getLongUrl()));
                    return new ResponseEntity<>(headers, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}