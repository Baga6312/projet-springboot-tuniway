package com.tuniway.repository;

import com.tuniway.model.Message;
import com.tuniway.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Get all messages between two users
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
            "(m.sender.id = :user2Id AND m.receiver.id = :user1Id) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findMessagesBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    // Get all conversations for a user (distinct users they've chatted with)
    @Query("SELECT DISTINCT CASE " +
            "WHEN m.sender.id = :userId THEN m.receiver " +
            "ELSE m.sender END " +
            "FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<User> findConversationParticipants(@Param("userId") Long userId);

    // Count unread messages for a user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    Long countUnreadMessages(@Param("userId") Long userId);

    // Get unread messages from a specific sender
    @Query("SELECT m FROM Message m WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = false")
    List<Message> findUnreadMessagesFrom(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
}