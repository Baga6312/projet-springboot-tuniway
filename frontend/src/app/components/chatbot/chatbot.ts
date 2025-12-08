import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ChatbotService } from '../../services/chatbot.service';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  providers: [ChatbotService],
  templateUrl: './chatbot.html',
  styleUrls: ['./chatbot.css']
})
export class ChatbotComponent implements OnInit, OnDestroy {
  isOpen = false;
  messages: { text: string; isUser: boolean; timestamp: Date }[] = [];
  newMessage = '';
  isLoading = false;
  isConnected = false;
  errorMessage = '';

  constructor(private chatbotService: ChatbotService) {}

  ngOnInit() {
    // Don't check health on init, check when chat is opened
  }

  ngOnDestroy() {
    // Cleanup if needed
  }

  checkChatbotHealth() {
    this.chatbotService.healthCheck().subscribe({
      next: (response) => {
        this.isConnected = true;
        console.log('✅ Chatbot service is healthy:', response);
        
        // Add welcome message after successful connection
        if (this.messages.length === 0) {
          this.messages.push({
            text: 'Hello! I\'m your Tuniway travel assistant. How can I help you today?',
            isUser: false,
            timestamp: new Date()
          });
        }
      },
      error: (error) => {
        this.isConnected = false;
        console.error('❌ Chatbot service is not available:', error);
        this.errorMessage = 'Could not connect to chatbot service';
        
        // Show error message in chat
        this.messages.push({
          text: 'Sorry, I\'m unable to connect right now. Please check if the chatbot service is running.',
          isUser: false,
          timestamp: new Date()
        });
      }
    });
  }

  toggleChat() {
    console.log('Toggle clicked, isOpen:', this.isOpen);
    this.isOpen = !this.isOpen;
    
    // Check health when opening chat
    if (this.isOpen) {
      this.checkChatbotHealth();
    }
  }

  sendMessage() {
    if (this.newMessage.trim()) {
      const userMessage = this.newMessage.trim();
      
      // Add user message to UI immediately
      this.messages.push({
        text: userMessage,
        isUser: true,
        timestamp: new Date()
      });
      
      this.newMessage = '';
      this.isLoading = true;
      this.errorMessage = '';
      
      // Send to chatbot service
      this.chatbotService.sendMessage(userMessage).subscribe({
        next: (response) => {
          // Add bot response to UI
          this.messages.push({
            text: response.response || 'I received your message!',
            isUser: false,
            timestamp: new Date()
          });
          this.isLoading = false;
          
          // Scroll to bottom
          setTimeout(() => this.scrollToBottom(), 100);
        },
        error: (error) => {
          console.error('Error sending message to chatbot:', error);
          this.errorMessage = 'Sorry, I couldn\'t connect to the chatbot service. Please try again.';
          this.messages.push({
            text: 'Sorry, I\'m having trouble connecting right now. Please try again later.',
            isUser: false,
            timestamp: new Date()
          });
          this.isLoading = false;
        }
      });
      
      // Scroll to bottom after adding user message
      setTimeout(() => this.scrollToBottom(), 100);
    }
  }

  private scrollToBottom() {
    const chatMessages = document.querySelector('.chat-messages');
    if (chatMessages) {
      chatMessages.scrollTop = chatMessages.scrollHeight;
    }
  }
}
