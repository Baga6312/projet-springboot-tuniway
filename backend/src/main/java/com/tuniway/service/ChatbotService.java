package com.tuniway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${chatbot.flask.url:http://localhost:5000}")
    private String flaskServerUrl;

    @Value("${chatbot.flask.endpoint:/chat}")
    private String chatEndpoint;

    @Value("${chatbot.flask.timeout:5000}")
    private int timeout;

    public ChatbotService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Send message to Flask chatbot and get response
     *
     * @param userMessage The message from the user
     * @return ChatbotResponse containing bot's reply
     */
    public ChatbotResponse sendMessageToBot(String userMessage) {
        logger.info("📤 Sending message to Flask chatbot: {}", userMessage);

        try {
            // Prepare request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("message", userMessage);

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // Make the request to Flask server
            String fullUrl = flaskServerUrl + chatEndpoint;
            logger.debug("🌐 Flask URL: {}", fullUrl);

            ResponseEntity<String> response = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Parse response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());

                // Extract bot response (adjust field name based on your Flask response)
                String botReply = extractBotReply(jsonResponse);

                logger.info("✅ Received response from chatbot: {}", botReply);

                return ChatbotResponse.builder()
                        .success(true)
                        .message(botReply)
                        .originalMessage(userMessage)
                        .build();
            } else {
                logger.error("❌ Unexpected response status: {}", response.getStatusCode());
                return ChatbotResponse.builder()
                        .success(false)
                        .message("Sorry, I couldn't process your message right now.")
                        .originalMessage(userMessage)
                        .error("Unexpected response status: " + response.getStatusCode())
                        .build();
            }

        } catch (ResourceAccessException e) {
            logger.error("❌ Flask server is not reachable: {}", e.getMessage());
            return ChatbotResponse.builder()
                    .success(false)
                    .message("Sorry, the chatbot service is currently unavailable. Please try again later.")
                    .originalMessage(userMessage)
                    .error("Connection timeout or server unreachable")
                    .build();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("❌ HTTP error from Flask: {} - {}", e.getStatusCode(), e.getMessage());
            return ChatbotResponse.builder()
                    .success(false)
                    .message("Sorry, there was an error processing your request.")
                    .originalMessage(userMessage)
                    .error("HTTP " + e.getStatusCode() + ": " + e.getMessage())
                    .build();

        } catch (Exception e) {
            logger.error("❌ Unexpected error: {}", e.getMessage(), e);
            return ChatbotResponse.builder()
                    .success(false)
                    .message("Sorry, an unexpected error occurred.")
                    .originalMessage(userMessage)
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * Extract bot reply from Flask JSON response
     * Adapt this method based on your Flask response format
     */
    private String extractBotReply(JsonNode jsonResponse) {
        // Try common field names
        if (jsonResponse.has("response")) {
            return jsonResponse.get("response").asText();
        } else if (jsonResponse.has("reply")) {
            return jsonResponse.get("reply").asText();
        } else if (jsonResponse.has("message")) {
            return jsonResponse.get("message").asText();
        } else if (jsonResponse.has("answer")) {
            return jsonResponse.get("answer").asText();
        } else if (jsonResponse.has("text")) {
            return jsonResponse.get("text").asText();
        }

        // If none of the above, return the whole response as string
        logger.warn("⚠️ Unknown Flask response format: {}", jsonResponse.toString());
        return jsonResponse.toString();
    }

    /**
     * Check if Flask chatbot is alive
     */
    public boolean isFlaskServerAlive() {
        try {
            String healthUrl = flaskServerUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.warn("⚠️ Flask server health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Response DTO for chatbot messages
     */
    public static class ChatbotResponse {
        private boolean success;
        private String message;
        private String originalMessage;
        private String error;
        private long timestamp;

        public ChatbotResponse() {
            this.timestamp = System.currentTimeMillis();
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getOriginalMessage() { return originalMessage; }
        public void setOriginalMessage(String originalMessage) { this.originalMessage = originalMessage; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        // Builder Pattern
        public static class Builder {
            private final ChatbotResponse response = new ChatbotResponse();

            public Builder success(boolean success) {
                response.success = success;
                return this;
            }

            public Builder message(String message) {
                response.message = message;
                return this;
            }

            public Builder originalMessage(String originalMessage) {
                response.originalMessage = originalMessage;
                return this;
            }

            public Builder error(String error) {
                response.error = error;
                return this;
            }

            public ChatbotResponse build() {
                return response;
            }
        }
    }
}