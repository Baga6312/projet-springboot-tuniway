import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { authService } from '../../services/auth';

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
    private authService: authService
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
      (this.authService as any).login({
        email: this.email.trim(),
        password: this.password
      }).subscribe({
        next: (response: any) => {
          this.successMessage = 'Login successful! Redirecting...';
          this.isLoading = false;
          
          // Redirect based on role
          setTimeout(() => {
            const svc: any = this.authService;
            if (svc.isGuide && svc.isGuide()) {
              this.router.navigate(['/guide/profile']);
            } else if (svc.isAdmin && svc.isAdmin()) {
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
      };
    }
  }
