import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { API_BASE_URL } from '../config/api.config';

export interface User {
  id: number;
  username: string;
  email: string;
  role: 'CLIENT' | 'GUIDE' | 'ADMIN';
  profilePicture?: string;
}

export interface LoginRequest {
  username: string;
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
  profilePicture?: string;
  message?: string;
  token?: string;
  accessToken?: string;
}

@Injectable({
  providedIn: 'root'
})
export class authService {
  private apiUrl = `${API_BASE_URL}/auth`;

  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    this.currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  private getUserFromStorage(): User | null {
    if (!this.isBrowser) {
      return null;
    }

    try {
      const userStr = localStorage.getItem('currentUser');
      const token = localStorage.getItem('jwtToken');
      
      if (userStr && token) {
        return JSON.parse(userStr);
      }
    } catch (e) {
      console.error('Failed to parse stored user', e);
    }

    return null;
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/signin`, credentials).pipe(
      tap(response => {
        const user: User = {
          id: response.id,
          username: response.username,
          email: response.email,
          role: response.role as 'CLIENT' | 'GUIDE' | 'ADMIN',
          profilePicture: response.profilePicture
        };
        
        if (this.isBrowser) {
          localStorage.setItem('currentUser', JSON.stringify(user));
          const token = response.token || response.accessToken;
          if (token) {
            localStorage.setItem('jwtToken', token);
          }
        }
        
        this.currentUserSubject.next(user);
      })
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/signup`, data).pipe(
      tap(response => {
        const user: User = {
          id: response.id,
          username: response.username,
          email: response.email,
          role: response.role as 'CLIENT' | 'GUIDE' | 'ADMIN',
          profilePicture: response.profilePicture
        };
        
        if (this.isBrowser) {
          localStorage.setItem('currentUser', JSON.stringify(user));
          const token = response.token || response.accessToken;
          if (token) {
            localStorage.setItem('jwtToken', token);
          }
        }
        
        this.currentUserSubject.next(user);
      })
    );
  }

  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('jwtToken');
    }
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  setCurrentUser(user: User): void {
    if (this.isBrowser) {
      localStorage.setItem('currentUser', JSON.stringify(user));
    }
    this.currentUserSubject.next(user);
  }

  updateCurrentUser(patch: Partial<User>): void {
    const current = this.currentUserSubject.value;
    if (current) {
      const updated = { ...current, ...patch };
      if (this.isBrowser) {
        localStorage.setItem('currentUser', JSON.stringify(updated));
      }
      this.currentUserSubject.next(updated);
    }
  }

  getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem('jwtToken');
    }
    return null;
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
