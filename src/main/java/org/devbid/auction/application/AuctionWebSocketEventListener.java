package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.dto.BidEventDto;
import org.devbid.auction.event.BidPlacedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionWebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleBidPlacedEvent(BidPlacedEvent event ) {
        log.info("입찰 이벤트 수신: {}", event);
        //DTO 생성
        BidEventDto dto = BidEventDto.from(
                event.getAuctionId(),
                event.getCurrentPrice(),
                event.getBidderNickname(),
                event.getBidderId(),
                event.getBidCount()
        );

        //WebSocket 전송
        messagingTemplate.convertAndSend("/topic/auctions/" + event.getAuctionId(), dto);
        log.info("WebSocket 전송 이벤트 종료..");
    }
}
