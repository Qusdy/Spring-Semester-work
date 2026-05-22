package ru.kpfu.itis.snetkov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.snetkov.dto.TagDto;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.entity.Tag;
import ru.kpfu.itis.snetkov.service.PostService;
import ru.kpfu.itis.snetkov.service.TagService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagRestController {

    private final PostService postService;
    private final TagService tagService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<TagDto>> getPostTags(@PathVariable Integer postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(tagService.getTagsByPost(post));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TagDto>> searchTags(@RequestParam String q) {
        return ResponseEntity.ok(tagService.searchTags(q));
    }

    @PostMapping("/post/{postId}")
    @PreAuthorize("@postService.canEdit(#postId, authentication.principal)")
    public ResponseEntity<?> addTags(@PathVariable Integer postId,
                                     @RequestBody Map<String, String> request,
                                     Authentication auth) {
        String tagsInput = request.get("tags");
        if (tagsInput == null || tagsInput.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Введите теги"));
        }

        postService.addTagsToPost(postId, tagsInput);
        Post post = postService.getPostById(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("tags", tagService.getTagsByPost(post));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/post/{postId}/tag/{tagId}")
    @PreAuthorize("@postService.canEdit(#postId, authentication.principal)")
    public ResponseEntity<?> removeTag(@PathVariable Integer postId,
                                       @PathVariable Integer tagId,
                                       Authentication auth) {
        postService.removeTagFromPost(postId, tagId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}