package com.garv.foodApp.foodApp.Config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Do NOT include /api/v1 here, context-path is automatically prefixed
        registry.addEndpoint("/ws-order").setAllowedOriginPatterns("*"); // no SockJS
        registry.addEndpoint("/ws-order").setAllowedOriginPatterns("*").withSockJS(); // no SockJS
    }
}
