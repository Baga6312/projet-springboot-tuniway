package com.tuniway.service;

import com.tuniway.model.Message;
import com.tuniway.model.User;
import com.tuniway.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getMessagesBetweenUsers(Long user1Id, Long user2Id) {
        return messageRepository.findMessagesBetweenUsers(user1Id, user2Id);
    }


    public List<User> getConversationParticipants(Long userId) {
        List<User> senders = messageRepository.findSendersToUser(userId);
        List<User> receivers = messageRepository.findReceiversFromUser(userId);

        // Combine and remove duplicates
        Set<User> allParticipants = new HashSet<>(senders);
        allParticipants.addAll(receivers);

        return new ArrayList<>(allParticipants);
    }



    public Long countUnreadMessages(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    public void markMessagesAsRead(Long receiverId, Long senderId) {
        List<Message> unreadMessages = messageRepository.findUnreadMessagesFrom(receiverId, senderId);
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            messageRepository.save(message);
        }
    }

    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }
}