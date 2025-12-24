package com.tuniway.controller.Rest;


import com.tuniway.model.chatbot.ChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rest/chat")
@CrossOrigin(origins = "*")
public class ChatRestController {

    private List<ChatMessage> chatHistory = new ArrayList<>();

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory() {
        return ResponseEntity.ok(chatHistory);
    }

    @PostMapping("/save")
    public ResponseEntity<ChatMessage> saveMessage(@RequestBody ChatMessage message) {
        chatHistory.add(message);

        if (chatHistory.size() > 100) {
            chatHistory.remove(0);
        }

        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearHistory() {
        chatHistory.clear();
        return ResponseEntity.noContent().build();
    }
}