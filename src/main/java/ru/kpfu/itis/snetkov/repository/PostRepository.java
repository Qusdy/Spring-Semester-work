package ru.kpfu.itis.snetkov.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.snetkov.entity.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    
    Page<Post> findByStatusOrderByPublishedAtDesc(Post.PostStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.group.id = :groupId AND p.status = :status")
    List<Post> findByGroupIdAndStatus(@Param("groupId") Integer groupId, 
                                      @Param("status") Post.PostStatus status);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Post> searchByTitleOrContent(@Param("query") String query, Pageable pageable);

    Page<Post> findByGroupIdAndStatus(Integer groupId, Post.PostStatus status, Pageable pageable);
}