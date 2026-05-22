package ru.kpfu.itis.snetkov.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
public class LocalStorageStrategy {
    
    @Value("${upload.local.path:./uploads/}")
    private String uploadPath;
    
    @Value("${upload.local.url-prefix:/uploads/}")
    private String urlPrefix;
    
    private Path rootLocation;
    
    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadPath);
        try {
            Files.createDirectories(rootLocation);
            log.info("Local storage initialized at: {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not create upload directory", e);
        }
    }
    
    public String upload(MultipartFile file) {
        try {
            String filename = generateFilename(file.getOriginalFilename());
            Path destinationFile = rootLocation.resolve(filename).normalize().toAbsolutePath();
            
            Files.copy(file.getInputStream(), destinationFile);
            
            String url = urlPrefix + filename;
            log.info("File saved locally: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Local upload failed", e);
            throw new RuntimeException("Local upload failed: " + e.getMessage());
        }
    }
    
    public void delete(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith(urlPrefix)) return;
        
        try {
            String filename = imageUrl.substring(urlPrefix.length());
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
            log.info("Deleted local file: {}", filename);
        } catch (IOException e) {
            log.error("Failed to delete local file", e);
        }
    }
    
    private String generateFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }
}