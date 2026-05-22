package ru.kpfu.itis.snetkov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.snetkov.entity.*;
import ru.kpfu.itis.snetkov.repository.NotificationRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public int getUnreadCount(User user) {
        return notificationRepository.countUnreadByUserId(user.getId());
    }

    public void markAsRead(Integer notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadByUserId(user.getId());
    }

    public void notifyAboutNewComment(Post post, User commentAuthor) {
        if (post.getAuthor().getId().equals(commentAuthor.getId())) return;

        Notification notification = Notification.builder()
                .user(post.getAuthor())
                .type("NEW_COMMENT")
                .title("Новый комментарий")
                .message(commentAuthor.getUsername() + " прокомментировал ваш пост \"" + post.getTitle() + "\"")
                .referenceType("POST")
                .referenceId(post.getId())
                .build();

        notificationRepository.save(notification);
        log.info("Created comment notification for user {}", post.getAuthor().getUsername());
    }
}