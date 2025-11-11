package org.devbid.auction.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class BidPlacedEvent {
    private final Long auctionId;       //경매ID
    private final Long bidderId;        //입찰자ID
    private final BigDecimal bidAmount; //입찰금액

    public static BidPlacedEvent of(Long auctionId, Long bidderId, BigDecimal bidAmount) {
        return new BidPlacedEvent(auctionId, bidderId, bidAmount);
    }
}
