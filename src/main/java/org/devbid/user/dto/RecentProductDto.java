package org.devbid.user.dto;

import org.devbid.product.domain.ProductCondition;
import org.devbid.product.domain.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public record RecentProductDto(
        Long id,
        String mainImageUrl,  // 메인 이미지 URL
        List<String> subImageUrls,  // 서브 이미지 URL 리스트
        String productName,
        ProductStatus status,
        ProductCondition condition,
        String categoryName,
        String saleStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int activeAuctionCount
) {
}
