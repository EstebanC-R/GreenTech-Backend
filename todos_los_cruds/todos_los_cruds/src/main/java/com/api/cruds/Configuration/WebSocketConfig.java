package com.api.cruds.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configurar el broker simple para temas
        config.enableSimpleBroker("/topic", "/queue");
        // Prefijo para mensajes desde el cliente
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Más flexible que allowedOrigins
                .withSockJS()
                .setHeartbeatTime(25000) // Evitar desconexiones por timeout
                .setDisconnectDelay(5000)
                .setStreamBytesLimit(128 * 1024)
                .setHttpMessageCacheSize(1000)
                .setWebSocketEnabled(true);

        // Endpoint adicional sin SockJS para conexiones WebSocket nativas
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}