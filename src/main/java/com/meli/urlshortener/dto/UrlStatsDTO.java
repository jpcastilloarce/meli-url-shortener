package com.meli.urlshortener.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Getter @Setter
public class UrlStatsDTO {
    private String shortUrl;
    private long accessCount;
    private LocalDate lastAccessTime;

    public UrlStatsDTO(String shortUrl, long accessCount, long lastAccessTime) {
        this.shortUrl = shortUrl;
        this.accessCount = accessCount;
        if (lastAccessTime == 0) {
            this.lastAccessTime = LocalDate.of(1900, 1, 1);
        }
        else {
            this.lastAccessTime = Instant.ofEpochSecond(lastAccessTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
    }
}