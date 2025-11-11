package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.dto.BidEventDto;
import org.devbid.auction.event.BidPlacedEvent;
import org.devbid.user.domain.User;
import org.devbid.user.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionWebSocketEventListener {
    private final AuctionService auctionService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleBidPlacedEvent(BidPlacedEvent event ) {
        log.info("입찰 이벤트 수신: {}", event);

        //DTO 생성
        Auction auction = auctionService.findById(event.getAuctionId());
        User bidder = userRepository.findById(event.getBidderId()).orElseThrow();

        BidEventDto dto = BidEventDto.from(
                event.getAuctionId(),
                auction.getCurrentPrice().getValue(),
                bidder.getNickname().getValue(),
                event.getBidderId(),
                auction.getBidCount()
        );

        //WebSocket 전송
        messagingTemplate.convertAndSend("/topic/auction/" + event.getAuctionId(), dto);
    }
}
