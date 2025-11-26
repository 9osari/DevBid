package org.devbid.auction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionEditRequest(
    long auctionId,
    BigDecimal startingPrice,
    BigDecimal buyoutPrice,
    LocalDateTime startTime,
    LocalDateTime endTime) {
}
