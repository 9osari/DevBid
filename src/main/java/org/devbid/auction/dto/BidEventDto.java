package org.devbid.auction.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * WebSocket으로 전솔할 입찰 이벤트 DTO
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

    public static BidEventDto from(Long auctionId,
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
                .eventType("BID_PLACED")
                .build();

    }

}
