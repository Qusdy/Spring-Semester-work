package ru.kpfu.itis.snetkov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.itis.snetkov.entity.Group;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.entity.User;
import ru.kpfu.itis.snetkov.service.GroupService;
import ru.kpfu.itis.snetkov.service.ImageStorageService;
import ru.kpfu.itis.snetkov.service.PostService;

import java.util.List;

@Controller
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final ImageStorageService imageStorageService;

    @GetMapping
    public String listGroups(Model model) {
        List<Group> groups = groupService.getAllGroups();
        for (Group group : groups) {
            if (group.getLogoUrl() != null) {
                group.setLogoUrl(imageStorageService.getImageUrl(group.getLogoUrl()));
            }
        }
        model.addAttribute("groups", groups);
        model.addAttribute("title", "Все группы");
        return "groups/list";
    }

    @GetMapping("/{id}")
    public String viewGroup(@PathVariable Integer id, Model model, Authentication authentication) {
        Group group = groupService.getGroupById(id);

        if (group.getLogoUrl() != null) {
            group.setLogoUrl(imageStorageService.getImageUrl(group.getLogoUrl()));
        }

        model.addAttribute("group", group);
        model.addAttribute("groupService", groupService);
        model.addAttribute("posts", groupService.getGroupPosts(id));
        model.addAttribute("title", group.getName());

        if (authentication != null && authentication.isAuthenticated()) {
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("isOwner", groupService.isOwner(id, currentUser));
        }

        return "groups/view";
    }

    @GetMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String newGroupForm(Model model) {
        model.addAttribute("group", new Group());
        model.addAttribute("title", "Создать группу");
        return "groups/form";
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String createGroup(@ModelAttribute Group group,
                              @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        User user = (User) auth.getPrincipal();

        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                String logoUrl = imageStorageService.uploadImage(logoFile);
                group.setLogoUrl(logoUrl);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Ошибка загрузки логотипа: " + e.getMessage());
                return "redirect:/groups/new";
            }
        }

        Group created = groupService.createGroup(group, user);
        redirectAttributes.addFlashAttribute("successMessage",
                "Группа \"" + created.getName() + "\" успешно создана!");
        return "redirect:/groups/" + created.getId();
    }

    @PreAuthorize("@groupService.isOwner(#id, authentication.principal) or hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editGroupForm(@PathVariable Integer id, Model model) {
        Group group = groupService.getGroupById(id);
        model.addAttribute("group", group);
        model.addAttribute("title", "Редактировать: " + group.getName());
        return "groups/form";
    }

    @PreAuthorize("@groupService.isOwner(#id, authentication.principal) or hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String updateGroup(@PathVariable Integer id,
                              @ModelAttribute Group group,
                              @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                              RedirectAttributes redirectAttributes) {
        Group existingGroup = groupService.getGroupById(id);

        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                if (existingGroup.getLogoUrl() != null &&
                        !existingGroup.getLogoUrl().equals("/images/default-group-logo.png")) {
                    imageStorageService.deleteImage(existingGroup.getLogoUrl());
                }
                String logoUrl = imageStorageService.uploadImage(logoFile);
                group.setLogoUrl(logoUrl);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Ошибка загрузки логотипа: " + e.getMessage());
                return "redirect:/groups/" + id + "/edit";
            }
        } else {
            group.setLogoUrl(existingGroup.getLogoUrl());
        }

        groupService.updateGroup(id, group);
        redirectAttributes.addFlashAttribute("successMessage", "Группа обновлена");
        return "redirect:/groups/" + id;
    }

    @PreAuthorize("@groupService.isOwner(#id, authentication.principal) or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            groupService.deleteGroup(id);
            redirectAttributes.addFlashAttribute("successMessage", "Группа успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении группы: " + e.getMessage());
        }
        return "redirect:/groups";
    }
}