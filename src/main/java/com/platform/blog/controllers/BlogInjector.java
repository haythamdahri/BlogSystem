package com.platform.blog.controllers;

import com.platform.blog.entities.User;
import com.platform.blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

// Exception and request Handling
@ControllerAdvice
public class BlogInjector {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Value("${spring.application.name}")
    private String applicationName;

    public User getConnectedUser() {
        SecurityContext securityContext = (SecurityContext) this.httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        if( securityContext != null ){
            String email = securityContext.getAuthentication().getName();
            User user = this.userService.getUser(email);
            return user;
        }
        return null;
    }


    @ModelAttribute
    public void injectBlog(Model model) {
        model.addAttribute("user", getConnectedUser());
        model.addAttribute("applicationName", this.applicationName);
    }

}
