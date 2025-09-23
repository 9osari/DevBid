package org.devbid.controller;

import lombok.AllArgsConstructor;
import org.devbid.domain.UserEntity;
import org.devbid.application.UserService;
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

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("isLoggedIn", true);

            try {
                UserEntity userEntity = userService.findByUsername(auth.getName());
                model.addAttribute("nickname", userEntity.getNickname().getValue());
                model.addAttribute("status", userEntity.getStatus());
                model.addAttribute("userId", userEntity.getId());
            } catch (Exception e) {
                // 사용자 조회 실패시 로그아웃 상태로 처리
                model.addAttribute("isLoggedIn", false);
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "index";
    }
}
