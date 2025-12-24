import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { ChatbotComponent } from "./components/chatbot/chatbot";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    ChatbotComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private platformId = inject(PLATFORM_ID);
  title = 'tuniway-frontend';
  
  ngOnInit() {
    // Initialize dark mode on app startup - only in browser
    if (isPlatformBrowser(this.platformId)) {
      const savedDarkMode = localStorage.getItem('darkMode');
      if (savedDarkMode === 'true') {
        document.documentElement.classList.add('dark');
      }
    }
  }
}