package ru.kpfu.itis.snetkov.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.snetkov.api.generated.dto.CommentRequest;
import ru.kpfu.itis.snetkov.api.generated.dto.CommentResponse;
import ru.kpfu.itis.snetkov.entity.Comment;
import ru.kpfu.itis.snetkov.entity.User;
import ru.kpfu.itis.snetkov.mapper.CommentMapper;
import ru.kpfu.itis.snetkov.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable Integer postId) {
        List<Comment> comments = commentService.getCommentsByPost(postId);
        List<CommentResponse> dtos = comments.stream()
                .map(commentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Integer postId,
                                                       @Valid @RequestBody CommentRequest request,
                                                      Authentication auth) {
        User user = (User) auth.getPrincipal();
        Comment comment = commentService.addComment(postId, request.getContent(), user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentMapper.toResponse(comment));
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("@commentService.canEdit(#commentId, authentication.principal)")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Integer commentId,
                                                          @Valid @RequestBody CommentRequest request) {
        Comment comment = commentService.updateComment(commentId, request.getContent());
        return ResponseEntity.ok(commentMapper.toResponse(comment));
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("@commentService.canDelete(#commentId, authentication.principal)")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}