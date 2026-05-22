package ru.kpfu.itis.snetkov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.snetkov.entity.Comment;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.entity.User;
import ru.kpfu.itis.snetkov.repository.CommentRepository;
import ru.kpfu.itis.snetkov.repository.PostRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    public List<Comment> getCommentsByPost(Integer postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public Comment addComment(Integer postId, String content, User author) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));
        
        Comment comment = Comment.builder()
                .content(content)
                .author(author)
                .post(post)
                .build();
        
        Comment savedComment = commentRepository.save(comment);

        notificationService.notifyAboutNewComment(post, author);
        
        return savedComment;
    }

    public Comment updateComment(Integer commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));
        
        comment.setContent(content);
        comment.setIsEdited(true);
        
        return commentRepository.save(comment);
    }

    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    public boolean canEdit(Integer commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) return false;

        return user.getRole() == User.UserRole.ADMIN ||
                user.getRole() == User.UserRole.MODERATOR ||
                comment.getAuthor().getId().equals(user.getId());
    }

    public boolean canDelete(Integer commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) return false;
        
        return user.getRole() == User.UserRole.ADMIN ||
               user.getRole() == User.UserRole.MODERATOR ||
               comment.getAuthor().getId().equals(user.getId());
    }

    public Page<Comment> getLatestCommentOnEachPost(Pageable pageable) {
        return commentRepository.findLatestCommentOnEachPost(pageable);
    }
}