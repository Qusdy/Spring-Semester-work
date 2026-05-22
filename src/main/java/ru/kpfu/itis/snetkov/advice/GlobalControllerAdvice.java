package ru.kpfu.itis.snetkov.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.kpfu.itis.snetkov.entity.User;
import ru.kpfu.itis.snetkov.service.NotificationService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication authentication, HttpServletRequest request) {
        model.addAttribute("baseUrl", baseUrl);

        model.addAttribute("currentUrl", request.getRequestURL().toString());

        model.addAttribute("contextPath", request.getContextPath());
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);
        }
    }
}