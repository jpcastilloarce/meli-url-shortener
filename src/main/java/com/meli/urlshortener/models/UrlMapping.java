package com.meli.urlshortener.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "url_mapping", indexes = {
        @Index(name = "idx_short_url", columnList = "shortUrl"),
        @Index(name = "idx_long_url", columnList = "longUrl")
})
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String longUrl;
    @Column(unique = true)
    private String shortUrl;
    private long creationTime;
    private long accessCount;
    private long lastAccessTime;
}