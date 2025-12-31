import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Subject, takeUntil } from 'rxjs';
import { Navbar } from '../../components/navbar/navbar';
import { Footer } from '../../components/footer/footer';
import { FavoritesService } from '../../services/favorites.service';
import { GalleryFilters, PortalService } from '../../services/portal.service';
import { Place, Review } from '../../services/admin.service';
import { authService } from '../../services/auth';

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [CommonModule, FormsModule, Navbar ,Footer],
  templateUrl: './explore.html',
  styleUrl: './explore.css'
})
export class ExplorePage implements OnInit, OnDestroy {
  places: Place[] = [];
  filteredPlaces: Place[] = [];
  selectedPlace: Place | null = null;
  selectedPlaceReviews: Review[] = [];
  categories: string[] = [];
  cities: string[] = [];
  favoriteIds: number[] = [];
  
  filters: GalleryFilters = {
    category: '',
    city: '',
    popularity: 'desc',
    search: ''
  };
  
  loading = true;
  
  // ✅ NEW: Review modal state
  showReviewModal = false;
  newReview = {
    rating: 0,
    comment: ''
  };
  submittingReview = false;
  reviewError = '';
  reviewSuccess = '';
  
  private destroy$ = new Subject<void>();
  private apiUrl = 'http://tuniway.duckdns.org:8083/api/reviews';

  constructor(
    private readonly portalService: PortalService,
    private readonly favoritesService: FavoritesService,
    private readonly authService: authService,
    private readonly http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadFilters();
    this.favoritesService.favorites$
      .pipe(takeUntil(this.destroy$))
      .subscribe(ids => (this.favoriteIds = ids));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  toggleFavorite(place: Place): void {
    if (place.id) {
      this.favoritesService.toggleFavorite(place.id);
    }
  }

  isFavorite(place: Place): boolean {
    return place.id ? this.favoriteIds.includes(place.id) : false;
  }

  selectPlace(place: Place): void {
    this.selectedPlace = place;
    if (place.id) {
      this.portalService
        .getPlaceReviews(place.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe(reviews => (this.selectedPlaceReviews = reviews));
    } else {
      this.selectedPlaceReviews = [];
    }
  }

  clearSelection(): void {
    this.selectedPlace = null;
    this.selectedPlaceReviews = [];
  }

  applyFilters(): void {
    this.filteredPlaces = this.places
      .filter(place => {
        const matchesCategory = this.filters.category ? place.category === this.filters.category : true;
        const matchesCity = this.filters.city ? place.city === this.filters.city : true;
        const searchTerm = this.filters.search?.toLowerCase() || '';
        const matchesSearch = place.name.toLowerCase().includes(searchTerm);
        return matchesCategory && matchesCity && matchesSearch;
      })
      .sort((a, b) => {
        const weightA = a.id || 0;
        const weightB = b.id || 0;
        return this.filters.popularity === 'asc' ? weightA - weightB : weightB - weightA;
      });
    
    if (this.selectedPlace) {
      const exists = this.filteredPlaces.some(place => place.id === this.selectedPlace?.id);
      if (!exists) {
        this.clearSelection();
      }
    }
  }

  // ✅ NEW: Open review modal
  openReviewModal(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      alert('Please login to write a review');
      return;
    }
    
    this.showReviewModal = true;
    this.newReview = { rating: 0, comment: '' };
    this.reviewError = '';
    this.reviewSuccess = '';
  }

  // ✅ NEW: Close review modal
  closeReviewModal(): void {
    this.showReviewModal = false;
    this.newReview = { rating: 0, comment: '' };
    this.reviewError = '';
    this.reviewSuccess = '';
  }

  // ✅ NEW: Set rating
  setRating(rating: number): void {
    this.newReview.rating = rating;
  }

  // ✅ NEW: Submit review
  submitReview(): void {
    if (!this.selectedPlace || !this.selectedPlace.id) {
      this.reviewError = 'No place selected';
      return;
    }

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.reviewError = 'Please login to submit a review';
      return;
    }

    if (!this.newReview.rating || !this.newReview.comment.trim()) {
      this.reviewError = 'Please provide both rating and comment';
      return;
    }

    this.submittingReview = true;
    this.reviewError = '';
    this.reviewSuccess = '';

    const reviewData = {
      placeId: this.selectedPlace.id,
      userId: currentUser.id,
      rating: this.newReview.rating,
      comment: this.newReview.comment.trim()
    };

    this.http.post<Review>(this.apiUrl, reviewData).subscribe({
      next: (review) => {
        console.log('✅ Review submitted:', review);
        this.reviewSuccess = 'Review submitted successfully!';
        this.submittingReview = false;
        
        // Add the new review to the list
        this.selectedPlaceReviews.unshift(review);
        
        // Close modal after 2 seconds
        setTimeout(() => {
          this.closeReviewModal();
        }, 2000);
      },
      error: (error) => {
        console.error('❌ Error submitting review:', error);
        this.reviewError = error.error || 'Failed to submit review. Please try again.';
        this.submittingReview = false;
      }
    });
  }

  private loadFilters(): void {
    this.portalService
      .getPlacesGallery()
      .pipe(takeUntil(this.destroy$))
      .subscribe(places => {
        this.places = places;
        this.cities = Array.from(new Set(places.map(place => place.city)));
        this.applyFilters();
        this.loading = false;
      });
    
    this.portalService
      .getPlaceCategories()
      .pipe(takeUntil(this.destroy$))
      .subscribe(categories => (this.categories = categories));
  }
}
