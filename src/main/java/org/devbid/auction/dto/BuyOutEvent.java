package org.devbid.auction.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class BuyOutEvent {
    private final Long auctionId;           //경매ID
    private final Long buyerId;             //구매자ID
    private final String buyerNickname;     //구매자 닉네임
    private final BigDecimal buyoutPrice;   //즉시구매가
    private final LocalDateTime endTime;    //종료시간

    public static BuyOutEvent of(Long auctionId,
                                 Long buyerId,
                                 String buyerNickname,
                                 BigDecimal buyoutPrice,
                                 LocalDateTime endTime) {
        return new BuyOutEvent(
                auctionId,
                buyerId,
                buyerNickname,
                buyoutPrice,
                endTime);
    }
}
