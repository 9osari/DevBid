package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.dto.BidEventDto;
import org.devbid.auction.dto.BidPlacedEvent;
import org.devbid.auction.dto.BuyOutEvent;
import org.devbid.auction.infrastructure.cache.AuctionCacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionEventHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuctionCacheManager auctionCacheManager;

    @EventListener
    public void handleBidPlacedEvent(BidPlacedEvent event ) {
        log.info("입찰 이벤트 수신: {}", event);
        //DTO 생성
        BidEventDto dto = BidEventDto.fromBid(
                event.getAuctionId(),
                event.getCurrentPrice(),
                event.getBidderNickname(),
                event.getBidderId(),
                event.getBidCount()
        );
        // Redis에 최신 정보 저장
        auctionCacheManager.setLatestBid(event.getAuctionId(), dto);
        try {
            redisTemplate.convertAndSend("auction.events", dto); // Pub/Sub 전송
            log.info("Redis 발행 완료: {}", event.getAuctionId());
        } catch (Exception e) {
            log.error("Redis 발행 실패", e);
        }
    }

    @EventListener
    public void handleBuyOutEvent(BuyOutEvent event) {
        log.info("즉시구매 이벤트 수신: {}", event);
        BidEventDto dto = BidEventDto.fromBuyOut(
                event.getAuctionId(),
                event.getBuyoutPrice(),
                event.getBuyerNickname(),
                event.getBuyerId(),
                event.getEndTime()
        );
        // Redis에 최신 정보 저장
        auctionCacheManager.setLatestBid(event.getAuctionId(), dto);
        try {
            redisTemplate.convertAndSend("auction.events", dto);
            log.info("Redis 발행 완료: {}", event.getAuctionId());
        } catch (Exception e) {
            log.error("Redis 발행 실패", e);
        }
    }
}
