import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http'; // ✅ ADD HttpClient
import { Router } from '@angular/router'; // ✅ ADD Router
import { Navbar } from '../../components/navbar/navbar';
import { Footer } from '../../components/footer/footer';
import { PortalService } from '../../services/portal.service';
import { authService } from '../../services/auth'; // ✅ ADD authService
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
  
  // ✅ ADD THESE NEW PROPERTIES
  makingReservation = false;
  reservationMessage = '';
  reservationSuccess = false;
  
  private destroy$ = new Subject<void>();
  private apiUrl = 'http://localhost:8083/api';

  constructor(
    private readonly portalService: PortalService,
    private readonly http: HttpClient,        // ✅ ADD
    private readonly authService: authService, // ✅ ADD
    private readonly router: Router           // ✅ ADD
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
    this.reservationMessage = ''; // ✅ Clear previous messages
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

  // ✅ NEW: Make Reservation Function
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
        
        // Clear message after 5 seconds
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

  // Navigate to messages page with the guide's userId as a query parameter
  this.router.navigate(['/messages'], { 
    queryParams: { userId: this.selectedTour.guide.id }
  });
}
}