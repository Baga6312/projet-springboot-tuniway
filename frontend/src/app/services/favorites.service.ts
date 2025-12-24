import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  private readonly STORAGE_KEY = 'tuniway-favorites';
  private platformId = inject(PLATFORM_ID);
  private favoritesSubject = new BehaviorSubject<number[]>(this.readFromStorage());
  favorites$ = this.favoritesSubject.asObservable();

  toggleFavorite(placeId: number): void {
    const favorites = new Set(this.favoritesSubject.value);
    if (favorites.has(placeId)) {
      favorites.delete(placeId);
    } else {
      favorites.add(placeId);
    }
    this.update(Array.from(favorites));
  }

  addFavorite(placeId: number): void {
    if (!this.isFavorite(placeId)) {
      this.update([...this.favoritesSubject.value, placeId]);
    }
  }

  removeFavorite(placeId: number): void {
    this.update(this.favoritesSubject.value.filter(id => id !== placeId));
  }

  isFavorite(placeId: number): boolean {
    return this.favoritesSubject.value.includes(placeId);
  }

  clear(): void {
    this.update([]);
  }

  private update(nextState: number[]): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(nextState));
    }
    this.favoritesSubject.next(nextState);
  }

  private readFromStorage(): number[] {
    if (!isPlatformBrowser(this.platformId)) {
      return [];
    }
    const raw = localStorage.getItem(this.STORAGE_KEY);
    try {
      return raw ? (JSON.parse(raw) as number[]) : [];
    } catch {
      return [];
    }
  }
}