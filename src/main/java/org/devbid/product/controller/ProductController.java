package org.devbid.product.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.product.application.awsService.PresignedUrlData;
import org.devbid.product.application.awsService.S3Service;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final S3Service s3Service;
    private final RestClient.Builder builder;

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
    public String createProduct(
            ProductRegistrationRequest request,
            @AuthenticationPrincipal AuthUser authUser,
            BindingResult result,
            RedirectAttributes ra
    ) {
        if(result.hasErrors()) {
            return "product/productRegister";
        }

        // 이미지 URL은 프론트엔드에서 S3 업로드 후 받아옴
        ProductRegistrationRequest registrationRequest = new ProductRegistrationRequest (
                request.productName(),
                request.description(),
                request.price(),
                request.categoryId(),
                request.condition(),
                authUser.getId(),
                request.mainImageKey(),
                request.subImageKeys()
        );

        productService.registerProduct(registrationRequest);

        ra.addFlashAttribute("productName",request.productName());
        return "redirect:/productRegisterSuccess";
    }

    @PostMapping("/product/presigned-url")
    @ResponseBody
    public Map<String, String> getPresignedUrl(
            @RequestParam("filename") String filename,
            @RequestParam("contentType") String contentType
    ) {
        //pre-signed URL 생성
        PresignedUrlData data = s3Service.generatePresignedUrl(filename, contentType);
        return Map.of(
                "uploadUrl", data.uploadUrl(),
                "key", data.key()
        );
    }

    @GetMapping("/productRegisterSuccess")
    public String registerSuccess() {
        return "product/registerSuccess";
    }

    @GetMapping("/product/image-url")
    @ResponseBody
    public Map<String, String> getImageUrl(@RequestParam("key") String key) {
        String presignedGetUrl = s3Service.generatePresignedGetUrl(key);
        return Map.of("imageUrl", presignedGetUrl);
    }

    @GetMapping("/product/list")
    public String productList(Model model,  @AuthenticationPrincipal AuthUser authUser) {
        model.addAttribute("products", productService.findAllProducts());
        model.addAttribute("productCount", productService.getProductCount());
        return "product/myProductList";
    }


    @GetMapping("/product/my-list")
    public String myProductList(Model model, @AuthenticationPrincipal AuthUser authUser) {
        model.addAttribute("products", productService.findAllProductsBySellerId(authUser.getId()));
        model.addAttribute("productCount", productService.getProductCount());
        return "product/productList";
    }
}
