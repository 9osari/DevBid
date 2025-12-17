package org.devbid.home.controller;

import lombok.AllArgsConstructor;
import org.devbid.home.application.HomeApplicationService;
import org.devbid.home.dto.HomeData;
import org.devbid.user.security.oauth2.CustomOAuth2User;
import org.devbid.user.domain.User;
import org.devbid.user.application.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    private final UserService userService;
    private final HomeApplicationService homeApplicationService;

    @GetMapping
    public String home(@AuthenticationPrincipal CustomOAuth2User auth2User,
                       Model model) {
        if (auth2User != null) {
            model.addAttribute("isLoggedIn", true);
            User user = auth2User.getUser();
            model.addAttribute("nickname", user.getNickname().getValue());
            model.addAttribute("status", user.getStatus());
            model.addAttribute("userId", user.getId());
        } else {
            model.addAttribute("isLoggedIn", false);
        }


        HomeData homeData = homeApplicationService.getHomeData();
        model.addAttribute("totalOngoingAuctions", homeData.getTotalOngoingAuctions());
        model.addAttribute("totalProducts", homeData.getTotalProducts());
        model.addAttribute("userCount", homeData.getUserCount());
        model.addAttribute("todayDeals", homeData.getTodayDeals());
        model.addAttribute("hotAuctions", homeData.getHotAuctions());
        model.addAttribute("recentAuctions", homeData.getRecentAuctions());

        return "index";
    }
}
