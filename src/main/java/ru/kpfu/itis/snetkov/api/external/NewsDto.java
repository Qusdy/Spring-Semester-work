package ru.kpfu.itis.snetkov.api.external;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NewsDto implements Serializable {
    private String status;
    private int totalResults;
    private List<Article> articles;
    
    @Data
    public static class Article implements Serializable{
        private String author;
        private String title;
        private String description;
        private String url;
        private String urlToImage;
        private String publishedAt;
        private String content;
    }
}