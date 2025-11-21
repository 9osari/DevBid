package org.devbid.auction.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.dto.BidEventDto;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionRedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            //Redis에서 받은 메세지 파싱
            String body = new String(message.getBody());
            log.info("=== Redis 메세지 수신 ===");
            log.info("원본 메세지: {}", body);

            log.info("JSON 파싱 시도...");
            BidEventDto dto = objectMapper.readValue(body, BidEventDto.class);
            log.info("파싱 성공: {}", dto);

            //WebSocket으로 전송
            log.info("WebSocket 전송 시도...");
            messagingTemplate.convertAndSend("/topic/auctions/" + dto.getAuctionId(), dto);
            log.info("WebSocket 전송 완료: auctionId: {}", dto.getAuctionId());
        } catch (Exception e) {
            log.error("Redis 메세지 처리 실패", e);
        }
    }
}
