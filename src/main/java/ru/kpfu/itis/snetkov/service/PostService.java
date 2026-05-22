package ru.kpfu.itis.snetkov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.snetkov.entity.Group;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.entity.Tag;
import ru.kpfu.itis.snetkov.entity.User;
import ru.kpfu.itis.snetkov.repository.GroupRepository;
import ru.kpfu.itis.snetkov.repository.PostRepository;
import ru.kpfu.itis.snetkov.repository.TagRepository;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final TagService tagService;

    public Page<Post> getPublishedPosts(Pageable pageable) {
        return postRepository.findByStatusOrderByPublishedAtDesc(Post.PostStatus.PUBLISHED, pageable);
    }

    public Post getPostById(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));

        post.setViewsCount(post.getViewsCount() + 1);
        return postRepository.save(post);
    }

    public Page<Post> searchByTitleOrContent(String query, Pageable pageable) {
        return postRepository.searchByTitleOrContent(query, pageable);
    }

    public Post createPost(Post post, User author, Integer groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        post.setAuthor(author);
        group.ifPresent(post::setGroup);
        post.setStatus(Post.PostStatus.DRAFT);
        return postRepository.save(post);
    }

    public Post updatePost(Integer id, Post updatedPost) {
        Post post = getPostById(id);
        
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setImageUrl(updatedPost.getImageUrl());
        
        return postRepository.save(post);
    }

    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }

    public Post publishPost(Integer id) {
        Post post = getPostById(id);
        post.setStatus(Post.PostStatus.PUBLISHED);
        post.setPublishedAt(Instant.now());
        
        Post savedPost = postRepository.save(post);
        
        return savedPost;
    }

    public boolean canEdit(Integer postId, User user) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return false;

        return user.getRole() == User.UserRole.ADMIN ||
               user.getRole() == User.UserRole.MODERATOR ||
               post.getAuthor().getId().equals(user.getId());
    }

    public List<Post> getPostsByGroupAndStatus(Integer groupId, Post.PostStatus status) {
        return postRepository.findByGroupIdAndStatus(groupId, status);
    }

    public Page<Post> getGroupPosts(Integer groupId, Pageable pageable) {
        return postRepository.findByGroupIdAndStatus(groupId, Post.PostStatus.PUBLISHED, pageable);
    }

    public void addTagsToPost(Integer postId, String tagsInput) {
        Post post = getPostById(postId);
        String[] tagNames = tagsInput.split(",");

        for (String tagName : tagNames) {
            String cleanName = tagName.trim().toLowerCase();
            if (!cleanName.isEmpty()) {
                Tag tag = tagService.findOrCreate(cleanName);
                post.getTags().add(tag);
            }
        }
        postRepository.save(post);
    }

    public void removeTagFromPost(Integer postId, Integer tagId) {
        Post post = getPostById(postId);
        Tag tag = tagService.getTagById(tagId);
        post.getTags().remove(tag);
        postRepository.save(post);
    }
}