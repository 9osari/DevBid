package org.devbid.product.dto;

import org.devbid.product.domain.Product;
import org.devbid.product.domain.ProductCondition;
import org.devbid.product.domain.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProductListResponse(
        Long id,
        String productName,
        String description,
        ProductCondition condition,
        ProductStatus saleStatus,
        Long categoryId,
        String categoryName,
        String sellerName,
        String mainImageUrl,  // 메인 이미지 URL
        List<String> subImageUrls,  // 서브 이미지 URL 리스트
        LocalDateTime createdAt
) {
    public static ProductListResponse of(Product product, String mainImageUrl, List<String> subImageUrls) {
        return new ProductListResponse(
                product.getId(),
                product.getProductName().getValue(),
                product.getDescription().getValue(),
                product.getCondition(),
                product.getSaleStatus(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getSeller().getUsername().getValue(),
                mainImageUrl,
                subImageUrls,
                product.getCreatedAt()
        );
    }
}