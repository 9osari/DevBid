package org.devbid.product.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.product.application.awsService.S3Service;
import org.devbid.product.application.CategoryService;
import org.devbid.product.application.ProductService;
import org.devbid.product.dto.CategoryDto;
import org.devbid.product.dto.ProductRegistrationRequest;
import org.devbid.user.application.UserService;
import org.devbid.user.domain.User;
import org.devbid.user.security.AuthUser;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
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

    @PostMapping(value = "/product/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Map<String, Object> testImageJson(@RequestParam("mainImage") MultipartFile mainImage,
                                             @RequestParam(value = "subImages", required = false) List<MultipartFile> subImages) {

        String mainImageUrl = s3Service.upload(mainImage);

        System.out.println("Upload result: " + mainImageUrl);  // ← 결과 확인

        List<String> subImageUrls = (subImages == null || subImages.isEmpty())
                ? List.of()
                : s3Service.uploadMultiple(subImages);

        return Map.ofEntries(
                Map.entry("mainImageUrl", mainImageUrl != null ? mainImageUrl : ""),
                Map.entry("subImageUrls", subImageUrls != null ? subImageUrls : List.of())
        );
    }


    @PostMapping("/product/new")
    public String createProduct(
            ProductRegistrationRequest request,
            @RequestParam("mainImage") MultipartFile mainImage,
            @RequestParam("subImages") List<MultipartFile> subImages,
            @AuthenticationPrincipal AuthUser authUser,
            BindingResult result,
            RedirectAttributes ra
    ) {
        if(result.hasErrors()) {
            return "product/productRegister";
        }
        //파일을 S3에 업로드
        String mainImageUrl = s3Service.upload(mainImage);
        List<String> subImageUrl = s3Service.uploadMultiple(subImages);

        ProductRegistrationRequest registrationRequest = new ProductRegistrationRequest (
                request.productName(),
                request.description(),
                request.price(),
                request.categoryId(),
                request.condition(),
                authUser.getId(),
                mainImageUrl,
                subImageUrl
        );

        productService.registerProduct(registrationRequest);

        ra.addFlashAttribute("productName",request.productName());
        return "redirect:/productRegisterSuccess";
    }

    @GetMapping("/productRegisterSuccess")
    public String registerSuccess() {
        return "product/registerSuccess";
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
