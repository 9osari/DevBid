package org.devbid.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.product.application.awsService.PresignedUrlData;
import org.devbid.product.application.awsService.S3Service;
import org.devbid.product.application.CategoryService;
import org.devbid.product.application.ProductService;
import org.devbid.product.domain.Product;
import org.devbid.product.dto.CategoryDto;
import org.devbid.product.dto.ProductListResponse;
import org.devbid.product.dto.ProductRegistrationRequest;
import org.devbid.product.dto.ProductUpdateRequest;
import org.devbid.user.application.UserService;
import org.devbid.user.domain.User;
import org.devbid.user.security.AuthUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/productsMain")
    public String productsMain() {
        return "products/productsMain";
    }

    @GetMapping("/products/new")
    public String newProduct(@AuthenticationPrincipal AuthUser authUser, Model model) {
        User user = userService.findById(authUser.getId());
        model.addAttribute("user", user);

        List<CategoryDto> categoryDtoList = categoryService.getCategoryTree();
        model.addAttribute("categoryDtoList", categoryDtoList);

        return "products/new";
    }

    @PostMapping("/products/new")
    public String createProduct(
            ProductRegistrationRequest request,
            @AuthenticationPrincipal AuthUser authUser,
            BindingResult result,
            RedirectAttributes ra
    ) {
        if(result.hasErrors()) {
            return "products/new";
        }
        // 이미지 URL은 프론트엔드에서 S3 업로드 후 받아옴
        ProductRegistrationRequest registrationRequest = new ProductRegistrationRequest (
                request.productName(),
                request.description(),
                request.categoryId(),
                request.condition(),
                authUser.getId(),
                request.mainImageKey(),
                request.subImageKeys()
        );

        productService.registerProduct(registrationRequest);

        ra.addFlashAttribute("productName",request.productName());
        return "redirect:/productSuccess";
    }

    @GetMapping("/productSuccess")
    public String success() {
        return "products/success";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id,
                              @AuthenticationPrincipal AuthUser authUser,
                              Model model) {
        model.addAttribute("product", productService.findEditableByIdAndSeller(id, authUser.getId()));
        model.addAttribute("category", categoryService.findAllCategoryTree());
        return "products/edit";
    }

    @PostMapping("/products/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @AuthenticationPrincipal AuthUser authUser,
                                @Valid @ModelAttribute("form") ProductUpdateRequest req) {
        productService.update(id, authUser.getId(), req);
        return "redirect:/products/my";
    }

    @PostMapping("/products/presigned-url")
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

    @GetMapping("/products/image-url")
    @ResponseBody
    public Map<String, String> getImageUrl(@RequestParam("key") String key) {
        String presignedGetUrl = s3Service.generatePresignedGetUrl(key);
        return Map.of("imageUrl", presignedGetUrl);
    }

    @GetMapping("/products")
    public String productList(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size
                              )
    {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductListResponse> productPage = productService.findAllWithImages(pageable);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("productCount", productService.getProductCount());
        return "products/productList";
    }


    @GetMapping("/products/my")
    public String myProductList(Model model,
                                @AuthenticationPrincipal AuthUser authUser,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductListResponse> productPage = productService.findAllProductsBySellerId(authUser.getId(), pageable);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("productCount", productService.getProductCount());
        return "products/myProductList";
    }

    @PostMapping("/products/{id}")
    public String deleteProduct(@PathVariable Long id,
                                @AuthenticationPrincipal AuthUser authUser) {
        ProductListResponse currentProduct = productService.findEditableByIdAndSeller(id, authUser.getId());
        if (currentProduct == null) {
            throw new IllegalArgumentException("Product not found");
        }

        productService.deleteProductById(id);
        return "redirect:/products/my";
    }
}
