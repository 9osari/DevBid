package org.devbid.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.devbid.domain.User;
import org.devbid.security.SessionManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {
    private final SessionManager sessionManager;

    @GetMapping
    public String home(HttpSession session, Model model) {
        User loginUser = sessionManager.getLoginUser(session);
        if (loginUser != null) {
            //로그인됨
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("username", loginUser.getUsername());
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "index";
    }
}
