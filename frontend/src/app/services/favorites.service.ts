import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { authService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  private readonly apiUrl = 'http://tuniway.duckdns.org:8083/api/favorites';
  private platformId = inject(PLATFORM_ID);
  private favoritesSubject = new BehaviorSubject<number[]>([]);
  favorites$ = this.favoritesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: authService
  ) {
    // Load favorites from backend when service initializes
    this.loadFavorites();
  }

  // Load all favorites from backend
  loadFavorites(): void {
    if (!this.authService.isLoggedIn()) {
      this.favoritesSubject.next([]);
      return;
    }

    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any[]>(`${this.apiUrl}`, { headers }).subscribe({
      next: (places) => {
        const favoriteIds = places.map(place => place.id);
        this.favoritesSubject.next(favoriteIds);
        console.log('✅ Loaded favorites from backend:', favoriteIds);
      },
      error: (error) => {
        console.error('❌ Error loading favorites:', error);
        this.favoritesSubject.next([]);
      }
    });
  }

  // Toggle favorite (add or remove)
  toggleFavorite(placeId: number): void {
    const favorites = this.favoritesSubject.value;
    if (favorites.includes(placeId)) {
      this.removeFavorite(placeId);
    } else {
      this.addFavorite(placeId);
    }
  }

  // Add place to favorites
  addFavorite(placeId: number): void {
    if (!this.authService.isLoggedIn()) {
      console.error('User not logged in');
      return;
    }

    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.post(`${this.apiUrl}/${placeId}`, {}, { headers }).subscribe({
      next: () => {
        const updated = [...this.favoritesSubject.value, placeId];
        this.favoritesSubject.next(updated);
        console.log('✅ Added to favorites:', placeId);
      },
      error: (error) => {
        console.error('❌ Error adding favorite:', error);
      }
    });
  }

  // Remove place from favorites
  removeFavorite(placeId: number): void {
    if (!this.authService.isLoggedIn()) {
      console.error('User not logged in');
      return;
    }

    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete(`${this.apiUrl}/${placeId}`, { headers }).subscribe({
      next: () => {
        const updated = this.favoritesSubject.value.filter(id => id !== placeId);
        this.favoritesSubject.next(updated);
        console.log('✅ Removed from favorites:', placeId);
      },
      error: (error) => {
        console.error('❌ Error removing favorite:', error);
      }
    });
  }

  // Check if place is favorite
  isFavorite(placeId: number): boolean {
    return this.favoritesSubject.value.includes(placeId);
  }

  // Check favorite status from backend
  checkFavorite(placeId: number): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get(`${this.apiUrl}/check/${placeId}`, { headers });
  }

  // Clear all favorites (logout)
  clear(): void {
    this.favoritesSubject.next([]);
  }
}
