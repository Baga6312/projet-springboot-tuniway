import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { authService } from '../../services/auth';
import { OAuth2Service } from '../../services/oauth2.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  name: string = '';
  email: string = '';
  password: string = '';
  role: string = 'CLIENT';
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
    const name = this.name.trim();
    const email = this.email.trim();
    const password = this.password;
    this.isFormValid = !!(name && email && password && password.length >= 6);
  }

onSubmit() {
  if (this.isFormValid && !this.isLoading) {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.authService.register({
      username: this.name.trim(),
      email: this.email.trim(),
      password: this.password,
      role: 'CLIENT' // Always CLIENT now
    }).subscribe({
      next: (response) => {
        this.successMessage = 'Registration successful! Redirecting...';
        this.isLoading = false;
        
        setTimeout(() => {
          this.router.navigate(['/profile']); // Always redirect to client profile
        }, 1000);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
        console.error('Registration error:', error);
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