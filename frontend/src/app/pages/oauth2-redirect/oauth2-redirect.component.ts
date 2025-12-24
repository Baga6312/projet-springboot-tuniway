import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OAuth2Service } from '../../services/oauth2.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-oauth2-redirect',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex items-center justify-center min-h-screen">
      <div class="text-center">
        <div class="loading-spinner"></div>
        <p class="mt-4 text-gray-600">Connexion en cours...</p>
      </div>
    </div>
  `,
  styles: [`
    .loading-spinner {
      border: 4px solid #f3f3f3;
      border-top: 4px solid #3498db;
      border-radius: 50%;
      width: 50px;
      height: 50px;
      animation: spin 1s linear infinite;
      margin: 0 auto;
    }
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  `]
})
export class OAuth2RedirectComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private oauth2Service: OAuth2Service,
    @Inject(PLATFORM_ID) private platformId: Object  // ✅ Inject platform ID
  ) {}

  ngOnInit(): void {
    // ✅ Only execute in browser
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    // Extract query parameters
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const username = params['username'];
      const email = params['email'];
      const role = params['role'];
      const id = params['id'];

      console.log('OAuth2 Redirect - Params:', { token, username, email, role, id });

      if (token && username && email && role && id) {
        this.oauth2Service.handleOAuth2Callback(token, username, email, role, id);
      } else {
        console.error('Missing OAuth2 parameters');
        this.router.navigate(['/login']);
      }
    });
  }
}