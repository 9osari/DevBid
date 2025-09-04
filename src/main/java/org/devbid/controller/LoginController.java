package org.devbid.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.devbid.dto.UserLoginRequest;
import org.devbid.service.UserLoginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserLoginService loginService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/user/login")
    public String loginUser(@ModelAttribute UserLoginRequest request, HttpSession session, Model model) {
        try{
            loginService.login(request, session);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            return "login";
        }
    }
}
