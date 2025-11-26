import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil, take } from 'rxjs';

import { Navbar } from '../../../components/navbar/navbar';
import { Footer } from '../../../components/footer/footer';
import { AuthService } from '../../../services/auth.service';
import { FavoritesService } from '../../../services/favorites.service';
import { PortalService, Reservation } from '../../../services/portal.service';
import { Place, Review, User } from '../../../services/admin.service';

type ProfileTab = 'overview' | 'reservations' | 'reviews' | 'favorites';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, Navbar, Footer],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css'
})
export class UserProfile implements OnInit, OnDestroy {
  activeTab: ProfileTab = 'overview';
  currentUser: User | null = null;
  profileForm: Partial<User> = {};
  reservations: Reservation[] = [];
  reviews: Review[] = [];
  favoritePlaces: Place[] = [];
  loadingProfile = true;
  savingProfile = false;
  statusMessage = '';

  private destroy$ = new Subject<void>();

  constructor(
    private readonly portalService: PortalService,
    private readonly favoritesService: FavoritesService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.bootstrapUser();
    this.listenToFavorites();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  setActiveTab(tab: ProfileTab): void {
    this.activeTab = tab;
  }

  saveProfile(): void {
    if (!this.currentUser?.id) {
      return;
    }

    this.savingProfile = true;
    this.portalService.updateUserProfile(this.currentUser.id, this.profileForm).pipe(takeUntil(this.destroy$))
      .subscribe(updated => {
        this.currentUser = updated;
        this.authService.updateCurrentUser(updated);
        this.savingProfile = false;
        this.statusMessage = 'Profile updated successfully.';
        setTimeout(() => (this.statusMessage = ''), 4000);
      });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  removeFavorite(placeId?: number): void {
    if (placeId) {
      this.favoritesService.removeFavorite(placeId);
    }
  }

  refreshData(): void {
    if (!this.currentUser?.id) {
      return;
    }
    this.loadReservations(this.currentUser.id);
    this.loadReviews(this.currentUser.id);
    this.reloadFavorites();
  }

  private bootstrapUser(): void {
    const fallbackUser: User = {
      id: 1,
      username: 'Guest Explorer',
      email: 'guest@tuniway.tn',
      role: 'CLIENT',
      profilePicture: ''
    };

    const snapshot = this.authService.getCurrentUser() ?? fallbackUser;
    if (!this.authService.getCurrentUser()) {
      this.authService.setCurrentUser(snapshot);
    }

    this.currentUser = snapshot;
    this.profileForm = { ...snapshot };

    if (snapshot.id) {
      this.portalService.getUserProfile(snapshot.id).pipe(takeUntil(this.destroy$)).subscribe(user => {
        this.currentUser = user;
        this.profileForm = { ...user };
        this.authService.updateCurrentUser(user);
        this.loadingProfile = false;
      });
      this.loadReservations(snapshot.id);
      this.loadReviews(snapshot.id);
      this.reloadFavorites();
    } else {
      this.loadingProfile = false;
    }
  }

  private loadReservations(userId: number): void {
    this.portalService
      .getUserReservations(userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => (this.reservations = data));
  }

  private loadReviews(userId: number): void {
    this.portalService
      .getUserReviews(userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => (this.reviews = data));
  }

  private reloadFavorites(): void {
    this.favoritesService.favorites$.pipe(take(1)).subscribe(ids => {
      if (!ids.length) {
        this.favoritePlaces = [];
        return;
      }
      this.portalService
        .getPlacesGallery()
        .pipe(take(1))
        .subscribe(places => {
          this.favoritePlaces = places.filter(place => place.id && ids.includes(place.id));
        });
    });
  }

  private listenToFavorites(): void {
    this.favoritesService.favorites$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.reloadFavorites());
  }

  get averageReviewScore(): number | null {
    if (!this.reviews.length) {
      return null;
    }
    const sum = this.reviews.reduce((acc, review) => acc + (review.rating || 0), 0);
    return sum / this.reviews.length;
  }
}


