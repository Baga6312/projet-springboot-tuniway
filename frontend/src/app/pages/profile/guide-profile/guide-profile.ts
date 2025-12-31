import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router'; // ✅ ADD ActivatedRoute
import { Subject, takeUntil } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { API_BASE_URL } from '../../../config/api.config';

import { Navbar } from '../../../components/navbar/navbar';
import { Footer } from '../../../components/footer/footer';
import { authService } from '../../../services/auth';
import { PortalService, Reservation, TourPayload } from '../../../services/portal.service';
import { Review, Tour, User } from '../../../services/admin.service';

type GuideTab = 'overview' | 'tours' | 'reservations' | 'reviews';

@Component({
  selector: 'app-guide-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, Navbar, Footer],
  templateUrl: './guide-profile.html',
  styleUrl: './guide-profile.css'
})
export class GuideProfile implements OnInit, OnDestroy {
  activeTab: GuideTab = 'overview';
  currentUser: User | null = null;
  profileForm: Partial<User> = {};
  tours: Tour[] = [];
  reservations: Reservation[] = [];
  tourReviews: Record<number, Review[]> = {};
  guideFeedback: Review[] = [];
  newTour: Partial<TourPayload> = {
    titre: '',
    description: '',
    prix: 0,
    date: ''
  };
  savingProfile = false;
  publishingTour = false;
  statusMessage = '';
  tourMessage = '';
  loading = true;
  
  processingReservation: number | null = null;
  editingTourId: number | null = null;
  
  // Image upload states
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  uploadingImage: boolean = false;
  uploadSuccess: boolean = false;
  uploadError: string | null = null;
  
  private apiUrl = `${API_BASE_URL}`;
  private destroy$ = new Subject<void>();

  constructor(
    private readonly portalService: PortalService,
    private readonly authService: authService,
    private readonly router: Router,
    private readonly route: ActivatedRoute, // ✅ ADD THIS
    private readonly http: HttpClient
  ) {}

