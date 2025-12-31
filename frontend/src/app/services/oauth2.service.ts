import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { authService } from './auth';
import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class OAuth2Service {
  private backendUrl = `${API_BASE_URL}`;
  
  constructor(
    private router: Router,
    private authService: authService,
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  loginWithGoogle(): void {
    window.location.href = `${this.backendUrl}/oauth2/authorization/google`;
  }

  loginWithGitHub(): void {
    window.location.href = `${this.backendUrl}/oauth2/authorization/github`;
  }

  handleOAuth2Callback(
    token: string, 
    username: string, 
    email: string, 
    role: string, 
    id: string,
    profilePicture?: string
  ): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    console.log('Handling OAuth2 callback:', { token, username, email, role, id, profilePicture });
    
    // Store JWT token
    localStorage.setItem('jwtToken', token);
    
    // ✅ FIXED: Added parenthesis before backtick
    this.http.get<any>(`${this.backendUrl}/api/users/profile/${id}`).subscribe({
      next: (userProfile) => {
        console.log('✅ Fetched complete user profile from backend:', userProfile);
        
        const user = {
          id: userProfile.id,
          username: userProfile.username,
          email: userProfile.email,
          role: userProfile.role as 'CLIENT' | 'GUIDE' | 'ADMIN',
          profilePicture: userProfile.profilePicture || undefined
        };
        
        console.log('✅ Setting user with profilePicture from DB:', user.profilePicture);
        
        // Use AuthService.setCurrentUser
        this.authService.setCurrentUser(user);
        
        // Redirect based on role
        if (user.role === 'ADMIN') {
          console.log('Redirecting to /admin');
          this.router.navigate(['/admin']);
        } else if (user.role === 'GUIDE') {
          console.log('Redirecting to /guide/profile');
          this.router.navigate(['/guide/profile']);
        } else {
          console.log('Redirecting to /profile');
          this.router.navigate(['/profile']);
        }
      },
      error: (error) => {
        console.error('❌ Failed to fetch user profile from backend:', error);
        // Fallback: use the data from URL params
        const user = {
          id: parseInt(id),
          username: username,
          email: email,
          role: role as 'CLIENT' | 'GUIDE' | 'ADMIN',
          profilePicture: profilePicture || undefined
        };
        this.authService.setCurrentUser(user);
        this.router.navigate(['/profile']);
      }
    });
  }
}
