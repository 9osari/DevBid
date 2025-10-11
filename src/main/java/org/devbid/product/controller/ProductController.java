package org.devbid.product.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.product.application.CategoryService;
import org.devbid.product.domain.Category;
import org.devbid.product.dto.CategoryDto;
import org.devbid.user.application.UserService;
import org.devbid.user.domain.User;
import org.devbid.user.security.AuthUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductController {

    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/productMain")
    public String productMain() {
        return "product/productMain";
    }

    @GetMapping("/product/new")
    public String newProduct(@AuthenticationPrincipal AuthUser authUser, Model model) {
        User user = userService.findById(authUser.getId());
        model.addAttribute("user", user);

        List<CategoryDto> categoryDtoList = categoryService.getCategoryTree();
        System.out.println("categoryDtoList: " + categoryDtoList.toString());
        model.addAttribute("categoryDtoList", categoryDtoList);

        return "product/productRegister";
    }

    @PostMapping("/product-auction/new")
    public String newAuction(@AuthenticationPrincipal AuthUser authUser, Model model) {
        /*todo
        Category에서 dto를 사용한 이유와 재귀호출 블로그에 정리해보기
        Request DTO 2개 만들기
        Service에서 Product 저장
        이미지 저장
        Auction 저장 (Product 연결)
         */

        return null;
    }
}
