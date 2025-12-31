import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { authService } from './auth';
import { API_BASE_URL } from '../config/api.config';

export interface Message {
  id?: number;
  sender: any;
  receiver: any;
  content: string;
  sentAt: string;
  isRead: boolean;
}

export interface ConversationUser {
  id: number;
  username: string;
  email: string;
  role: string;
  profilePicture?: string;
}

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private apiUrl = `${API_BASE_URL}/messages`;

  constructor(
    private http: HttpClient,
    private authService: authService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  sendMessage(receiverId: number, content: string): Observable<Message> {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      throw new Error('User not authenticated');
    }

    const payload = {
      senderId: currentUser.id,
      receiverId: receiverId,
      content: content
    };

    return this.http.post<Message>(`${this.apiUrl}/send`, payload, { headers: this.getHeaders() });
  }

  getConversation(user1Id: number, user2Id: number): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/conversation/${user1Id}/${user2Id}`, { headers: this.getHeaders() });
  }

  getConversations(userId: number): Observable<ConversationUser[]> {
    return this.http.get<ConversationUser[]>(`${this.apiUrl}/conversations/${userId}`, { headers: this.getHeaders() });
  }

  getUnreadCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread/${userId}`, { headers: this.getHeaders() });
  }

  markAsRead(receiverId: number, senderId: number): Observable<void> {
    const payload = {
      receiverId: receiverId,
      senderId: senderId
    };
    return this.http.put<void>(`${this.apiUrl}/mark-read`, payload, { headers: this.getHeaders() });
  }
}
