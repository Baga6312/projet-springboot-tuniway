import { Component, OnInit, OnDestroy, PLATFORM_ID, inject, afterNextRender } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { authService, User } from '../../services/auth';
import { MessageService } from '../../services/message.service';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit, OnDestroy {
  private platformId = inject(PLATFORM_ID);
  isDarkMode: boolean = false;
  currentUser: User | null = null;
  isLoggedIn: boolean = false;
  unreadMessageCount: number = 0;
  isAuthReady: boolean = false; // ✅ ADD THIS - prevents flicker
  
  private messageCheckSubscription?: Subscription;

  constructor(
    private authService: authService,
    private messageService: MessageService
  ) {
    if (isPlatformBrowser(this.platformId)) {
      const saved = localStorage.getItem('darkMode');
      if (saved !== null) {
        this.isDarkMode = JSON.parse(saved);
      } else {
        this.isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
      }
    }

    // ✅ ADD THIS: Only set auth state after client-side render
    afterNextRender(() => {
      this.currentUser = this.authService.getCurrentUser();
      this.isLoggedIn = !!this.currentUser;
      this.isAuthReady = true; // ✅ Now safe to show buttons
      
      if (this.currentUser) {
        this.startMessageChecking();
      }
    });
  }
  
  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
      this.isAuthReady = true; // ✅ Ensure it's set
      console.log('🔔 Navbar: Auth state changed -', user ? `Logged in as ${user.username}` : 'Logged out');
      
      if (user) {
        this.startMessageChecking();
      } else {
        this.stopMessageChecking();
        this.unreadMessageCount = 0;
      }
    });
    
    this.applyDarkMode();
  }

  ngOnDestroy() {
    this.stopMessageChecking();
  }

  private startMessageChecking() {
    if (!this.currentUser?.id) return;

    setTimeout(() => {
      this.checkUnreadMessages();

      this.messageCheckSubscription = interval(10000)
        .pipe(switchMap(() => this.messageService.getUnreadCount(this.currentUser!.id)))
        .subscribe({
          next: (count) => {
            this.unreadMessageCount = count;
          },
          error: (error) => {
            console.error('Error checking unread messages:', error);
          }
        });
    }, 1000);
  }

  private stopMessageChecking() {
    if (this.messageCheckSubscription) {
      this.messageCheckSubscription.unsubscribe();
    }
  }

  private checkUnreadMessages() {
    if (!this.currentUser?.id) {
      console.log('❌ No user ID, skipping message check');
      return;
    }

    console.log('🔔 Checking unread messages for user:', this.currentUser.id);

    this.messageService.getUnreadCount(this.currentUser.id).subscribe({
      next: (count) => {
        console.log('✅ Unread count received:', count);
        this.unreadMessageCount = count;
      },
      error: (error) => {
        console.error('❌ Error checking unread messages:', error);
      }
    });
  }

  isGuide(): boolean {
    return this.authService.isGuide();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  logout() {
    this.authService.logout();
    if (isPlatformBrowser(this.platformId)) {
      window.location.href = '/';
    }
  }

  toggleDarkMode() {
    this.isDarkMode = !this.isDarkMode;
    this.applyDarkMode();
    
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('darkMode', JSON.stringify(this.isDarkMode));
    }
  }

  private applyDarkMode() {
    if (isPlatformBrowser(this.platformId)) {
      if (this.isDarkMode) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    }
  }
}
