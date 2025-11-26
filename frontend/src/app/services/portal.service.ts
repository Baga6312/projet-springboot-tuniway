import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { Place, Review, Tour, User } from './admin.service';

export type ReservationStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface Reservation {
  id?: number;
  status?: ReservationStatus;
  startDate?: string;
  endDate?: string;
  createdAt?: string;
  totalPrice?: number;
  place?: Place;
  tour?: Tour;
  client?: User;
  guide?: User;
}

export interface GalleryFilters {
  category?: string;
  city?: string;
  popularity?: 'asc' | 'desc';
  search?: string;
}

export interface TourPayload {
  titre: string;
  description?: string;
  prix: number;
  date?: string;
  guideId: number;
}

@Injectable({
  providedIn: 'root'
})
export class PortalService {
  constructor(private http: HttpClient) {}

  getUserProfile(userId: number): Observable<User> {
    return this.http.get<User>(`${API_BASE_URL}/users/${userId}`).pipe(
      catchError(this.handleError<User>(this.createEmptyUser(userId)))
    );
  }

  updateUserProfile(userId: number, payload: Partial<User>): Observable<User> {
    return this.http.put<User>(`${API_BASE_URL}/users/${userId}`, payload).pipe(
      catchError(this.handleError<User>({ ...payload, id: userId } as User))
    );
  }

  getUserReservations(userId: number): Observable<Reservation[]> {
    return this.http
      .get<Reservation[]>(`${API_BASE_URL}/reservations/client/${userId}`)
      .pipe(catchError(this.handleError<Reservation[]>([])));
  }

  getUserReviews(userId: number): Observable<Review[]> {
    return this.http
      .get<Review[]>(`${API_BASE_URL}/reviews/user/${userId}`)
      .pipe(catchError(this.handleError<Review[]>([])));
  }

  getGuideReservations(guideId: number): Observable<Reservation[]> {
    return this.http
      .get<Reservation[]>(`${API_BASE_URL}/reservations/guide/${guideId}`)
      .pipe(catchError(this.handleError<Reservation[]>([])));
  }

  getGuideTours(guideId: number): Observable<Tour[]> {
    return this.http
      .get<Tour[]>(`${API_BASE_URL}/tours/guide/${guideId}`)
      .pipe(catchError(this.handleError<Tour[]>([])));
  }

  createTour(payload: TourPayload): Observable<Tour> {
    const body = {
      titre: payload.titre,
      description: payload.description,
      prix: payload.prix,
      date: payload.date,
      guideId: payload.guideId
    };

    return this.http.post<Tour>(`${API_BASE_URL}/tours`, body).pipe(
      catchError(this.handleError<Tour>({
        id: Date.now(),
        titre: payload.titre,
        description: payload.description,
        prix: payload.prix,
        date: payload.date
      }))
    );
  }

  deleteTour(tourId: number): Observable<void> {
    return this.http
      .delete<void>(`${API_BASE_URL}/tours/${tourId}`)
      .pipe(catchError(this.handleError<void>(undefined)));
  }

  getAllTours(): Observable<Tour[]> {
    return this.http.get<Tour[]>(`${API_BASE_URL}/tours`).pipe(
      catchError(this.handleError<Tour[]>([]))
    );
  }

  getPlacesGallery(): Observable<Place[]> {
    return this.http.get<Place[]>(`${API_BASE_URL}/places`).pipe(
      catchError(this.handleError<Place[]>([]))
    );
  }

  getPlacesByCategory(category: string): Observable<Place[]> {
    return this.http
      .get<Place[]>(`${API_BASE_URL}/places/category/${category}`)
      .pipe(catchError(this.handleError<Place[]>([])));
  }

  getPlacesByCity(city: string): Observable<Place[]> {
    return this.http
      .get<Place[]>(`${API_BASE_URL}/places/city/${city}`)
      .pipe(catchError(this.handleError<Place[]>([])));
  }

  searchPlaces(term: string): Observable<Place[]> {
    return this.http
      .get<Place[]>(`${API_BASE_URL}/places/search/${term}`)
      .pipe(catchError(this.handleError<Place[]>([])));
  }

  getPlaceCategories(): Observable<string[]> {
    return this.http
      .get<string[]>(`${API_BASE_URL}/places/categories`)
      .pipe(catchError(this.handleError<string[]>([])));
  }

  getPlaceReviews(placeId: number): Observable<Review[]> {
    return this.http
      .get<Review[]>(`${API_BASE_URL}/reviews/place/${placeId}`)
      .pipe(catchError(this.handleError<Review[]>([])));
  }

  getTourReviews(tourId: number): Observable<Review[]> {
    // Backend does not expose dedicated tour review endpoint yet.
    // Fallback to reviews filtered client-side by the tour place name if available.
    return this.http.get<Review[]>(`${API_BASE_URL}/reviews`).pipe(
      map(reviews =>
        reviews.filter(review => review.place && review.place.name?.includes('Tour'))
      ),
      catchError(this.handleError<Review[]>([]))
    );
  }

  getPopularPlaces(limit = 6): Observable<Place[]> {
    return this.getPlacesGallery().pipe(
      map(places =>
        [...places].sort((a, b) => (b.id || 0) - (a.id || 0)).slice(0, limit)
      )
    );
  }

  private handleError<T>(fallbackValue: T) {
    return (error: unknown): Observable<T> => {
      console.warn('[PortalService] API call failed, using fallback value.', error);
      return of(fallbackValue);
    };
  }

  private createEmptyUser(id: number): User {
    return {
      id,
      username: 'Guest User',
      email: 'guest@tuniway.tn',
      role: 'CLIENT',
      profilePicture: ''
    };
  }
}


