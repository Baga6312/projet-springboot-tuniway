import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  private readonly STORAGE_KEY = 'tuniway-favorites';
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
    if (this.isBrowser()) {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(nextState));
    }
    this.favoritesSubject.next(nextState);
  }

  private readFromStorage(): number[] {
    if (!this.isBrowser()) {
      return [];
    }
    const raw = localStorage.getItem(this.STORAGE_KEY);
    try {
      return raw ? (JSON.parse(raw) as number[]) : [];
    } catch {
      return [];
    }
  }

  private isBrowser(): boolean {
    return typeof window !== 'undefined';
  }
}


