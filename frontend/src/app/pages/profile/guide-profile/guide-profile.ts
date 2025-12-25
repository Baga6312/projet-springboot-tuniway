import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http'; // ✅ ADD

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
  
  // ✅ ADD THESE
  processingReservation: number | null = null;
  private apiUrl = 'http://localhost:8083/api';

  private destroy$ = new Subject<void>();

  constructor(
    private readonly portalService: PortalService,
    private readonly authService: authService,
    private readonly router: Router,
    private readonly http: HttpClient  // ✅ ADD
  ) {}

  ngOnInit(): void {
    this.bootstrapGuide();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  setActiveTab(tab: GuideTab): void {
    this.activeTab = tab;
  }

  saveProfile(): void {
    if (!this.currentUser?.id) {
      return;
    }
    this.savingProfile = true;
    this.portalService
      .updateUserProfile(this.currentUser.id, this.profileForm)
      .pipe(takeUntil(this.destroy$))
      .subscribe(updated => {
        this.currentUser = updated;
        this.authService.updateCurrentUser(updated);
        this.savingProfile = false;
        this.statusMessage = 'Profile updated';
        setTimeout(() => (this.statusMessage = ''), 3000);
      });
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

  deleteTour(tourId?: number): void {
    if (!tourId) {
      return;
    }
    this.portalService
      .deleteTour(tourId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.tours = this.tours.filter(t => t.id !== tourId);
      });
  }

  // ✅ ADD THESE METHODS
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
          
          // Update the reservation in the list
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
          
          // Update the reservation in the list
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