package org.devbid.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketTestController {
    @GetMapping("ws-test")
    public String wsTestPage() {
        return "ws-test";
    }

    //웹소켓 메세지 핸들러 (테스트)
    @MessageMapping("/test/{message}")  // WebSocketConfig의 "/app" prefix 사용
    @SendTo("/topic/test")  //리턴 값을 특정 경로 구독자들에게 자동 전송 해 모든 /topic/test 구독자가 메시지 받음 (SimpleBroker)
    public String handleTestMessage(@DestinationVariable String message ) {
        log.info("웹소켓 메세지 수신: {}", message);
        return "서버 응답: " + message + " (시각; " + LocalDateTime.now() + ")";
    }


}
