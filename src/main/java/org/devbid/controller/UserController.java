package org.devbid.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.domain.User;
import org.devbid.domain.UserDto;
import org.devbid.domain.common.Result;
import org.devbid.dto.UserRegistrationRequest;
import org.devbid.dto.UserUpdateRequest;
import org.devbid.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRegistrationRequest("", "", "", "", ""));
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationRequest request,
            BindingResult bindingResult,
            RedirectAttributes ra,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "user/register";
        }
        UserDto userDto = request.toDto();
        Result<User> result = userService.registerUser(userDto);

        if(result.isSuccess()){
            ra.addFlashAttribute("username", request.username());
            ra.addFlashAttribute("nickname", request.nickname());
            return "redirect:/registerSuccess";
        } else {
            model.addAttribute("errorMessage", result.getErrorMessage());
            return "user/register";
        }
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
        Result<User> result = userService.findByUsername(auth.getName());
        if(!result.isSuccess()) {
            model.addAttribute("error", result.getErrorMessage());
            return "user/userUpdate";
        }
        User user = result.getData();
        model.addAttribute("user", user);
        model.addAttribute("form", new UserUpdateRequest(user.getEmail().getValue(), user.getNickname().getValue(), user.getPhone().getValue()));
        return "user/userUpdate";
    }

    @PostMapping("/user/update")
    public String userUpdateProc(@Valid @ModelAttribute("form") UserUpdateRequest request, BindingResult bindingResult,
                                 Authentication auth, RedirectAttributes ra, Model model) {
        if (bindingResult.hasErrors()) {
            Result<User> result = userService.findByUsername(auth.getName());
            if(!result.isSuccess()) {
                model.addAttribute("error", result.getErrorMessage());
                return "user/userUpdate";
            }
            model.addAttribute("user", result.getData());
            return "user/userUpdate";
        }
        Result<User> updateResult = userService.updateUser(auth.getName(), request);
        if(updateResult.isSuccess()){
            ra.addFlashAttribute("msg", "User updated successfully");
            return "redirect:/user/list";
        } else {
            model.addAttribute("errorMessage", updateResult.getErrorMessage());
            model.addAttribute("form", request);
            return "redirect:/user/update";
        }
    }

    @PostMapping("/user/delete")
    public String userDelete(HttpServletRequest request, Authentication auth) {
        String username = auth.getName();
        Result<User> result = userService.deleteUser(username);
        if(result.isSuccess()){
            //로그아웃
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if(session != null) {
                session.invalidate();   //세션 무효화
            }
        }
        return "redirect:/";
    }
}
