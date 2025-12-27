import { Component, OnInit, OnDestroy, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { authService, User } from '../../services/auth';
import { MessageService } from '../../services/message.service'; // ✅ ADD
import { interval, Subscription } from 'rxjs'; // ✅ ADD
import { switchMap } from 'rxjs/operators'; // ✅ ADD

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit, OnDestroy { // ✅ ADD OnDestroy
  private platformId = inject(PLATFORM_ID);
  isDarkMode: boolean = false;
  currentUser: User | null = null;
  isLoggedIn: boolean = false;
  unreadMessageCount: number = 0; // ✅ ADD THIS
  
  private messageCheckSubscription?: Subscription; // ✅ ADD THIS

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
    
    // ✅ ADD THIS: Initialize current user immediately
    this.currentUser = this.authService.getCurrentUser();
    this.isLoggedIn = !!this.currentUser;
  }
}
  

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
      console.log('🔔 Navbar: Auth state changed -', user ? `Logged in as ${user.username}` : 'Logged out');
      
      // ✅ START/STOP MESSAGE CHECKING
      if (user) {
        this.startMessageChecking();
      } else {
        this.stopMessageChecking();
        this.unreadMessageCount = 0;
      }
    });
    
    this.applyDarkMode();
  }

  // ✅ ADD THIS METHOD
  ngOnDestroy() {
    this.stopMessageChecking();
  }


  
  private startMessageChecking() {
  if (!this.currentUser?.id) return;

  // ✅ ADD DELAY: Wait a bit for token to be ready
  setTimeout(() => {
    this.checkUnreadMessages();

    // Then check every 10 seconds
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
  }, 1000); // Wait 1 second for token to load
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