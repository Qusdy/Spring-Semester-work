package ru.kpfu.itis.snetkov.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.snetkov.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.post.id = :postId")
    List<Comment> findByPostIdOrderByCreatedAtAsc(Integer postId);

    @Query("SELECT c FROM Comment c WHERE c.createdAt = (" +
            "SELECT MAX(c2.createdAt) FROM Comment c2 WHERE c2.post.id = c.post.id" +
            ") ORDER BY c.createdAt DESC")
    Page<Comment> findLatestCommentOnEachPost(Pageable pageable);
}