import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { authService, User } from '../../../services/auth';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Navbar} from '../../../components/navbar/navbar';
import { API_BASE_URL } from '../../../config/api.

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, Navbar],
  templateUrl: './user-profile.html',
  styleUrls: ['./user-profile.css']
})
export class UserProfile implements OnInit {
  currentUser: User | null = null;
  activeTab: string = 'overview';
  
  profileForm = {
    username: '',
    email: '',
    profilePicture: ''
  };

  // Image upload states
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  uploadingImage: boolean = false;
  uploadSuccess: boolean = false;
  uploadError: string | null = null;

  loadingProfile = true;
  savingProfile = false;
  statusMessage = '';
  
  reservations: any[] = [];
  reviews: any[] = [];
  favoritePlaces: any[] = [];
  averageReviewScore: number | null = null;

  private apiUrl = `${API_BASE_URL}`; 

  constructor(
    private authService: authService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.bootstrapUser();
  }

bootstrapUser(): void {
  this.currentUser = this.authService.getCurrentUser();
  
  if (!this.currentUser) {
    this.router.navigate(['/login']);
    return;
  }

  
  this.profileForm.username = this.currentUser.username;
  this.profileForm.email = this.currentUser.email;
  this.profileForm.profilePicture = this.currentUser.profilePicture || '';
  
  if (this.currentUser.profilePicture) {
    this.previewUrl = this.currentUser.profilePicture;
  }
  
  this.loadingProfile = false;
  this.loadUserData();
}




loadUserData(): void {
  if (!this.currentUser) return;

  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  // Load favorites
  this.http.get<any[]>(`${this.apiUrl}/favorites`, { headers }).subscribe({
    next: (favorites) => {
      this.favoritePlaces = favorites;
    },
    error: (error) => {
      this.favoritePlaces = [];
    }
  });

  this.http.get<any[]>(`${this.apiUrl}/reservations/client/${this.currentUser.id}`, { headers }).subscribe({
    next: (reservations) => {
      this.reservations = reservations;
    },
    error: (error) => {
      this.reservations = [];
    }
  });

  this.http.get<any[]>(`${this.apiUrl}/reviews/user/${this.currentUser.id}`).subscribe({
    next: (reviews) => {
      this.reviews = reviews;
      
      if (reviews.length > 0) {
        const total = reviews.reduce((sum, review) => sum + review.rating, 0);
        this.averageReviewScore = total / reviews.length;
      }
    },
    error: (error) => {
      this.reviews = [];
    }
  });
}







  // Image upload handler
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    
    if (input.files && input.files[0]) {
      const file = input.files[0];
      
      // Validate file type
      if (!file.type.startsWith('image/')) {
        this.uploadError = 'Please select a valid image file';
        return;
      }
      
      // Validate file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.uploadError = 'Image size must be less than 5MB';
        return;
      }
      
      this.selectedFile = file;
      this.uploadError = null;
      this.uploadSuccess = false;
      
      // Create preview
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result as string;
        this.profileForm.profilePicture = reader.result as string; // Store base64
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage(): void {
    this.selectedFile = null;
    this.previewUrl = null;
    this.profileForm.profilePicture = '';
    this.uploadSuccess = false;
    this.uploadError = null;
  }

  saveProfile(): void {
    if (this.savingProfile) return;
    
    this.savingProfile = true;
    this.statusMessage = '';
    this.uploadError = null;
    this.uploadSuccess = false;

    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const updateData = {
      username: this.profileForm.username,
      email: this.profileForm.email,
      profilePicture: this.profileForm.profilePicture // Base64 string with data URI
    };

    this.http.put(`${this.apiUrl}/users/profile`, updateData, { headers }).subscribe({
      next: (response: any) => {
        
        this.authService.updateCurrentUser({
          username: response.username,
          email: response.email,
          profilePicture: response.profilePicture
        });
        
        this.currentUser = this.authService.getCurrentUser();
        this.statusMessage = '✅ Profile updated successfully!';
        this.uploadSuccess = true;
        this.savingProfile = false;
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.statusMessage = '';
          this.uploadSuccess = false;
        }, 3000);
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        this.uploadError = error.error?.error || 'Failed to update profile';
        this.savingProfile = false;
      }
    });
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  refreshData(): void {
    this.loadUserData();
  }



// Update this method to return null when no profile picture exists
getProfileImageSrc(): string | null {
  const profilePicture = this.previewUrl || this.currentUser?.profilePicture || this.profileForm.profilePicture;
  
  if (!profilePicture) {
    return null; // ✅ Return null to trigger the template's fallback
  }
  
  // Check if it's already a base64 data URI
  if (profilePicture.startsWith('data:image')) {
    return profilePicture;
  }
  
  // Check if it's a URL (OAuth image from GitHub/Google)
  if (profilePicture.startsWith('http://') || profilePicture.startsWith('https://')) {
    return profilePicture;
  }
  
  // If it's base64 without prefix, add the prefix
  return `data:image/jpeg;base64,${profilePicture}`;
}
}
