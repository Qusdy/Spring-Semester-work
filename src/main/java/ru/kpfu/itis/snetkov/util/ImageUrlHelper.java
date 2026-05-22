package ru.kpfu.itis.snetkov.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImageUrlHelper {
    @Value("${upload.local.url-prefix:/uploads/}")
    private String localUrlPrefix;

    public boolean isCloudinaryUrl(String url) {
        if (url == null) return false;
        return url.contains("cloudinary.com") || 
               url.contains("res.cloudinary.com");
    }

    public boolean isLocalUrl(String url) {
        if (url == null) return false;
        return url.startsWith(localUrlPrefix) || 
               url.startsWith("/images/") ||
               url.startsWith("/uploads/");
    }

    public String getDisplayUrl(String url, String baseUrl) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        if (url.startsWith("/")) {
            return baseUrl + url;
        }
        
        return url;
    }
}