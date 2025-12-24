import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CHATBOT_API_URL } from '../config/api.config';

export interface ChatbotResponse {
  response: string;
  confidence?: number;
  intent?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  private apiUrl = CHATBOT_API_URL;

  constructor(private http: HttpClient) {}

  sendMessage(message: string): Observable<ChatbotResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<ChatbotResponse>(
      `${this.apiUrl}/chat`,
      { message: message },
      { headers }
    );
  }

  healthCheck(): Observable<any> {
    return this.http.get(`${this.apiUrl}/health`);
  }
}