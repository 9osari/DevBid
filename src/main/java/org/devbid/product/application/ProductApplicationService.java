package org.devbid.product.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.product.application.awsService.S3Service;
import org.devbid.product.domain.Category;
import org.devbid.product.domain.Product;
import org.devbid.product.domain.ProductFactory;
import org.devbid.product.domain.ProductImage;
import org.devbid.product.dto.ProductListResponse;
import org.devbid.product.dto.ProductRegistrationRequest;
import org.devbid.product.dto.ProductUpdateRequest;
import org.devbid.product.repository.CategoryRepository;
import org.devbid.product.repository.ProductImageRepository;
import org.devbid.product.repository.ProductRepository;
import org.devbid.user.domain.User;
import org.devbid.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductApplicationService implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Override
    public void registerProduct(ProductRegistrationRequest request) {
        Category category = getCategoryWithId(request);
        User seller = getSellerWithId(request);

        Product product = ProductFactory.createFromPrimitives(
                request.productName(),
                request.description(),
                request.price(),
                category,
                request.condition(),
                seller
        );

        productRepository.save(product);

        mainImageValidationAndSave(request, product);
        subImagesValidationAndSave(request, product);
    }

    @Override
    public void update(Long productId, Long sellerId, ProductUpdateRequest req) {
        Product product = productRepository.findByIdAndSellerId(productId, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if(req.categoryId() != null) {
            Category category = categoryRepository.findById(req.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            product.updateCategory(category);
        }

        product.updateProductInfo(
                req.productName(),
                req.description(),
                req.price(),
                req.condition());
        product.updateMainImage(req.keepMainImageId(), req.mainImageKey());
        product.updateSubImages(req.keepSubImageIds(), req.subImageKeys());
    }

    private void mainImageValidationAndSave(ProductRegistrationRequest request, Product product) {
        if(request.mainImageKey() != null && !request.mainImageKey().isEmpty()) {
            saveProductImage(product, request.mainImageKey(), 1);
        }
    }

    private void subImagesValidationAndSave(ProductRegistrationRequest request, Product product) {
        if(request.subImageKeys() != null && !request.subImageKeys().isEmpty()) {
            int sortOrd = 2;
            for (String key : request.subImageKeys()) {
                saveProductImage(product, key, sortOrd++);
            }
        }
    }

    private void saveProductImage(Product product, String imageKey, int sortOrder) {
        ProductImage productImage = ProductImage.create(product, imageKey, sortOrder);
        productImageRepository.save(productImage);
    }

    private User getSellerWithId(ProductRegistrationRequest request) {
        return userRepository.findById(request.sellerId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다." + request.sellerId()));
    }

    private Category getCategoryWithId(ProductRegistrationRequest request) {
        return categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다." + request.categoryId()));
        // orElse(null): 값이 없으면 null 반환 → 나중에 문제 발생 가능
        // orElseThrow(): 값이 없으면 즉시 예외 발생 → 문제를 바로 발견하고 처리
    }

    @Override
    public List<ProductListResponse> findAllProductsBySellerId(Long sellerId) {
        List<Product> products = productRepository.findBySellerId(sellerId);
        return products.stream()
                .map(this::convertToProductListResponse)
                .toList();
    }

    @Override
    public ProductListResponse findEditableByIdAndSeller(Long id, Long sellerId) {
        Product product = productRepository.findEditableByIdAndSeller(id, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return convertToProductListResponse(product);
    }

    @Override
    public List<ProductListResponse> findAllWithImages() {
        List<Product> products = productRepository.findAllWithImages();
        return products.stream()
                .map(this::convertToProductListResponse)
                .toList();
    }

    private ProductListResponse convertToProductListResponse(Product product) {
        String mainImageUrl = product.getImages().stream()
                .filter(img -> img.getSortOrder() == 1)
                .findFirst()
                .map(img -> s3Service.generatePresignedGetUrl(img.getImageKey()))
                .orElse(null);

        List<String> subImageUrls = product.getImages().stream()
                .filter(img -> img.getSortOrder() > 1)
                .sorted(Comparator.comparing(ProductImage::getSortOrder))
                .map(img -> s3Service.generatePresignedGetUrl(img.getImageKey()))
                .toList();
        return ProductListResponse.of(product, mainImageUrl, subImageUrls);
    }

    @Override
    public long getProductCount() {
        return productRepository.count();
    }
}
