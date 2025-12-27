import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { Footer } from '../../components/footer/footer';
import { PortalService } from '../../services/portal.service';
import { authService } from '../../services/auth';
import { Review, Tour } from '../../services/admin.service';

@Component({
  selector: 'app-tours',
  standalone: true,
  imports: [CommonModule, FormsModule, Navbar, Footer],
  templateUrl: './tours.html',
  styleUrl: './tours.css'
})
export class ToursPage implements OnInit, OnDestroy {
  tours: Tour[] = [];
  filteredTours: Tour[] = [];
  selectedTour: Tour | null = null;
  selectedTourReviews: Review[] = [];
  searchTerm = '';
  priceOrder: 'asc' | 'desc' = 'asc';
  loading = true;
  
  makingReservation = false;
  reservationMessage = '';
  reservationSuccess = false;
  
  private destroy$ = new Subject<void>();
  private apiUrl = 'http://localhost:8083/api';

  constructor(
    private readonly portalService: PortalService,
    private readonly http: HttpClient,
    private readonly authService: authService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.portalService
      .getAllTours()
      .pipe(takeUntil(this.destroy$))
      .subscribe(tours => {
        this.tours = tours;
        this.applyFilters();
        this.loading = false;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  applyFilters(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredTours = this.tours
      .filter(tour => tour.titre.toLowerCase().includes(term) || tour.description?.toLowerCase().includes(term))
      .sort((a, b) => {
        const priceA = a.prix || 0;
        const priceB = b.prix || 0;
        return this.priceOrder === 'asc' ? priceA - priceB : priceB - priceA;
      });
    if (this.selectedTour && !this.filteredTours.find(t => t.id === this.selectedTour?.id)) {
      this.clearSelection();
    }
  }

  selectTour(tour: Tour): void {
    this.selectedTour = tour;
    this.reservationMessage = '';
    this.reservationSuccess = false;
    
    if (tour.id) {
      this.portalService
        .getTourReviews(tour.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe(reviews => (this.selectedTourReviews = reviews));
    } else {
      this.selectedTourReviews = [];
    }
  }

  clearSelection(): void {
    this.selectedTour = null;
    this.selectedTourReviews = [];
    this.reservationMessage = '';
    this.reservationSuccess = false;
  }

  // ✅ Check if current user owns the selected tour
  isOwnTour(): boolean {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !this.selectedTour) {
      return false;
    }
    return this.selectedTour.guide?.id === currentUser.id;
  }

  isGuide(): boolean {
    return this.authService.isGuide();
  }

  // ✅ Edit own tour - navigate to guide profile
  editOwnTour(): void {
    this.router.navigate(['/guide/profile'], { 
      queryParams: { tab: 'tours' }
    });
  }

  makeReservation(): void {
    const currentUser = this.authService.getCurrentUser();
    
    if (!currentUser) {
      this.reservationMessage = 'Please login to make a reservation';
      this.reservationSuccess = false;
      setTimeout(() => this.router.navigate(['/login']), 2000);
      return;
    }

    if (!this.selectedTour) {
      this.reservationMessage = 'Please select a tour first';
      this.reservationSuccess = false;
      return;
    }

    this.makingReservation = true;
    this.reservationMessage = '';

    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const reservationData = {
      clientId: currentUser.id,
      guideId: this.selectedTour.guide?.id,
      tourId: this.selectedTour.id,
      type: 'GUIDED_TOUR'
    };

    this.http.post(`${this.apiUrl}/reservations`, reservationData, { headers }).subscribe({
      next: (response) => {
        console.log('✅ Reservation created:', response);
        this.reservationMessage = '✅ Reservation created successfully!';
        this.reservationSuccess = true;
        this.makingReservation = false;
        
        setTimeout(() => {
          this.reservationMessage = '';
        }, 5000);
      },
      error: (error) => {
        console.error('❌ Error creating reservation:', error);
        this.reservationMessage = error.error || '❌ Failed to create reservation. Please try again.';
        this.reservationSuccess = false;
        this.makingReservation = false;
      }
    });
  }

  contactGuide(): void {
    if (!this.selectedTour?.guide?.id) {
      this.reservationMessage = '❌ Guide information not available';
      this.reservationSuccess = false;
      return;
    }

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.reservationMessage = 'Please login to contact the guide';
      this.reservationSuccess = false;
      setTimeout(() => this.router.navigate(['/login']), 2000);
      return;
    }

    this.router.navigate(['/messages'], { 
      queryParams: { userId: this.selectedTour.guide.id }
    });
  }
}