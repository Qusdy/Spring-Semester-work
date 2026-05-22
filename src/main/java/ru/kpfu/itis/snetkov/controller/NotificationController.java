package ru.kpfu.itis.snetkov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.snetkov.entity.Notification;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.entity.User;
import ru.kpfu.itis.snetkov.service.NotificationService;
import ru.kpfu.itis.snetkov.service.PostService;

import java.util.List;

@Controller
@RequestMapping("/notifications")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class NotificationController {
    private final PostService postService;
    private final NotificationService notificationService;

    @GetMapping
    public String listNotifications(Model model, Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Notification> notifications = notificationService.getUserNotifications(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("title", "Уведомления");
        return "notifications/list";
    }

    @GetMapping("/posts/{id}")
    public String post(@PathVariable Integer id, Model model) {
        Post post = postService.getPostById(id);
        return "redirect:/groups/" + post.getGroup().getId() + "/posts/" + id;
    }

    @PostMapping("/{id}/read")
    @ResponseBody
    public void markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
    }

    @PostMapping("/read-all")
    @ResponseBody
    public void markAllAsRead(Authentication auth) {
        User user = (User) auth.getPrincipal();
        notificationService.markAllAsRead(user);
    }

    @GetMapping("/unread-count")
    @ResponseBody
    public int getUnreadCount(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return notificationService.getUnreadCount(user);
    }
}