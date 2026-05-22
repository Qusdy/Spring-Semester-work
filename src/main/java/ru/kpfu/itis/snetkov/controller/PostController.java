package ru.kpfu.itis.snetkov.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.itis.snetkov.entity.Comment;
import ru.kpfu.itis.snetkov.entity.Group;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.entity.User;
import ru.kpfu.itis.snetkov.service.CommentService;
import ru.kpfu.itis.snetkov.service.GroupService;
import ru.kpfu.itis.snetkov.service.ImageStorageService;
import ru.kpfu.itis.snetkov.service.PostService;

import java.util.List;

@Controller
@RequestMapping("/groups/{groupId}/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final GroupService groupService;
    private final ImageStorageService imageStorageService;

    @Value("${app.pagination.page-size:10}")
    private Integer PAGE_SIZE;

    @GetMapping
    public String listPublishedPosts(@PathVariable Integer groupId, @RequestParam(defaultValue = "0") int page, Model model) {
        Group group = groupService.getGroupById(groupId);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("publishedAt").descending());
        Page<Post> postsPage = postService.getGroupPosts(groupId, pageable);

        model.addAttribute("group", group);
        model.addAttribute("posts", postsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postsPage.getTotalPages());
        model.addAttribute("title", "Посты группы: " + group.getName());

        return "posts/list";
    }

    @PreAuthorize("@groupService.isOwner(#groupId, authentication.principal) or hasRole('ADMIN')")
    @GetMapping("/drafts")
    public String listDrafts(@PathVariable Integer groupId, Model model, Authentication auth) {
        Group group = groupService.getGroupById(groupId);
        List<Post> drafts = postService.getPostsByGroupAndStatus(groupId, Post.PostStatus.DRAFT);

        model.addAttribute("group", group);
        model.addAttribute("drafts", drafts);
        model.addAttribute("title", "Черновики: " + group.getName());
        return "posts/drafts";
    }

    @GetMapping("/{postId}")
    public String viewPost(@PathVariable Integer groupId, @PathVariable Integer postId, Model model, Authentication auth) {
        Post post = postService.getPostById(postId);
        Group group = groupService.getGroupById(groupId);

        if (post.getStatus() == Post.PostStatus.DRAFT) {
            if (auth == null || !auth.isAuthenticated()) {
                throw new AccessDeniedException("Пост не опубликован");
            }
            User user = (User) auth.getPrincipal();
            if (!group.getOwner().getId().equals(user.getId()) && user.getRole() != User.UserRole.ADMIN) {
                throw new AccessDeniedException("У вас нет доступа к черновику");
            }
        }

        boolean isOwner = false;
        if (auth != null && auth.isAuthenticated()) {
            User currentUser = (User) auth.getPrincipal();
            isOwner = groupService.isOwner(groupId, currentUser);
        }
        model.addAttribute("isOwner", isOwner);

        List<Comment> comments = commentService.getCommentsByPost(postId);

        model.addAttribute("post", post);
        model.addAttribute("group", group);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("title", post.getTitle());
        return "posts/view";
    }

    @PreAuthorize("@groupService.isOwner(#groupId, authentication.principal) or hasRole('ADMIN')")
    @GetMapping("/new")
    public String newPostForm(@PathVariable Integer groupId, Model model) {
        Group group = groupService.getGroupById(groupId);
        Post post = new Post();
        post.setGroup(group);

        model.addAttribute("post", post);
        model.addAttribute("groupId", groupId);
        model.addAttribute("title", "Новый пост в группе: " + group.getName());
        return "posts/form";
    }

    @PreAuthorize("@groupService.isOwner(#groupId, authentication.principal) or hasRole('ADMIN')")
    @PostMapping
    public String createPost(@PathVariable Integer groupId,
                             @Valid @ModelAttribute Post post,
                             BindingResult result,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "posts/form";
        }

        User user = (User) auth.getPrincipal();

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = imageStorageService.uploadImage(imageFile);
                post.setImageUrl(imageUrl);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ошибка загрузки: " + e.getMessage());
                return "redirect:/groups/" + groupId + "/posts/new";
            }
        }

        Post created = postService.createPost(post, user, groupId);
        redirectAttributes.addFlashAttribute("successMessage", "Пост сохранён как черновик");
        return "redirect:/groups/" + groupId + "/posts/" + created.getId();
    }

    @PreAuthorize("@groupService.isOwner(#groupId, authentication.principal) or hasRole('ADMIN')")
    @GetMapping("/{postId}/edit")
    public String editPostForm(@PathVariable Integer groupId, @PathVariable Integer postId, Model model) {
        Post post = postService.getPostById(postId);
        Group group = groupService.getGroupById(groupId);

        model.addAttribute("post", post);
        model.addAttribute("groupId", groupId);
        model.addAttribute("title", "Редактировать: " + post.getTitle());
        return "posts/form";
    }

    @PreAuthorize("@groupService.isOwner(#groupId, authentication.principal) or hasRole('ADMIN')")
    @PostMapping("/{postId}")
    public String updatePost(@PathVariable Integer groupId,
                             @PathVariable Integer postId,
                             @Valid @ModelAttribute Post post,
                             BindingResult result,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "posts/form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = imageStorageService.uploadImage(imageFile);
                post.setImageUrl(imageUrl);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ошибка загрузки: " + e.getMessage());
                return "redirect:/groups/" + groupId + "/posts/" + postId + "/edit";
            }
        }

        postService.updatePost(postId, post);
        redirectAttributes.addFlashAttribute("successMessage", "Пост обновлён");
        return "redirect:/groups/" + groupId + "/posts/" + postId;
    }

    @PreAuthorize("@groupService.isOwner(#groupId, authentication.principal) or hasRole('ADMIN')")
    @PostMapping("/{postId}/publish")
    public String publishPost(@PathVariable Integer groupId,
                              @PathVariable Integer postId,
                              RedirectAttributes redirectAttributes) {
        postService.publishPost(postId);
        redirectAttributes.addFlashAttribute("successMessage", "Пост опубликован!");
        return "redirect:/groups/" + groupId + "/posts";
    }

    @PreAuthorize("@groupService.isOwner(#groupId, authentication.principal) or hasRole('ADMIN')")
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Integer groupId,
                             @PathVariable Integer postId,
                             RedirectAttributes redirectAttributes) {
        postService.deletePost(postId);
        redirectAttributes.addFlashAttribute("successMessage", "Пост удалён");
        return "redirect:/groups/" + groupId + "/posts";
    }
}