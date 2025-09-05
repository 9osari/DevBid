package org.devbid.controller;

import lombok.RequiredArgsConstructor;
import org.devbid.dto.UserRegisterRequest;
import org.devbid.service.UserLookupService;
import org.devbid.service.UserRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserRegistrationService userRegistrationService;
    private final UserLookupService userLookupService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRegisterRequest("", "", "", "", ""));
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegisterRequest request) {
            userRegistrationService.registerUser(request);
            return "redirect:/login";
        }

    @GetMapping("/user/list")
    public String userList(Model model) {
        model.addAttribute("users", userLookupService.findAllUsers());
        return "user/userList";
    }
}
