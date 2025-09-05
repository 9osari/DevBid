package org.devbid.controller;

import lombok.RequiredArgsConstructor;
import org.devbid.dto.UserRegisterRequest;
import org.devbid.service.UserRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRegistrationService userService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRegisterRequest("", "", "", "", ""));
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegisterRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(request);
            redirectAttributes.addFlashAttribute("message", "success");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/register";
        }
    }
}
