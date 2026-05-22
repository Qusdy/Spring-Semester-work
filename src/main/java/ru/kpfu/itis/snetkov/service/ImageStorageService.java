package ru.kpfu.itis.snetkov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.snetkov.storage.CloudinaryStorageStrategy;
import ru.kpfu.itis.snetkov.storage.LocalStorageStrategy;
import ru.kpfu.itis.snetkov.util.ImageUrlHelper;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final CloudinaryStorageStrategy cloudinaryStrategy;
    private final LocalStorageStrategy localStorageStrategy;
    private final ImageUrlHelper imageUrlHelper;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_SIZE = 10 * 1024 * 1024;

    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateImage(file);

        if (cloudinaryStrategy.isAvailable()) {
            try {
                String url = cloudinaryStrategy.upload(file);
                log.info("Image uploaded to Cloudinary: {}", url);
                return url;
            } catch (Exception e) {
                log.error("Cloudinary failed, falling back to local storage", e);
            }
        }

        String url = localStorageStrategy.upload(file);
        log.info("Image uploaded to local storage: {}", url);
        return url;
    }

    public String getImageUrl(String storedUrl) {
        if (storedUrl == null || storedUrl.isEmpty()) {
            return null;
        }
        return imageUrlHelper.getDisplayUrl(storedUrl, baseUrl);
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null) return;

        if (imageUrlHelper.isCloudinaryUrl(imageUrl) && cloudinaryStrategy.isAvailable()) {
            try {
                cloudinaryStrategy.delete(imageUrl);
                log.info("Deleted from Cloudinary: {}", imageUrl);
            } catch (Exception e) {
                log.error("Failed to delete from Cloudinary", e);
            }
        }

        if (imageUrlHelper.isLocalUrl(imageUrl)) {
            localStorageStrategy.delete(imageUrl);
        }
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new RuntimeException("Неподдерживаемый формат. Используйте JPG, PNG, GIF или WEBP");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("Файл слишком большой. Максимум 10MB");
        }
    }
}