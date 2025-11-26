import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

declare const SockJS: any;
declare const Stomp: any;

export interface ChatMessage {
  type: 'CHAT' | 'JOIN' | 'LEAVE' | 'TYPING';
  content: string;
  sender: string;
  receiver?: string;
  timestamp?: string;
}

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private stompClient: any = null;
  private messageSubject = new BehaviorSubject<ChatMessage | null>(null);
  private connectionStatus = new BehaviorSubject<boolean>(false);
  
  private readonly SOCKET_URL = 'http://192.168.11.86:8080/ws-chat';

  connect(username: string): void {
    const socket = new SockJS(this.SOCKET_URL);
    this.stompClient = Stomp.over(socket);
    
    this.stompClient.connect({}, 
      () => {
        console.log('✅ Connected to WebSocket');
        this.connectionStatus.next(true);
        
        this.stompClient.subscribe('/topic/public', (message: any) => {
          const chatMessage = JSON.parse(message.body) as ChatMessage;
          console.log('📩 Message received:', chatMessage);
          this.messageSubject.next(chatMessage);
        });

        this.sendJoinMessage(username);
      },
      (error: any) => {
        console.error('❌ Connection error:', error);
        this.connectionStatus.next(false);
      }
    );
  }

  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.connectionStatus.next(false);
    }
  }

  sendMessage(content: string, sender: string): void {
    if (this.stompClient && this.stompClient.connected) {
      const chatMessage: ChatMessage = {
        type: 'CHAT',
        content: content,
        sender: sender
      };
      
      this.stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
    }
  }

  private sendJoinMessage(username: string): void {
    if (this.stompClient && this.stompClient.connected) {
      const joinMessage: ChatMessage = {
        type: 'JOIN',
        content: '',
        sender: username
      };
      
      this.stompClient.send('/app/chat.addUser', {}, JSON.stringify(joinMessage));
    }
  }

  getMessages(): Observable<ChatMessage | null> {
    return this.messageSubject.asObservable();
  }

  getConnectionStatus(): Observable<boolean> {
    return this.connectionStatus.asObservable();
  }
}