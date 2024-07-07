package com.neel.api.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UrlShortenerService {

    private final ConcurrentMap<String, UrlEntry> urlMap = new ConcurrentHashMap<>();

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?" + // Optional scheme
                    "(([\\w\\d\\-]+\\.)+[\\w\\d\\-]+|" + // Domain
                    "localhost|" + // OR localhost
                    "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})" + // OR IPv4
                    "(:\\d{1,5})?" + // Optional port
                    "(/.*)?$", // Optional path
            Pattern.CASE_INSENSITIVE);

    public String shortenUrl(String originalUrl) {
        validateUrl(originalUrl);
        String shortUrl;

        shortUrl = generateShortUrl(originalUrl);

        urlMap.put(shortUrl, new UrlEntry(originalUrl));
        System.out.println(urlMap);
        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        UrlEntry entry = urlMap.get(shortUrl);
        if (entry != null && (entry.getExpiry() == null || entry.getExpiry() > System.currentTimeMillis())) {
            return entry.getOriginalUrl();
        }
        return null; // Return null or handle URL not found/expired case appropriately
    }

    private void validateUrl(String url) {
        if (!isValidUrlFormat(url)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
    }

    private boolean isValidUrlFormat(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        return matcher.matches();
    }

    private String generateShortUrl(String originalUrl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash).substring(0, 6);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private static class UrlEntry {
        private final String originalUrl;
        private final Long expiry; // Use Long for nullable field

        UrlEntry(String originalUrl) {
            this.originalUrl = originalUrl;
            this.expiry = null; // Default to no expiry
        }

        UrlEntry(String originalUrl, long expiry) {
            this.originalUrl = originalUrl;
            this.expiry = expiry;
        }

        public String getOriginalUrl() {
            return originalUrl;
        }

        public Long getExpiry() {
            return expiry;
        }
    }
}
