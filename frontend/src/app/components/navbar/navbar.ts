import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { authService, User } from '../../services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  private platformId = inject(PLATFORM_ID);
  isDarkMode: boolean = false;
  currentUser: User | null = null;
  isLoggedIn: boolean = false;

  constructor(private authService: authService) {
    // Initialize dark mode from localStorage or system preference - only in browser
    if (isPlatformBrowser(this.platformId)) {
      const saved = localStorage.getItem('darkMode');
      if (saved !== null) {
        this.isDarkMode = JSON.parse(saved);
      } else {
        this.isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
      }
    }
  }

  ngOnInit() {
    // Subscribe to auth changes
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
      console.log('🔔 Navbar: Auth state changed -', user ? `Logged in as ${user.username}` : 'Logged out');
    });
    
    // Apply dark mode on init
    this.applyDarkMode();
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
    
    // Save to localStorage - only in browser
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