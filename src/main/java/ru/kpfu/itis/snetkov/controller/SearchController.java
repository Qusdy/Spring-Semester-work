package ru.kpfu.itis.snetkov.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kpfu.itis.snetkov.api.external.NewsDto;
import ru.kpfu.itis.snetkov.api.external.service.ExternalNewsService;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.service.PostService;

import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final PostService postService;
    private final ExternalNewsService externalNewsService;

    @Value("${app.pagination.page-size:10}")
    private Integer PAGE_SIZE;

    @GetMapping("/search")
    public String searchPosts(@RequestParam(required = false) String q,
                              @RequestParam(defaultValue = "false") boolean external,
                              @RequestParam(defaultValue = "0") int page,
                              Model model,
                              Authentication auth) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("publishedAt").descending());

        if (external) {
            if (q == null) {
                model.addAttribute("posts", List.of());
            }
            NewsDto news = externalNewsService.fetchNews(q != null ? q : "technology");
            List<NewsDto.Article> articles = news != null && news.getArticles() != null
                    ? news.getArticles() : List.of();

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), articles.size());
            List<NewsDto.Article> pagedArticles = start < articles.size()
                    ? articles.subList(start, end) : List.of();

            model.addAttribute("posts", pagedArticles);
            model.addAttribute("totalPages", (int) Math.ceil(articles.size() / (double) PAGE_SIZE));
            model.addAttribute("isExternal", true);
        } else {
            Page<Post> postPage;
            if (q != null && !q.isEmpty()) {
                postPage = postService.searchByTitleOrContent(q, pageable);
            } else {
                postPage = postService.getPublishedPosts(pageable);
            }
            model.addAttribute("posts", postPage.getContent());
            model.addAttribute("totalPages", postPage.getTotalPages());
            model.addAttribute("isExternal", false);
        }

        model.addAttribute("query", q);
        model.addAttribute("currentPage", page);
        model.addAttribute("external", external);
        model.addAttribute("title", "Поиск");

        return "posts/search";
    }
}