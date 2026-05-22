package ru.kpfu.itis.snetkov.mapper;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.snetkov.api.generated.dto.CommentRequest;
import ru.kpfu.itis.snetkov.api.generated.dto.CommentResponse;
import ru.kpfu.itis.snetkov.entity.Comment;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.entity.User;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class CommentMapper {
    public CommentResponse toResponse(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId().longValue());
        response.setContent(comment.getContent());
        response.setAuthorId(comment.getAuthor().getId().longValue());
        response.setAuthorUsername(comment.getAuthor().getUsername());
        response.setCreatedAt(comment.getCreatedAt() == null ? null : OffsetDateTime.ofInstant(comment.getCreatedAt(), ZoneOffset.UTC));
        response.setIsEdited(comment.getIsEdited() != null && comment.getIsEdited());

        return response;
    }

    public Comment toEntity(CommentRequest request, Post post, User author) {
        if (request == null) {
            return null;
        }

        return Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .isEdited(false)
                .build();
    }

    public void updateEntity(Comment existingComment, CommentRequest request) {
        if (request == null || existingComment == null) {
            return;
        }

        existingComment.setContent(request.getContent());
        existingComment.setIsEdited(true);
    }
}
