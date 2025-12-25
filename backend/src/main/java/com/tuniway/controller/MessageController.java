package com.tuniway.controller;

import com.tuniway.model.Message;
import com.tuniway.model.User;
import com.tuniway.service.MessageService;
import com.tuniway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    // Send a new message
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest request) {
        Optional<User> sender = userService.getUserById(request.getSenderId());
        Optional<User> receiver = userService.getUserById(request.getReceiverId());

        if (!sender.isPresent()) {
            return ResponseEntity.badRequest().body("Sender not found");
        }

        if (!receiver.isPresent()) {
            return ResponseEntity.badRequest().body("Receiver not found");
        }

        Message message = new Message();
        message.setSender(sender.get());
        message.setReceiver(receiver.get());
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());
        message.setIsRead(false);

        Message savedMessage = messageService.sendMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
    }

    // Get conversation between two users
    @GetMapping("/conversation/{user1Id}/{user2Id}")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long user1Id,
            @PathVariable Long user2Id) {

        List<Message> messages = messageService.getMessagesBetweenUsers(user1Id, user2Id);

        // Mark messages as read for the current user
        messageService.markMessagesAsRead(user1Id, user2Id);

        return ResponseEntity.ok(messages);
    }

    // Get all conversation participants for a user
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<User>> getConversations(@PathVariable Long userId) {
        List<User> participants = messageService.getConversationParticipants(userId);
        return ResponseEntity.ok(participants);
    }

    // Get unread message count
    @GetMapping("/unread/{userId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        Long count = messageService.countUnreadMessages(userId);
        return ResponseEntity.ok(count);
    }

    // Mark messages as read
    @PutMapping("/mark-read")
    public ResponseEntity<Void> markAsRead(@RequestBody MarkReadRequest request) {
        messageService.markMessagesAsRead(request.getReceiverId(), request.getSenderId());
        return ResponseEntity.ok().build();
    }

    // Inner class for message request
    static class MessageRequest {
        private Long senderId;
        private Long receiverId;
        private String content;

        public Long getSenderId() {
            return senderId;
        }

        public void setSenderId(Long senderId) {
            this.senderId = senderId;
        }

        public Long getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(Long receiverId) {
            this.receiverId = receiverId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    // Inner class for mark read request
    static class MarkReadRequest {
        private Long receiverId;
        private Long senderId;

        public Long getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(Long receiverId) {
            this.receiverId = receiverId;
        }

        public Long getSenderId() {
            return senderId;
        }

        public void setSenderId(Long senderId) {
            this.senderId = senderId;
        }
    }
}