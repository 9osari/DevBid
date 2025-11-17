package org.devbid.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker   //WebSocket + STOMP 프로토콜 사용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")         // ws://localhost:3000/ws HTTP 업그레이드 요청
                .setAllowedOriginPatterns("*")      //CORS 허용
                .withSockJS();                      //호환성을 위해
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //메세지 브로커 설정
        //백그라운드에서 SimpleBroker를 생성하고:
        //@SendTo("/topic/...") ← 이게 작동하도록 지원
        //SimpMessagingTemplate ← 빈이 생성되어 주입 가능하게 함
        config.enableSimpleBroker("/topic", "/queue");  //SimpleBroker는 인메모리로 구독자 관리
        //topic: 1:N 브로드캐스트 (모든 구독자에게)
        //queue: 1:1 개인 메세지

        config.setApplicationDestinationPrefixes("/app");
        //클라이언트가 서버로 메세지 보낼 떄 앞에 /app 붙음
    }
}
