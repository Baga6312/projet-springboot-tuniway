import { Component, OnInit } from '@angular/core';
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
  isDarkMode: boolean = false;
  currentUser: User | null = null;
  isLoggedIn: boolean = false;

  constructor(private authService: authService) {
    // Initialize dark mode from localStorage or system preference
    if (typeof localStorage !== 'undefined') {
      const saved = localStorage.getItem('darkMode');
      if (saved !== null) {
        this.isDarkMode = JSON.parse(saved);
      } else if (typeof window !== 'undefined') {
        this.isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
      }
    }
  }

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
    });

    // Apply dark mode on init
    this.applyDarkMode();
  }

  isGuide(): boolean {
    return this.authService.isGuide();
  }

  logout() {
    this.authService.logout();
    window.location.href = '/';
  }

  toggleDarkMode() {
    this.isDarkMode = !this.isDarkMode;
    this.applyDarkMode();
    
    // Save to localStorage
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('darkMode', JSON.stringify(this.isDarkMode));
    }
  }

  private applyDarkMode() {
    if (typeof document !== 'undefined') {
      if (this.isDarkMode) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    }
  }
}