package org.devbid.controller;

import lombok.AllArgsConstructor;
import org.devbid.application.UserApplicationService;
import org.devbid.domain.User;
import org.devbid.application.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    private final UserApplicationService userApplicationService;

    @GetMapping
        public String home(Model model) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                model.addAttribute("isLoggedIn", true);

                try {
                    User user = userApplicationService.findByUsername(auth.getName());
                    model.addAttribute("nickname", user.getNickname().getValue());
                    model.addAttribute("status", user.getStatus());
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
