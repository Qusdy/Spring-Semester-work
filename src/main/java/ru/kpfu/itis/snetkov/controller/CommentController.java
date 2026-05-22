package ru.kpfu.itis.snetkov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kpfu.itis.snetkov.entity.Comment;
import ru.kpfu.itis.snetkov.service.CommentService;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Value("${app.pagination.page-size:10}")
    private Integer PAGE_SIZE;

    @GetMapping("/latest")
    public String showLatestComments(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<Comment> commentsPage = commentService.getLatestCommentOnEachPost(pageable);
        
        model.addAttribute("comments", commentsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentsPage.getTotalPages());
        model.addAttribute("totalItems", commentsPage.getTotalElements());
        model.addAttribute("title", "Последние комментарии к постам");
        
        return "comments/latest";
    }
}