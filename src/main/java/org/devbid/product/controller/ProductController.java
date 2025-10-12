package org.devbid.product.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.product.application.CategoryService;
import org.devbid.product.application.ProductService;
import org.devbid.product.dto.CategoryDto;
import org.devbid.product.dto.ProductRegistrationRequest;
import org.devbid.user.application.UserService;
import org.devbid.user.domain.User;
import org.devbid.user.security.AuthUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/productMain")
    public String productMain() {
        return "product/productMain";
    }

    @GetMapping("/product/new")
    public String newProduct(@AuthenticationPrincipal AuthUser authUser, Model model) {
        User user = userService.findById(authUser.getId());
        model.addAttribute("user", user);

        List<CategoryDto> categoryDtoList = categoryService.getCategoryTree();
        model.addAttribute("categoryDtoList", categoryDtoList);

        return "product/productRegister";
    }

    @PostMapping("/product/new")
    public String createProduct(ProductRegistrationRequest request,
                                @AuthenticationPrincipal AuthUser authUser,
                                BindingResult result) {
        System.out.println("productName: " + request.productName());
        System.out.println("description: " + request.description());
        System.out.println("categoryId: " + request.categoryId());
        System.out.println("condition: " + request.condition());
        System.out.println("seller: " + authUser.getUsername());
        System.out.println("sellerId: " + authUser.getId());

        if(result.hasErrors()) {
            return "product/productRegister";
        }
        ProductRegistrationRequest registrationRequest = new ProductRegistrationRequest(
                request.productName(),
                request.description(),
                request.categoryId(),
                request.condition(),
                authUser.getId()
        );

        productService.registerProduct(registrationRequest);

        return "redirect:/productMain";
    }
}
