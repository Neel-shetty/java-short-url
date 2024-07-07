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
    public String shortenUrl(@RequestParam String url) {
        return urlShortenerService.shortenUrl(url);
    }

    @GetMapping("/{shortUrl}")
    public void redirectToUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String redirectUrl = urlShortenerService.getOriginalUrl(shortUrl);
        System.out.println(shortUrl + " -> " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
