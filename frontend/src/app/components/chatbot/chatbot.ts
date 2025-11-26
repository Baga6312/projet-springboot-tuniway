import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WebsocketService, ChatMessage } from '../../services/websocket';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [WebsocketService],
  templateUrl: './chatbot.html',
  styleUrls: ['./chatbot.css']
})
export class ChatbotComponent implements OnInit, OnDestroy {
  isOpen = false;
  messages: { text: string; isUser: boolean; timestamp: Date; sender: string }[] = [];
  newMessage = '';
  username = 'User_' + Math.floor(Math.random() * 1000);
  isConnected = false;

  private messageSubscription?: Subscription;
  private connectionSubscription?: Subscription;

  constructor(@Inject(WebsocketService) private websocketService: WebsocketService) {}


  ngOnInit() {
  // Subscribe to incoming messages
  this.messageSubscription = this.websocketService.getMessages().subscribe(
    (message: ChatMessage | null) => {
      if (message && message.type === 'CHAT') {
        // Only show if not from current user (avoid duplicates)
        if (message.sender !== this.username) {
          this.messages.push({
            text: message.content,
            isUser: false,
            timestamp: new Date(),
            sender: message.sender
          });
        }
      }
      // Remove JOIN/LEAVE messages - we don't need them in UI
    }
  );

  // Subscribe to connection status
  this.connectionSubscription = this.websocketService.getConnectionStatus().subscribe(
    (status: boolean) => {
      this.isConnected = status;
      console.log('Connection status:', status);
    }
  );
}


   ngOnDestroy() {
    this.messageSubscription?.unsubscribe();
    this.connectionSubscription?.unsubscribe();
    this.websocketService.disconnect();
  }

toggleChat() {
  console.log('Toggle clicked, isOpen:', this.isOpen);
  this.isOpen = !this.isOpen;
  
  // Connect when opening chat
  if (this.isOpen) {
    console.log('Attempting to connect...');
    this.websocketService.connect(this.username);
  }
}


sendMessage() {
  if (this.newMessage.trim() && this.isConnected) {
    // Add message to UI immediately
    this.messages.push({
      text: this.newMessage,
      isUser: true,
      timestamp: new Date(),
      sender: this.username
    });
    
    // Send to WebSocket
    this.websocketService.sendMessage(this.newMessage, this.username);
    this.newMessage = '';
  }
}

}