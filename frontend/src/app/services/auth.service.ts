import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { User } from './admin.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly STORAGE_KEY = 'tuniway-current-user';
  private currentUserSubject = new BehaviorSubject<User | null>(this.readFromStorage());

  currentUser$ = this.currentUserSubject.asObservable();

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  setCurrentUser(user: User): void {
    this.persist(user);
    this.currentUserSubject.next(user);
  }

  updateCurrentUser(patch: Partial<User>): void {
    const merged = { ...this.currentUserSubject.value, ...patch } as User;
    this.persist(merged);
    this.currentUserSubject.next(merged);
  }

  logout(): void {
    if (this.isBrowser()) {
      localStorage.removeItem(this.STORAGE_KEY);
    }
    this.currentUserSubject.next(null);
  }

  private readFromStorage(): User | null {
    if (!this.isBrowser()) {
      return null;
    }
    const raw = localStorage.getItem(this.STORAGE_KEY);
    try {
      return raw ? (JSON.parse(raw) as User) : null;
    } catch {
      return null;
    }
  }

  private persist(user: User): void {
    if (this.isBrowser()) {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));
    }
  }

  private isBrowser(): boolean {
    return typeof window !== 'undefined';
  }
}


