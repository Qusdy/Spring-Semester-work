package ru.kpfu.itis.snetkov.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class CloudinaryStorageStrategy {
    
    private Cloudinary cloudinary;
    
    @Value("${cloudinary.cloud-name:}")
    private String cloudName;
    
    @Value("${cloudinary.api-key:}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret:}")
    private String apiSecret;
    
    @Value("${cloudinary.enabled:true}")
    private boolean enabled;
    
    @PostConstruct
    public void init() {
        if (enabled && isConfigured()) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
            ));
            log.info("Cloudinary initialized");
        } else {
            log.warn("Cloudinary not configured, will use local storage");
        }
    }
    
    private boolean isConfigured() {
        return cloudName != null && !cloudName.isEmpty() &&
               apiKey != null && !apiKey.isEmpty() &&
               apiSecret != null && !apiSecret.isEmpty();
    }
    
    public String upload(MultipartFile file) {
        if (!isAvailable()) {
            throw new RuntimeException("Cloudinary not available");
        }
        
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "news_platform",
                    "allowed_formats", "jpg,jpeg,png,gif,webp"
                )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Cloudinary upload failed", e);
            throw new RuntimeException("Cloudinary upload failed: " + e.getMessage());
        }
    }

    public void delete(String imageUrl) {
        if (!isAvailable() || imageUrl == null) {
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Deleted from Cloudinary: {}", publicId);
            }
        } catch (Exception e) {
            log.error("Failed to delete from Cloudinary: {}", imageUrl, e);
        }
    }

    private String extractPublicId(String url) {
        if (url == null || !url.contains("cloudinary.com")) {
            return null;
        }

        try {
            String uploadMarker = "/upload/";
            int uploadIndex = url.indexOf(uploadMarker);
            if (uploadIndex == -1) return null;

            String afterUpload = url.substring(uploadIndex + uploadMarker.length());
            if (afterUpload.startsWith("v")) {
                int slashIndex = afterUpload.indexOf("/");
                if (slashIndex != -1) {
                    afterUpload = afterUpload.substring(slashIndex + 1);
                }
            }
            int dotIndex = afterUpload.lastIndexOf(".");
            if (dotIndex != -1) {
                afterUpload = afterUpload.substring(0, dotIndex);
            }
            return afterUpload;
        } catch (Exception e) {
            log.error("Failed to extract public_id from URL: {}", url, e);
            return null;
        }
    }
    
    public boolean isAvailable() {
        return enabled && cloudinary != null && isConfigured();
    }
}