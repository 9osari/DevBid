package org.devbid.auction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record AuctionRegistrationRequest(
        Long productId,
        BigDecimal startingPrice,
        BigDecimal buyoutPrice,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
