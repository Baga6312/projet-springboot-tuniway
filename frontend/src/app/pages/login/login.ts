import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { authService } from '../../services/auth';
import { OAuth2Service } from '../../services/oauth2.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  email: string = '';
  password: string = '';
  showPassword: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  isFormValid: boolean = false;
  isLoading: boolean = false;

  constructor(
    private router: Router,
    private authService: authService,
    private oauth2Service: OAuth2Service
  ) {}

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  checkFormValidity() {
    const email = this.email.trim();
    const password = this.password;
    this.isFormValid = !!(email && password);
  }

  onSubmit() {
    if (this.isFormValid && !this.isLoading) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.authService.login({
        username: this.email.trim(),
        password: this.password
      }).subscribe({
        next: (response) => {
          this.successMessage = 'Login successful! Redirecting...';
          this.isLoading = false;
          
          setTimeout(() => {
            if (this.authService.isGuide()) {
              this.router.navigate(['/guide/profile']);
            } else if (this.authService.isAdmin()) {
              this.router.navigate(['/admin']);
            } else {
              this.router.navigate(['/profile']);
            }
          }, 1000);
        },
        error: (error: HttpErrorResponse) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Login failed. Please check your credentials.';
          console.error('Login error:', error);
        }
      });
    }
  }

  // ✅ NEW: OAuth2 Methods
  loginWithGoogle() {
    this.oauth2Service.loginWithGoogle();
  }

  loginWithGitHub() {
    this.oauth2Service.loginWithGitHub();
  }
}