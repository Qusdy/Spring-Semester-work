package ru.kpfu.itis.snetkov.api.external.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kpfu.itis.snetkov.api.external.NewsDto;

@Service
@Slf4j
public class ExternalNewsService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${news.api.url:https://newsapi.org/v2/everything}")
    private String apiUrl;

    @Value("${news.api.key:}")
    private String apiKey;

    @Value("${news.api.enabled:false}")
    private boolean enabled;

    @Cacheable(value = "externalNews", key = "#query", unless = "#result == null")
    public NewsDto fetchNews(String query) {
        if (!enabled || apiKey.isEmpty()) {
            log.warn("External API is disabled or API key missing");
            return null;
        }

        String url = apiUrl + "?q=" + query + "&pageSize=10&apiKey=" + apiKey;

        try {
            ResponseEntity<NewsDto> response = restTemplate.getForEntity(url, NewsDto.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch news", e);
            return null;
        }
    }
}