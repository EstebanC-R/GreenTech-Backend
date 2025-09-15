package com.api.cruds.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Map;

@Controller
public class WebSocketController {

    @MessageMapping("/keep-alive")
    public void handleKeepAlive(@Payload Map<String, Object> payload, Principal principal) {
        System.out.println("Keep-alive recibido de: " + (principal != null ? principal.getName() : "unknown"));
        System.out.println("Payload: " + payload);
        // No necesitas responder, solo procesar para mantener viva la conexión
    }

    // Opcional: Endpoint para enviar confirmación de keep-alive
    @MessageMapping("/ping")
    @SendToUser("/queue/pong")
    public Map<String, Object> handlePing(@Payload Map<String, Object> payload, Principal principal) {
        System.out.println("Ping recibido de: " + (principal != null ? principal.getName() : "unknown"));
        return Map.of(
                "type", "pong",
                "timestamp", System.currentTimeMillis(),
                "received", payload
        );
    }
}