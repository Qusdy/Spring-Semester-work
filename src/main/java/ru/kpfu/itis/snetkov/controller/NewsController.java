package ru.kpfu.itis.snetkov.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.itis.snetkov.api.generated.api.NewsApi;
import ru.kpfu.itis.snetkov.entity.NewsEntity;
import ru.kpfu.itis.snetkov.service.NewsService;
import ru.kpfu.itis.snetkov.api.generated.dto.CreateNewsRequest;
import ru.kpfu.itis.snetkov.api.generated.dto.UpdateNewsRequest;
import ru.kpfu.itis.snetkov.api.generated.dto.News;

import java.util.List;

@RestController
public class NewsController implements NewsApi {
    private final NewsService newsService;
    
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }
    
    @Override
    public ResponseEntity<List<News>> getAllNews() {
        List<News> news = newsService.findAll().stream()
                .map(n -> new News(n.getId(), n.getTitle(), n.getContent(), n.getAuthorId(), n.getCreatedAt().toString()))
                .toList();
        return ResponseEntity.ok(news);
    }

    @Override
    public ResponseEntity<News> getNewsById(Long id) {
        NewsEntity entity = newsService.findById(id);
        return entity != null 
                ? ResponseEntity.ok(toDto(entity))
                : ResponseEntity.notFound().build();
    }
    
    @Override
    public ResponseEntity<News> createNews(CreateNewsRequest request) {
        NewsEntity created = newsService.create(
                request.getTitle(),
                request.getContent(),
                request.getAuthorId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @Override
    public ResponseEntity<News> updateNews(Long id, UpdateNewsRequest request) {
        NewsEntity updated = newsService.update(
                id,
                request.getTitle(),
                request.getContent()
        );
        return updated != null 
                ? ResponseEntity.ok(toDto(updated))
                : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteNews(Long id) {
        return newsService.delete(id) 
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
    
    private News toDto(NewsEntity entity) {
        return new News(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getAuthorId(),
                entity.getCreatedAt().toString()
        );
    }
}