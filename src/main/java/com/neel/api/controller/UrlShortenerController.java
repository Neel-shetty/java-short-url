package com.neel.api.controller;

import com.neel.api.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public String shortenUrl(@RequestParam String url, @RequestParam(required = false) Long expiryTime) {
        String shortUrl = urlShortenerService.shortenUrl(url, expiryTime);
        return "{\"shortUrl\":\"" + shortUrl + "\"}";
    }

    @GetMapping("/{shortUrl}")
    public void redirectToUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String redirectUrl = urlShortenerService.getOriginalUrl(shortUrl);
        if (redirectUrl == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        System.out.println(shortUrl + " -> " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
