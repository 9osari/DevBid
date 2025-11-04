package org.devbid.auction.dto;

import org.devbid.auction.domain.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AuctionListResponse(
        Long id,
        String productName,
        String sellerName,
        BigDecimal startingPrice,
        BigDecimal currentPrice,
        BigDecimal buyoutPrice,
        Integer bidCount,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AuctionStatus status,
        String description,
        String mainImageUrl,
        List<String> subImageUrls
) {
    public static AuctionListResponse of
            (Long id,
             String productName,
             String sellerName,
             BigDecimal startingPrice,
             BigDecimal currentPrice,
             BigDecimal buyoutPrice,
             Integer bidCount,
             LocalDateTime startTime,
             LocalDateTime endTime,
             AuctionStatus status,
             String description,
             String mainImageUrl,
             List<String> subImageUrls
            )
    {
        return new AuctionListResponse(
                id,
                productName,
                sellerName,
                startingPrice,
                currentPrice,
                buyoutPrice,
                bidCount,
                startTime,
                endTime,
                status,
                description,
                mainImageUrl,
                subImageUrls
        );
    }
}
