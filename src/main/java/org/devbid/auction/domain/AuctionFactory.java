package org.devbid.auction.domain;

import org.devbid.product.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionFactory {
    public static Auction createFromPrimitives(
            Product product,
            BigDecimal startingPrice,
            BigDecimal buyoutPrice,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return Auction.of(
                product,
                new StartingPrice(startingPrice),
                new BuyoutPrice(buyoutPrice),
                new CurrentPrice(startingPrice),    //시작가로 초기화.
                startTime,
                endTime,
                AuctionStatus.BEFORE_START
        );
    }
}
