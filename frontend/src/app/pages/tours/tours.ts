import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

import { Navbar } from '../../components/navbar/navbar';
import { Footer } from '../../components/footer/footer';
import { PortalService } from '../../services/portal.service';
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

  private destroy$ = new Subject<void>();

  constructor(private readonly portalService: PortalService) {}

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
  }

  contactGuide(): void {
    if (this.selectedTour?.guide?.email && typeof window !== 'undefined') {
      window.location.href = `mailto:${this.selectedTour.guide.email}?subject=Tour inquiry`;
    }
  }
}


