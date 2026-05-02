package ru.kpfu.itis.snetkov.service;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.snetkov.entity.NewsEntity;
import ru.kpfu.itis.snetkov.repository.NewsRepository;

import java.util.List;

@Service
public class NewsService {
    private final NewsRepository newsRepository;
    
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }
    
    public List<NewsEntity> findAll() { return newsRepository.findAll(); }
    
    public NewsEntity findById(Long id) { return newsRepository.findById(id).orElse(null); }
    
    public NewsEntity create(String title, String content, Long authorId) {
        NewsEntity news = new NewsEntity();
        news.setTitle(title);
        news.setContent(content);
        news.setAuthorId(authorId);
        return newsRepository.save(news);
    }
    
    public NewsEntity update(Long id, String title, String content) {
        NewsEntity news = findById(id);
        if (news != null) {
            news.setTitle(title);
            news.setContent(content);
            return newsRepository.save(news);
        }
        return null;
    }
    
    public boolean delete(Long id) {
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
            return true;
        }
        return false;
    }
}