import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, interval } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';

export interface ChatMessage {
  type: 'CHAT' | 'JOIN' | 'LEAVE' | 'TYPING';
  content: string;
  sender: string;
  receiver?: string;
  timestamp?: string;
}

export interface ChatResponse {
  response: string;
  confidence?: number;
  intent?: string;
  timestamp?: string;
}

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private messageSubject = new BehaviorSubject<ChatMessage | null>(null);
  private connectionStatus = new BehaviorSubject<boolean>(false);
  
  private readonly API_URL = 'http://localhost:5000/chat';
  private readonly HEALTH_URL = 'http://localhost:5000/health';
  
  private currentUser: string = '';
  private pollingInterval: any = null;

  constructor(private http: HttpClient) {
    this.checkConnection();
  }

  /**
   * Initialize connection with a username
   */
  connect(username: string): void {
    this.currentUser = username;
    console.log(`🔌 Connecting user: ${username}`);
    
    // Check if service is available
    this.checkConnection();
    
    // Send join message
    this.sendJoinMessage(username);
  }

  /**
   * Disconnect and cleanup
   */
  disconnect(): void {
    console.log('🔌 Disconnecting...');
    
    if (this.currentUser) {
      this.sendLeaveMessage(this.currentUser);
    }
    
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
      this.pollingInterval = null;
    }
    
    this.connectionStatus.next(false);
    this.currentUser = '';
  }

  /**
   * Send a chat message via HTTP POST
   */
  sendMessage(content: string, sender: string): void {
    if (!content.trim()) {
      return;
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const payload = {
      message: content,
      sender: sender,
      timestamp: new Date().toISOString()
    };

    console.log('📤 Sending message:', payload);

    this.http.post<ChatResponse>(this.API_URL, payload, { headers })
      .pipe(
        tap(response => console.log('📥 Response received:', response)),
        catchError(error => {
          console.error('❌ Error sending message:', error);
          this.connectionStatus.next(false);
          throw error;
        })
      )
      .subscribe({
        next: (response) => {
          // Convert response to ChatMessage format
          const chatMessage: ChatMessage = {
            type: 'CHAT',
            content: response.response,
            sender: 'bot',
            timestamp: response.timestamp || new Date().toISOString()
          };
          
          // Emit the message to subscribers
          this.messageSubject.next(chatMessage);
          this.connectionStatus.next(true);
        },
        error: (error) => {
          // Emit error message
          const errorMessage: ChatMessage = {
            type: 'CHAT',
            content: 'Sorry, I could not process your message. Please try again.',
            sender: 'system',
            timestamp: new Date().toISOString()
          };
          this.messageSubject.next(errorMessage);
        }
      });
  }

  /**
   * Send join notification
   */
  private sendJoinMessage(username: string): void {
    console.log(`👋 ${username} joined the chat`);
    
    const joinMessage: ChatMessage = {
      type: 'JOIN',
      content: `${username} has joined the chat`,
      sender: username,
      timestamp: new Date().toISOString()
    };
    
    this.messageSubject.next(joinMessage);
    this.connectionStatus.next(true);
  }

  /**
   * Send leave notification
   */
  private sendLeaveMessage(username: string): void {
    console.log(`👋 ${username} left the chat`);
    
    const leaveMessage: ChatMessage = {
      type: 'LEAVE',
      content: `${username} has left the chat`,
      sender: username,
      timestamp: new Date().toISOString()
    };
    
    this.messageSubject.next(leaveMessage);
  }

  /**
   * Check if the chat service is available
   */
  private checkConnection(): void {
    this.http.get(this.HEALTH_URL)
      .pipe(
        catchError(error => {
          console.error('❌ Health check failed:', error);
          this.connectionStatus.next(false);
          throw error;
        })
      )
      .subscribe({
        next: (response) => {
          console.log('✅ Chat service is healthy:', response);
          this.connectionStatus.next(true);
        },
        error: () => {
          console.error('❌ Chat service is unavailable');
          this.connectionStatus.next(false);
        }
      });
  }

  /**
   * Manual health check method
   */
  healthCheck(): Observable<any> {
    return this.http.get(this.HEALTH_URL).pipe(
      tap(() => this.connectionStatus.next(true)),
      catchError(error => {
        this.connectionStatus.next(false);
        throw error;
      })
    );
  }

  /**
   * Get messages stream
   */
  getMessages(): Observable<ChatMessage | null> {
    return this.messageSubject.asObservable();
  }

  /**
   * Get connection status
   */
  getConnectionStatus(): Observable<boolean> {
    return this.connectionStatus.asObservable();
  }

  /**
   * Get current connection state
   */
  isConnected(): boolean {
    return this.connectionStatus.value;
  }

  /**
   * Get current user
   */
  getCurrentUser(): string {
    return this.currentUser;
  }
}
