package org.devbid.home.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record HotAuctionDto(
        Long id,
        String productName,
        String mainImageUrl,  // 메인 이미지 URL
        List<String> subImageUrls,  // 서브 이미지 URL 리스트
        BigDecimal currentPrice,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int bidCount,
        BigDecimal buyoutPrice

) {
}
