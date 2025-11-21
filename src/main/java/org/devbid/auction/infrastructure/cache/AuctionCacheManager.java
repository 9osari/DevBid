package org.devbid.auction.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.BidEventDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionCacheManager {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public AuctionListResponse getDetailRedis(Long auctionId) {
        String redisKey = "auction:" + auctionId + ":detail";
        Object cached = redisTemplate.opsForValue().get(redisKey);

        if(cached != null) {
            log.info("Redis 캐시! auctionId: {}, /: {}", auctionId, cached);
            try {
                return objectMapper.convertValue(cached, AuctionListResponse.class);
            } catch (IllegalArgumentException e) {
                log.error("Redis 캐시 변환실패 - auctionId: {}", auctionId, e);
            }
        }
        log.info("Redis 미스! auctionId: {}, /: {}", auctionId, cached);
        return null;
    }

    public void setDetailRedis(Long auctionId, AuctionListResponse response) {
        String redisKey = "auction:" + auctionId + ":detail";
        redisTemplate.opsForValue().set(redisKey, response, 5, TimeUnit.MINUTES);
        log.info("Redis 경매 상세 저장 - auctionId: {}", auctionId);
    }

    public void deleteDetailRedis(Long auctionId) {
        String redisKey = "auction:" + auctionId + ":detail";
        redisTemplate.delete(redisKey);
        log.info("Redis 경매 상세 캐시 삭제 - auctionId: {}", auctionId);
    }

    public void setLatestBid(Long auctionId, BidEventDto bidEvent) {
        //Redis에 입찰, 즉시구매 정보 저장
        String redisKey = "auction:" + auctionId + ":latestBid";
        redisTemplate.opsForValue().set(redisKey, bidEvent, 1, TimeUnit.HOURS); //1시간 후 삭제
        log.info("Redis에 입찰정보 저장 - key: {}, value: {}", redisKey, bidEvent);
    }
}
