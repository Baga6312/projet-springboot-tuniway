import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { authService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class OAuth2Service {
  // ✅ FIXED: Use your actual backend URL
  private backendUrl = 'http://tuniway.duckdns.org:8083';
  
  constructor(
    private router: Router,
    private authService: authService,
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  loginWithGoogle(): void {
    // ✅ Remove /api prefix - Spring Security default is /oauth2/authorization
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
    
    // Fetch complete user profile from backend
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
```

## Key Changes:
1. **Changed `backendUrl`** from `http://localhost:8083` to `http://tuniway.duckdns.org:8083`
2. **Removed `/api` prefix** - Spring Security's default OAuth2 endpoint is `/oauth2/authorization`, not `/api/oauth2/authorization`

## Next Steps:

After making this change, you also need to **update your Google Cloud Console** OAuth2 configuration:

**Authorized redirect URIs should be:**
```
http://tuniway.duckdns.org:8083/login/oauth2/code/google
