package org.devbid.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.user.application.MyPageApplicationService;
import org.devbid.user.domain.CustomOAuth2User;
import org.devbid.user.domain.User;
import org.devbid.user.dto.MyPageData;
import org.devbid.user.dto.UserRegistrationRequest;
import org.devbid.user.dto.UserUpdateRequest;
import org.devbid.user.application.UserService;
import org.devbid.user.security.AuthUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MyPageApplicationService myPageApplicationService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/users/new")
    public String register(Model model) {
        model.addAttribute(
                "user",
                new UserRegistrationRequest(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                )
        );
        return "users/new";
    }

    @PostMapping("/users/new")
    public String registerUser(
            @Valid
            @ModelAttribute("user") UserRegistrationRequest request,
            BindingResult result,
            RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "users/new";
        }
        userService.registerUser(request);

        ra.addFlashAttribute("username", request.username());
        ra.addFlashAttribute("nickname", request.nickname());
        return "redirect:/userSuccess";
    }

    @GetMapping("/userSuccess")
    public String success() {
        return "users/success";
    }

    @GetMapping("/users")
    public String userList(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("userCount", userService.getUserCount());
        return "users/list";
    }

    @GetMapping("/users/myPage")
    public String myPage(@AuthenticationPrincipal CustomOAuth2User auth2User,
                         Model model,
                         @RequestParam(defaultValue = "0") int auctionPage,
                         @RequestParam(defaultValue = "0") int productPage,
                         @RequestParam(defaultValue = "0") int bidPage,
                         @RequestParam(defaultValue = "0") int buyoutPage,
                         @RequestParam(defaultValue = "5") int size) {
        Pageable auctionPageable = PageRequest.of(auctionPage, size);
        Pageable productPageable = PageRequest.of(productPage, size);
        Pageable bidPageable = PageRequest.of(bidPage, size);
        Pageable buyoutPageable = PageRequest.of(buyoutPage, size);

        MyPageData data = myPageApplicationService.getMyPageData(auth2User.getUser().getId(), auctionPageable, productPageable, bidPageable, buyoutPageable);

        model.addAttribute("user", data.getUser());
        model.addAttribute("ongoingAuctionCount", data.getAuctionActiveCount());
        model.addAttribute("productCount", data.getProductCount());
        model.addAttribute("totalAuctionCount", data.getAuctionCount());
        model.addAttribute("participatingAuctionCount", data.getParticipatingAuctionCount());
        model.addAttribute("recentAuctions", data.getRecentAuctions());
        model.addAttribute("recentProducts", data.getRecentProducts());
        model.addAttribute("recentBids", data.getRecentBids());
        model.addAttribute("recentBuyouts", data.getRecentBuyouts());

        // 각 섹션별 페이징 정보
        model.addAttribute("auctionCurrentPage", data.getAuctionCurrentPage());
        model.addAttribute("auctionTotalPages", data.getAuctionTotalPages());
        model.addAttribute("auctionHasNext", data.isAuctionHasNext());

        model.addAttribute("productCurrentPage", data.getProductCurrentPage());
        model.addAttribute("productTotalPages", data.getProductTotalPages());
        model.addAttribute("productHasNext", data.isProductHasNext());

        model.addAttribute("bidCurrentPage", data.getBidCurrentPage());
        model.addAttribute("bidTotalPages", data.getBidTotalPages());
        model.addAttribute("bidHasNext", data.isBidHasNext());

        model.addAttribute("buyoutCurrentPage", data.getBuyoutCurrentPage());
        model.addAttribute("buyoutTotalPages", data.getBuyoutTotalPages());
        model.addAttribute("buyoutHasNext", data.isBuyoutHasNext());

        model.addAttribute("pageSize", size);
        return "users/myPage";
    }

    @GetMapping("/users/{id}/edit")
    public String userUpdate(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("form",
                new UserUpdateRequest(
                        user.getEmail().getValue(),
                        user.getNickname().getValue(),
                        user.getPhone().getValue(),
                        user.getAddress().getZipCode(),
                        user.getAddress().getStreet(),
                        user.getAddress().getDetail()
                )
        );
        return "users/edit";
    }

    @PostMapping("/users/{id}/edit")
    public String userUpdateProc(@PathVariable Long id,
                                 @Valid @ModelAttribute("form") UserUpdateRequest request,
                                 BindingResult result,
                                 RedirectAttributes ra,
                                 Model model) {
        if (result.hasErrors()) {
            User user = userService.findById(id);
            model.addAttribute("user", user);
            return "users/edit";
        }
        userService.updateUser(id, request);
        ra.addFlashAttribute("msg", "User information has been updated.");
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/delete")
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

    // AJAX API 엔드포인트들
    @GetMapping("/api/users/myPage/auctions")
    @ResponseBody
    public Map<String, Object> getAuctions(@AuthenticationPrincipal CustomOAuth2User auth2User,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        MyPageData data = myPageApplicationService.getMyPageData(auth2User.getUser().getId(), pageable, PageRequest.of(0, 5), PageRequest.of(0, 5), PageRequest.of(0, 5));

        Map<String, Object> response = new HashMap<>();
        response.put("items", data.getRecentAuctions());
        response.put("currentPage", data.getAuctionCurrentPage());
        response.put("totalPages", data.getAuctionTotalPages());
        response.put("hasNext", data.isAuctionHasNext());
        return response;
    }

    @GetMapping("/api/users/myPage/products")
    @ResponseBody
    public Map<String, Object> getProducts(@AuthenticationPrincipal CustomOAuth2User auth2User,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        MyPageData data = myPageApplicationService.getMyPageData(auth2User.getUser().getId(), PageRequest.of(0, 5), pageable, PageRequest.of(0, 5), PageRequest.of(0, 5));

        Map<String, Object> response = new HashMap<>();
        response.put("items", data.getRecentProducts());
        response.put("currentPage", data.getProductCurrentPage());
        response.put("totalPages", data.getProductTotalPages());
        response.put("hasNext", data.isProductHasNext());
        return response;
    }

    @GetMapping("/api/users/myPage/bids")
    @ResponseBody
    public Map<String, Object> getBids(@AuthenticationPrincipal CustomOAuth2User auth2User,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        MyPageData data = myPageApplicationService.getMyPageData(auth2User.getUser().getId(), PageRequest.of(0, 5), PageRequest.of(0, 5), pageable, PageRequest.of(0, 5));

        Map<String, Object> response = new HashMap<>();
        response.put("items", data.getRecentBids());
        response.put("currentPage", data.getBidCurrentPage());
        response.put("totalPages", data.getBidTotalPages());
        response.put("hasNext", data.isBidHasNext());
        return response;
    }

    @GetMapping("/api/users/myPage/buyouts")
    @ResponseBody
    public Map<String, Object> getBuyouts(@AuthenticationPrincipal CustomOAuth2User auth2User,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        MyPageData data = myPageApplicationService.getMyPageData(auth2User.getUser().getId(), PageRequest.of(0, 5), PageRequest.of(0, 5), PageRequest.of(0, 5), pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("items", data.getRecentBuyouts());
        response.put("currentPage", data.getBuyoutCurrentPage());
        response.put("totalPages", data.getBuyoutTotalPages());
        response.put("hasNext", data.isBuyoutHasNext());
        return response;
    }
}
