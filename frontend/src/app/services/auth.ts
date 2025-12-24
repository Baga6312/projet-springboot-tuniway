import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

export interface User {
  id: number;
  username: string;
  email: string;
  role: 'CLIENT' | 'GUIDE' | 'ADMIN';
  profilePicture?: string; // Base64 image data URI
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
  private apiUrl = 'http://localhost:8083/api/auth';
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
    this.currentUser$ = this.currentUserSubject.asObservable();
    console.log('Auth service initialized with user:', this.currentUserSubject.value);
  }

  private getUserFromStorage(): User | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    const userStr = localStorage.getItem('currentUser');
    const token = localStorage.getItem('jwtToken');

    if (userStr && token) {
      try {
        return JSON.parse(userStr);
      } catch (e) {
        console.error('Failed to parse stored user', e);
        return null;
      }
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
        
        if (isPlatformBrowser(this.platformId)) {
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
        
        if (isPlatformBrowser(this.platformId)) {
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
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('jwtToken');
    }
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  setCurrentUser(user: User): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('currentUser', JSON.stringify(user));
    }
    this.currentUserSubject.next(user);
  }

  updateCurrentUser(patch: Partial<User>): void {
    const current = this.currentUserSubject.value;
    if (current) {
      const updated = { ...current, ...patch };
      if (isPlatformBrowser(this.platformId)) {
        localStorage.setItem('currentUser', JSON.stringify(updated));
      }
      this.currentUserSubject.next(updated);
      console.log('Updated user in auth service:', updated);
    }
  }

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
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