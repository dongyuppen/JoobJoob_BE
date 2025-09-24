package org.example.joobjoob.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 👈 2. setAllowedOrigins("*") 설정이 올바르게 되어 있는지 다시 확인합니다.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*") // 모든 출처 허용 (개발 시) 또는 "http://127.0.0.1:5500" 와 같이 특정 출처 지정
                .withSockJS();
    }
}