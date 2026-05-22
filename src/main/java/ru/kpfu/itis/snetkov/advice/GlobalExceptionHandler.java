package ru.kpfu.itis.snetkov.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.kpfu.itis.snetkov.entity.User;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private void addUserToModel(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);
        }
    }

    @ExceptionHandler({NoResourceFoundException.class, NoSuchElementException.class})
    public ModelAndView handleNotFound(Exception ex, HttpServletRequest request, Authentication authentication, Model model) {
        log.error("404: {}", ex.getMessage());
        addUserToModel(authentication, model);
        model.addAttribute("title", "Страница не найдена");
        model.addAttribute("contextPath", request.getContextPath());
        return new ModelAndView("error/404", model.asMap(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex, HttpServletRequest request, Authentication authentication, Model model) {
        log.warn("403: {}", ex.getMessage());
        addUserToModel(authentication, model);
        model.addAttribute("title", "Доступ запрещён");
        model.addAttribute("contextPath", request.getContextPath());
        return new ModelAndView("error/403", model.asMap(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, HttpServletRequest request, Authentication authentication, Model model) {
        log.error("500: ", ex);
        addUserToModel(authentication, model);
        model.addAttribute("title", "Ошибка сервера");
        model.addAttribute("contextPath", request.getContextPath());
        return new ModelAndView("error/500", model.asMap(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}