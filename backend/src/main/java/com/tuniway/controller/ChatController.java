package com.tuniway.controller;

import com.tuniway.model.chatbot.ChatMessage;
import com.tuniway.service.ChatbotService;
import com.tuniway.service.ChatbotService.ChatbotResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatbotService chatbotService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        System.out.println("📨 Message received from " + chatMessage.getSender() + ": " + chatMessage.getContent());

        // If message is from a user (not from bot), send to Flask chatbot
        if (chatMessage.getSender() != null &&
                !chatMessage.getSender().equals("TuniWay_Bot") &&
                chatMessage.getContent() != null &&
                !chatMessage.getContent().trim().isEmpty()) {

            // Send to Flask in a separate thread to avoid blocking
            processChatbotResponse(chatMessage);
        }

        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setTimestamp(LocalDateTime.now());
        System.out.println("✅ User joined: " + chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.sendPrivate")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(),
                "/queue/private",
                chatMessage
        );

        System.out.println("🔒 Private message from " + chatMessage.getSender() +
                " to " + chatMessage.getReceiver());
    }

    @MessageMapping("/chat.typing")
    @SendTo("/topic/typing")
    public ChatMessage userTyping(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.TYPING);
        return chatMessage;
    }

    /**
     * Process chatbot response asynchronously
     */
    private void processChatbotResponse(ChatMessage userMessage) {
        new Thread(() -> {
            try {
                System.out.println("🤖 Forwarding to Flask chatbot: " + userMessage.getContent());

                // Send message to Flask chatbot
                ChatbotResponse botResponse = chatbotService.sendMessageToBot(userMessage.getContent());

                if (botResponse.isSuccess()) {
                    // Create bot response message
                    ChatMessage botMessage = new ChatMessage();
                    botMessage.setType(ChatMessage.MessageType.CHAT);
                    botMessage.setSender("TuniWay_Bot");
                    botMessage.setContent(botResponse.getMessage());
                    botMessage.setTimestamp(LocalDateTime.now());

                    // Send bot response to all clients
                    messagingTemplate.convertAndSend("/topic/public", botMessage);

                    System.out.println("✅ Bot response sent: " + botResponse.getMessage());
                } else {
                    // Send error message if Flask is down
                    ChatMessage errorMessage = new ChatMessage();
                    errorMessage.setType(ChatMessage.MessageType.CHAT);
                    errorMessage.setSender("TuniWay_Bot");
                    errorMessage.setContent("Sorry, I'm having trouble connecting right now. Please try again in a moment.");
                    errorMessage.setTimestamp(LocalDateTime.now());

                    messagingTemplate.convertAndSend("/topic/public", errorMessage);

                    System.err.println("❌ Chatbot error: " + botResponse.getError());
                }

            } catch (Exception e) {
                System.err.println("❌ Error processing chatbot response: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}