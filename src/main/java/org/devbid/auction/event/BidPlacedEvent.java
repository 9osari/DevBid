package org.devbid.auction.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class BidPlacedEvent {
    private final Long auctionId;       //경매ID
    private final Long bidderId;        //입찰자ID
    private final String bidderNickname;    //입찰자 닉네임
    private final BigDecimal currentPrice;  //입찰 후 업데이트된 가격
    private final int bidCount;     //입찰 횟수

    public static BidPlacedEvent of(Long auctionId,
                                    Long bidderId,
                                    String bidderNickname,
                                    BigDecimal currentPrice,
                                    int bidCount) {
        return new BidPlacedEvent(
                auctionId,
                bidderId,
                bidderNickname,
                currentPrice,
                bidCount);
    }
}
