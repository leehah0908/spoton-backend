package com.spoton.spotonbackend.common.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker // STOMP 활성화
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                // Endpoint를 지정 -> /chat
                .addEndpoint("/chat")
                // CORS 설정
                .setAllowedOrigins("*");
    }

    // 메모리 기반의 Simple Message Broker를 활성화
    // /sub으로 시작하는 주소의 Subscriber들에게 메시지 전달
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 메시지를 받을 때, 경로 설정
        // 내장 브로커 사용
        // /sub가 api에 prefix로 붙은 경우, messageBroker가 해당 경로를 가로채 처리
        // SimpleBroker는 해당하는 경로로 구독하는 client에게 메시지를 전달
        registry.enableSimpleBroker("/sub");

        // 메시지를 보낼 때, 관련 경로를 설정
        // client에서 SEND 요청을 처리
        // 클라이언트가 메시지를 보낼 때, 경로 앞에 /pub 붙어있으면 Broker로 보내짐
        registry.setApplicationDestinationPrefixes("/pub");
    }
}
