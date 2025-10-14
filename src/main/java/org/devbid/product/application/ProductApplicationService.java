package org.devbid.product.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.product.domain.Category;
import org.devbid.product.domain.Product;
import org.devbid.product.domain.ProductFactory;
import org.devbid.product.dto.ProductRegistrationRequest;
import org.devbid.product.repository.CategoryRepository;
import org.devbid.product.repository.ProductRepository;
import org.devbid.user.domain.User;
import org.devbid.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductApplicationService implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public void registerProduct(ProductRegistrationRequest request) {
        Category category = getCategoryWithId(request);
        User seller = getSellerWithId(request);

        Product product = ProductFactory.createFromPrimitives(
                request.productName(),
                request.description(),
                category,
                request.condition(),
                seller
        );

        productRepository.save(product);
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
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findAllProductsBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    @Override
    public long getProductCount() {
        return productRepository.count();
    }
}
