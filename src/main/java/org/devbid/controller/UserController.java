package org.devbid.controller;

import lombok.RequiredArgsConstructor;
import org.devbid.service.UserRegisterRequest;
import org.devbid.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Transactional
public class UserController {
    private UserService userService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRegisterRequest());
        return "register";
    }

    @PostMapping("/user/register")
    public String registerUser(@ModelAttribute UserRegisterRequest request, RedirectAttributes redirectAttributes) {
        try {
            userService.rigisterUser(request);
            redirectAttributes.addFlashAttribute("message", "User registered successfully");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/register";
        }
    }
}
