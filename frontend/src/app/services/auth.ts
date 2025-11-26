import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

export interface User {
  id: number;
  username: string;
  email: string;
  role: 'CLIENT' | 'GUIDE' | 'ADMIN';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role: 'CLIENT' | 'GUIDE' | 'ADMIN';
}

export interface AuthResponse {
  id: number;
  username: string;
  email: string;
  role: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class authService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  private getUserFromStorage(): User | null {
    if (isPlatformBrowser(this.platformId)) {
      const userStr = localStorage.getItem('currentUser');
      return userStr ? JSON.parse(userStr) : null;
    }
    return null;
  }

 login(credentials: LoginRequest): Observable<AuthResponse> {
  return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
    tap(response => {
      const user: User = {
        id: response.id,
        username: response.username,
        email: response.email,
        role: response.role as 'CLIENT' | 'GUIDE' | 'ADMIN'
      };
      if (isPlatformBrowser(this.platformId)) {
        localStorage.setItem('currentUser', JSON.stringify(user));
      }
      this.currentUserSubject.next(user);
    })
  );
} 

 register(data: RegisterRequest): Observable<AuthResponse> {
  return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data).pipe(
    tap(response => {
      const user: User = {
        id: response.id,
        username: response.username,
        email: response.email,
        role: response.role as 'CLIENT' | 'GUIDE' | 'ADMIN'
      };
      if (isPlatformBrowser(this.platformId)) {
        localStorage.setItem('currentUser', JSON.stringify(user));
      }
      this.currentUserSubject.next(user);
    })
  );
}

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
    }
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  isGuide(): boolean {
    return this.currentUserSubject.value?.role === 'GUIDE';
  }

  isAdmin(): boolean {
    return this.currentUserSubject.value?.role === 'ADMIN';
  }

  isClient(): boolean {
    return this.currentUserSubject.value?.role === 'CLIENT';
  }
}