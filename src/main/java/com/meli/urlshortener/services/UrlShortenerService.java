package com.meli.urlshortener.services;

import com.meli.urlshortener.dto.UrlStatsDTO;
import com.meli.urlshortener.models.UrlMapping;
import com.meli.urlshortener.repositories.UrlMappingRepository;
import com.meli.urlshortener.util.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UrlShortenerService {
    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String URL_CACHE_PREFIX = "url:";

    public String shortenUrl(String longUrl) {
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setLongUrl(longUrl);
        urlMapping.setCreationTime(Instant.now().getEpochSecond());
        urlMapping.setAccessCount(0);
        urlMapping.setLastAccessTime(0);
        UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);

        String shortUrl;
        do {
            shortUrl = Base62Encoder.encode(savedUrlMapping.getId());
        } while (urlMappingRepository.findByShortUrl(shortUrl).isPresent());

        savedUrlMapping.setShortUrl(shortUrl);

        urlMappingRepository.save(savedUrlMapping);

        redisTemplate.opsForValue().set(URL_CACHE_PREFIX + shortUrl, savedUrlMapping, 1, TimeUnit.DAYS);

        return shortUrl;
    }

    public Optional<UrlMapping> getOriginalUrl(String shortUrl) {
        UrlMapping urlMapping = (UrlMapping) redisTemplate.opsForValue().get(URL_CACHE_PREFIX + shortUrl);
        if (urlMapping != null) {
            urlMapping.setAccessCount(urlMapping.getAccessCount() + 1);
            urlMapping.setLastAccessTime(Instant.now().getEpochSecond());
            redisTemplate.opsForValue().set(URL_CACHE_PREFIX + shortUrl, urlMapping, 1, TimeUnit.DAYS);
            return Optional.of(urlMapping);
        }

        Optional<UrlMapping> urlMappingOptional = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMappingOptional.isPresent()) {
            urlMapping = urlMappingOptional.get();
            urlMapping.setAccessCount(urlMapping.getAccessCount() + 1);
            urlMapping.setLastAccessTime(Instant.now().getEpochSecond());
            urlMappingRepository.save(urlMapping);

            redisTemplate.opsForValue().set(URL_CACHE_PREFIX + shortUrl, urlMapping, 1, TimeUnit.DAYS);
        }
        return urlMappingOptional;
    }

    @Transactional
    public void deleteUrl(String shortUrl) {
        urlMappingRepository.deleteByShortUrl(shortUrl);

        redisTemplate.delete(URL_CACHE_PREFIX + shortUrl);
    }

    public List<UrlStatsDTO> getUrlStats() {
        return urlMappingRepository.findAll().stream()
                .map(urlMapping -> new UrlStatsDTO(
                        urlMapping.getShortUrl(),
                        urlMapping.getAccessCount(),
                        urlMapping.getLastAccessTime()
                ))
                .collect(Collectors.toList());
    }
}