package com.tuniway.controller;

import com.tuniway.service.ChatbotService;
import com.tuniway.service.ChatbotService.ChatbotResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * Send message to Flask chatbot and get response
     *
     * POST /api/chatbot/chat
     * Body: { "message": "your message here" }
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatbotResponse> chat(@RequestBody ChatRequest request) {
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            ChatbotResponse errorResponse = ChatbotResponse.builder()
                    .success(false)
                    .message("Message cannot be empty")
                    .error("Invalid request")
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }

        ChatbotResponse response = chatbotService.sendMessageToBot(request.getMessage());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * Health check endpoint for chatbot service
     *
     * GET /api/chatbot/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> checkHealth() {
        boolean isAlive = chatbotService.isFlaskServerAlive();

        return ResponseEntity.ok(Map.of(
                "status", isAlive ? "UP" : "DOWN",
                "service", "Flask Chatbot",
                "available", isAlive
        ));
    }

    /**
     * Test endpoint to verify Spring Boot side is working
     *
     * GET /api/chatbot/test
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Spring Boot chatbot controller is working!",
                "timestamp", System.currentTimeMillis()
        ));
    }

    // Request DTO
    public static class ChatRequest {
        private String message;

        public ChatRequest() {}

        public ChatRequest(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}