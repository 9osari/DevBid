package org.devbid.controller;

import lombok.AllArgsConstructor;
import org.devbid.domain.User;
import org.devbid.domain.common.Result;
import org.devbid.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("isLoggedIn", true);

            Result<User> userResult = userService.findByUsername(auth.getName());
            model.addAttribute("nickname", userResult.getData().getNickname().getValue());
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "index";
    }
}
