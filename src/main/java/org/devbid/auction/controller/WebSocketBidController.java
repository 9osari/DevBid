package org.devbid.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.application.AuctionApplicationService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketBidController {
    private final AuctionApplicationService auctionApplicationService;

    @MessageMapping("/bid/{auctionId}")
    public void handleBid(@DestinationVariable Long auctionId, @Payload Map<String, Object> payload) {
        Long bidderId = Long.valueOf(payload.get("bidderId").toString());
        BigDecimal bidAmount = new BigDecimal(payload.get("bidAmount").toString());

        auctionApplicationService.placeBid(auctionId, bidderId, bidAmount);
    }
}
