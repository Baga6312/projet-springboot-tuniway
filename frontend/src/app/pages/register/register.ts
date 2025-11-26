import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { authService } from '../../services/auth';

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
  role: string = 'CLIENT'; // Default role
  showPassword: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  isFormValid: boolean = false;
  isLoading: boolean = false;

  constructor(
    private router: Router,
    private authService: authService
  ) {
  console.log('AuthService:', this.authService);
  console.log('Register method exists?', 'register' in this.authService);
  }

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

      (this.authService as any).register({
        username: this.name.trim(),
        email: this.email.trim(),
        password: this.password,
        role: this.role as 'CLIENT' | 'GUIDE' | 'ADMIN'
      }).subscribe({
        next: (response: any) => {
          this.successMessage = 'Registration successful! Redirecting...';
          this.isLoading = false;
          
          // Redirect to profile after registration
          setTimeout(() => {
            // AuthService has no isGuide(), use the selected role to decide redirect
            if (this.role === 'GUIDE') {
              this.router.navigate(['/guide/profile']);
            } else {
              this.router.navigate(['/profile']);
            }
          }, 1000);
        },
        error: (error: any) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
          console.error('Registration error:', error);
        }
      });
    }
  }
}