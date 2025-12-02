package org.devbid.user.dto;


import org.devbid.auction.domain.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RecentAuctionDto (
        Long id,
        AuctionStatus status,
        BigDecimal currentPrice,
        int bidCount,
        LocalDateTime endTime,
        LocalDateTime startTime,

        String productName,
        String mainImageUrl,  // 메인 이미지 URL
        List<String> subImageUrls  // 서브 이미지 URL 리스트
){}
