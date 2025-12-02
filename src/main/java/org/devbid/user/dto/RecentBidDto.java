package org.devbid.user.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RecentBidDto(
        Long id,
        String mainImageUrl,  // 메인 이미지 URL
        List<String> subImageUrls,  // 서브 이미지 URL 리스트
        String productName,
        BigDecimal bidAmount,
        BigDecimal currentPrice,
        LocalDateTime bidTime
) {
}
