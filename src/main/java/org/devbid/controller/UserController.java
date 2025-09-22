package org.devbid.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.domain.User;
import org.devbid.domain.UserDto;
import org.devbid.dto.UserRegistrationRequest;
import org.devbid.dto.UserUpdateRequest;
import org.devbid.application.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/users/new")
    public String register(Model model) {
        model.addAttribute("user", new UserRegistrationRequest("", "", "", "", ""));
        return "user/register";
    }

    @PostMapping("/users")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationRequest request,
            BindingResult result,
            RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "user/register";
        }
        UserDto userDto = request.toDto();
        userService.registerUser(userDto);

        ra.addFlashAttribute("username", request.username());
        ra.addFlashAttribute("nickname", request.nickname());
        return "redirect:/registerSuccess";
    }

    @GetMapping("/registerSuccess")
    public String registerSuccess() {
        return "user/registerSuccess";
    }

    @GetMapping("/users")
    public String userList(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("userCount", userService.getUserCount());
        return "user/userList";
    }

    @GetMapping("/users/{id}/edit")
    public String userUpdate(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("form", new UserUpdateRequest(user.getEmail().getValue(), user.getNickname().getValue(), user.getPhone().getValue()));
        return "user/userUpdate";
    }

    @PutMapping("/users/{id}")
    public String userUpdateProc(@PathVariable Long id, @Valid @ModelAttribute("form") UserUpdateRequest request, BindingResult result,
                                 Authentication auth, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            User user = userService.findById(id);
            model.addAttribute("user", user);
            return "user/userUpdate";
        }
        userService.updateUser(id, request);
        ra.addFlashAttribute("msg", "User information has been updated.");
        return "redirect:/users";
    }

    @DeleteMapping("/users/{id}")
    public String userDelete(@PathVariable Long id, HttpServletRequest request, Authentication auth, RedirectAttributes ra) {
        try {
            User currentUser = userService.findByUsername(auth.getName());
            if(!currentUser.getId().equals(id)) {
                throw new SecurityException("You are not allowed to delete this user");
            }

            userService.deleteUser(id);

            // 삭제 성공 로그아웃
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if(session != null) {
                session.invalidate();
            }
        } catch (Exception e) {
            log.error("Failed to delete user: {}, error: {}", auth.getName(), e.getMessage());
            ra.addFlashAttribute("error", "계정 삭제 실패");
            return "redirect:/users/" + id + "/edit";
        }
        return "redirect:/";
    }
}
