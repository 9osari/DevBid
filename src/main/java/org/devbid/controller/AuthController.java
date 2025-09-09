package org.devbid.controller;

import lombok.RequiredArgsConstructor;
import org.devbid.dto.UserRegistrationRequest;
import org.devbid.service.UserLookupService;
import org.devbid.service.UserRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserRegistrationService userRegistrationService;
    private final UserLookupService userLookupService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRegistrationRequest("", "", "", "", ""));
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegistrationRequest request) {
        userRegistrationService.registerUser(request);
        return "redirect:/login";
    }

    @GetMapping("/user/list")
    public String userList(Model model) {
        model.addAttribute("users", userLookupService.findAllUsers());
        model.addAttribute("userCount", userLookupService.getUserCount());
        return "user/userList";
    }
}
