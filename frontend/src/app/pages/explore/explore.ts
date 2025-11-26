import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

import { Navbar } from '../../components/navbar/navbar';
import { Footer } from '../../components/footer/footer';
import { FavoritesService } from '../../services/favorites.service';
import { GalleryFilters, PortalService } from '../../services/portal.service';
import { Place, Review } from '../../services/admin.service';

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [CommonModule, FormsModule, Navbar, Footer],
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

  private destroy$ = new Subject<void>();

  constructor(
    private readonly portalService: PortalService,
    private readonly favoritesService: FavoritesService
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


