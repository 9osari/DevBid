package org.devbid.auction.dto;

import lombok.Builder;
import lombok.Getter;
import org.devbid.auction.domain.BidType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * WebSocket으로 전송할 입찰 이벤트 DTO
 * 입찰이 발생하는 경우 모든 클라이언트에게 전송할 데이터들
 */
@Getter
@Builder
public class BidEventDto {
    private Long auctionId;             //경매 ID
    private BigDecimal currentPrice;    //현재가
    private String bidderName;          //입찰자
    private String bidderId;            //입찰자ID
    private Integer bidCount;           //입찰 횟수
    private LocalDateTime timestamp;    //입찰 시각
    private String eventType;           //메세지
    private LocalDateTime endTime;

    public static BidEventDto fromBid(Long auctionId,
                                      BigDecimal currentPrice,
                                      String bidderName,
                                      Long bidderId,
                                      Integer bidCount) {
        return BidEventDto.builder()
                .auctionId(auctionId)
                .currentPrice(currentPrice)
                .bidderName(bidderName)
                .bidderId(bidderId.toString())
                .bidCount(bidCount)
                .timestamp(LocalDateTime.now())
                .eventType(BidType.NORMAL.getEventType())
                .build();

    }

    /**
     * WebSocket으로 전송할 입찰 이벤트 DTO
     * 즉시구매가 발생하는 경우 모든 클라이언트에게 전송할 데이터들
     */
    public static BidEventDto fromBuyOut(Long auctionId,
                                         BigDecimal buyoutPrice,
                                         String buyerNickname,
                                         Long buyerId,
                                         LocalDateTime endTime) {
        return BidEventDto.builder()
                .auctionId(auctionId)
                .currentPrice(buyoutPrice)
                .bidderName(buyerNickname)
                .bidderId(buyerId.toString())
                .bidCount(null)
                .timestamp(LocalDateTime.now())
                .eventType(BidType.BUYOUT.getEventType())
                .endTime(endTime)
                .build();
    }

}