  ngOnInit(): void {
    this.bootstrapGuide();
    
    // ✅ ADD THIS: Check for tab query parameter
    this.route.queryParams.subscribe(params => {
      if (params['tab']) {
        this.setActiveTab(params['tab'] as GuideTab);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  setActiveTab(tab: GuideTab): void {
    console.log('Setting active tab to:', tab);
    console.log('Current tours:', this.tours);
    this.activeTab = tab;
  }

  saveProfile(): void {
    if (!this.currentUser?.id) {
      return;
    }
    this.savingProfile = true;
    
    const updateData = {
      username: this.profileForm.username,
      email: this.profileForm.email,
      profilePicture: this.profileForm.profilePicture
    };

    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.http.put(`${this.apiUrl}/users/profile`, updateData, { headers }).subscribe({
      next: (response: any) => {
        console.log('Profile updated:', response);
        
        this.authService.updateCurrentUser({
          username: response.username,
          email: response.email,
          profilePicture: response.profilePicture
        });
        
        this.currentUser = this.authService.getCurrentUser();
        this.statusMessage = '✅ Profile updated successfully!';
        this.uploadSuccess = true;
        this.savingProfile = false;
        
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

  // Image upload handler
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    
    if (input.files && input.files[0]) {
      const file = input.files[0];
      
      if (!file.type.startsWith('image/')) {
        this.uploadError = 'Please select a valid image file';
        return;
      }
      
      if (file.size > 5 * 1024 * 1024) {
        this.uploadError = 'Image size must be less than 5MB';
        return;
      }
      
      this.selectedFile = file;
      this.uploadError = null;
      this.uploadSuccess = false;
      
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result as string;
        this.profileForm.profilePicture = reader.result as string;
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

  getProfileImageSrc(): string | null {
    const profilePicture = this.previewUrl || this.currentUser?.profilePicture || this.profileForm.profilePicture;
    
    if (!profilePicture) {
      return null;
    }
    
    if (profilePicture.startsWith('data:image')) {
      return profilePicture;
    }
    
    if (profilePicture.startsWith('http://') || profilePicture.startsWith('https://')) {
      return profilePicture;
    }
    
    return `data:image/jpeg;base64,${profilePicture}`;
  }

  createTour(): void {
    if (!this.currentUser?.id || !this.newTour.titre || !this.newTour.prix) {
      return;
    }
    this.publishingTour = true;
    const payload: TourPayload = {
      titre: this.newTour.titre!,
      description: this.newTour.description,
      prix: Number(this.newTour.prix),
      date: this.newTour.date || undefined,
      guideId: this.currentUser.id
    };

    this.portalService
      .createTour(payload)
      .pipe(takeUntil(this.destroy$))
      .subscribe(tour => {
        this.tours = [tour, ...this.tours];
        this.newTour = { titre: '', description: '', prix: 0, date: '' };
        this.publishingTour = false;
        this.tourMessage = 'Tour published successfully.';
        setTimeout(() => (this.tourMessage = ''), 3000);
      });
  }

  editTour(tour: Tour): void {
    this.editingTourId = tour.id || null;
    this.newTour = {
      titre: tour.titre,
      description: tour.description,
      prix: tour.prix,
      date: tour.date ? new Date(tour.date).toISOString().split('T')[0] : ''
    };
    
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  updateTour(): void {
    if (!this.editingTourId || !this.currentUser?.id || !this.newTour.titre || !this.newTour.prix) {
      return;
    }
    
    this.publishingTour = true;
    
    const payload = {
      guideId: this.currentUser.id,
      titre: this.newTour.titre,
      description: this.newTour.description,
      prix: Number(this.newTour.prix),
      date: this.newTour.date || undefined
    };

    this.portalService
      .updateTour(this.editingTourId, payload)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedTour) => {
          const index = this.tours.findIndex(t => t.id === this.editingTourId);
          if (index !== -1) {
            this.tours[index] = updatedTour;
          }
          
          this.cancelEdit();
          this.publishingTour = false;
          this.tourMessage = 'Tour updated successfully.';
          setTimeout(() => (this.tourMessage = ''), 3000);
        },
        error: (error) => {
          console.error('Error updating tour:', error);
          this.publishingTour = false;
          this.tourMessage = 'Failed to update tour. Please try again.';
        }
      });
  }

  cancelEdit(): void {
    this.editingTourId = null;
    this.newTour = { titre: '', description: '', prix: 0, date: '' };
  }

  deleteTour(tourId?: number): void {
    if (!tourId) {
      return;
    }
    
    if (!confirm('Are you sure you want to delete this tour?')) {
      return;
    }
    
    this.portalService
      .deleteTour(tourId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.tours = this.tours.filter(t => t.id !== tourId);
      });
  }

  confirmReservation(reservationId?: number): void {
    if (!reservationId) return;
    
    this.processingReservation = reservationId;
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.put(`${this.apiUrl}/reservations/${reservationId}/confirm`, {}, { headers })
      .subscribe({
        next: (updatedReservation: any) => {
          console.log('✅ Reservation confirmed:', updatedReservation);
          
          const index = this.reservations.findIndex(r => r.id === reservationId);
          if (index !== -1) {
            this.reservations[index] = updatedReservation;
          }
          
          this.processingReservation = null;
        },
        error: (error) => {
          console.error('❌ Error confirming reservation:', error);
          alert('Failed to confirm reservation. Please try again.');
          this.processingReservation = null;
        }
      });
  }

  cancelReservation(reservationId?: number): void {
    if (!reservationId) return;
    
    if (!confirm('Are you sure you want to cancel this reservation?')) {
      return;
    }
    
    this.processingReservation = reservationId;
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.put(`${this.apiUrl}/reservations/${reservationId}/cancel`, {}, { headers })
      .subscribe({
        next: (updatedReservation: any) => {
          console.log('✅ Reservation cancelled:', updatedReservation);
          
          const index = this.reservations.findIndex(r => r.id === reservationId);
          if (index !== -1) {
            this.reservations[index] = updatedReservation;
          }
          
          this.processingReservation = null;
        },
        error: (error) => {
          console.error('❌ Error cancelling reservation:', error);
          alert('Failed to cancel reservation. Please try again.');
          this.processingReservation = null;
        }
      });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private bootstrapGuide(): void {
    const fallbackGuide: User = {
      id: 2,
      username: 'Guide Explorer',
      email: 'guide@tuniway.tn',
      role: 'GUIDE',
      profilePicture: ''
    };

    const snapshot = this.authService.getCurrentUser() ?? fallbackGuide;
    if (!this.authService.getCurrentUser()) {
      this.authService.setCurrentUser(snapshot as any);
    }
    this.currentUser = snapshot;
    this.profileForm = { ...snapshot };
    
    // Set preview URL from existing profile picture
    if (this.currentUser.profilePicture) {
      this.previewUrl = this.currentUser.profilePicture;
    }

    if (!snapshot.id) {
      this.loading = false;
      return;
    }

    this.portalService
      .getUserProfile(snapshot.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
        this.profileForm = { ...user };
        this.authService.updateCurrentUser(user);
        this.loading = false;
      });

    this.loadTours(snapshot.id);
    this.loadReservations(snapshot.id);
    this.loadReviews(snapshot.id);
  }

  loadTours(guideId: number): void {
    this.portalService
      .getGuideTours(guideId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(tours => {
        this.tours = tours;
        tours.forEach(tour => {
          if (tour.id) {
            this.portalService
              .getTourReviews(tour.id)
              .pipe(takeUntil(this.destroy$))
              .subscribe(reviews => (this.tourReviews[tour.id!] = reviews));
          }
        });
      });
  }

  private loadReservations(guideId: number): void {
    this.portalService
      .getGuideReservations(guideId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => (this.reservations = data));
  }

  private loadReviews(guideId: number): void {
    this.portalService
      .getUserReviews(guideId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => (this.guideFeedback = data));
  }

  get totalTourRevenue(): number {
    return this.tours.reduce((acc, tour) => acc + (tour.prix || 0), 0);
  }

  get averageGuideRating(): number | null {
    if (!this.guideFeedback.length) {
      return null;
    }
    const sum = this.guideFeedback.reduce((acc, review) => acc + (review.rating || 0), 0);
    return sum / this.guideFeedback.length;
  }
}