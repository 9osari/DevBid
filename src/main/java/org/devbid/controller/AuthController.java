package org.devbid.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devbid.domain.User;
import org.devbid.dto.UserRegistrationRequest;
import org.devbid.dto.UserUpdateRequest;
import org.devbid.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

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
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationRequest request,
            BindingResult result,
            RedirectAttributes ra
    ) {
        if (result.hasErrors()) {
            return "user/register";
        }
        userService.registerUser(request);

        ra.addFlashAttribute("username", request.username());
        ra.addFlashAttribute("nickname", request.nickname());
        return "redirect:/registerSuccess";
    }

    @GetMapping("/registerSuccess")
    public String registerSuccess() {
        return "user/registerSuccess";
    }

    @GetMapping("/user/list")
    public String userList(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("userCount", userService.getUserCount());
        return "user/userList";
    }

    @GetMapping("/user/update")
    public String userUpdate(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("form", new UserUpdateRequest(user.getEmail(), user.getNickname(), user.getPhone()));
        return "user/userUpdate";
    }

    @PostMapping("/user/update")
    public String userUpdateProc(@Valid @ModelAttribute("form") UserUpdateRequest request, BindingResult result,
                                 Authentication auth, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            User user = userService.findByUsername(auth.getName());
            model.addAttribute("user", user);
            return "user/userUpdate";
        }
        userService.updateUser(auth.getName(), request);
        ra.addFlashAttribute("msg", "User information has been updated.");
        return "redirect:/user/update";
    }
}
