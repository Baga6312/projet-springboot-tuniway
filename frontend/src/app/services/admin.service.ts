import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';

export interface Place {
  id?: number;
  name: string;
  description: string;
  category: string;
  city: string;
  imageUrl?: string;
  latitude?: number;
  longitude?: number;
}

export interface User {
  id?: number;
  username: string;
  email: string;
  password?: string;
  role: 'CLIENT' | 'GUIDE' | 'ADMIN';
  profilePicture?: string;
  bio?: string;
  languages?: string[];
  availability?: string;
}

export interface Review {
  id?: number;
  rating: number;
  comment: string;
  datePosted?: string;
  place?: Place;
  user?: User;
}

export interface Tour {
  id?: number;
  titre: string;
  description?: string;
  prix: number;
  date?: string;
  guide?: User;
  client?: User;
}

export interface DashboardStats {
  totalUsers: number;
  totalPlaces: number;
  totalReservations: number;
  totalReviews: number;
  totalTours: number;
  totalGuides: number;
  totalClients: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private headers = new HttpHeaders({
    'Content-Type': 'application/json'
  });

  constructor(private http: HttpClient) {}

  // Dashboard Statistics
  getDashboardStats(): Observable<DashboardStats> {
    // Since backend doesn't have a stats endpoint, we'll calculate from multiple calls
    // For now, return a combined observable
    return new Observable(observer => {
      Promise.all([
        this.getAllUsers().toPromise(),
        this.getAllPlaces().toPromise(),
        this.getAllReservations().toPromise(),
        this.getAllReviews().toPromise(),
        this.getAllTours().toPromise()
      ]).then(([users, places, reservations, reviews, tours]: any) => {
        const stats: DashboardStats = {
          totalUsers: users?.length || 0,
          totalPlaces: places?.length || 0,
          totalReservations: reservations?.length || 0,
          totalReviews: reviews?.length || 0,
          totalTours: tours?.length || 0,
          totalGuides: users?.filter((u: User) => u.role === 'GUIDE').length || 0,
          totalClients: users?.filter((u: User) => u.role === 'CLIENT').length || 0
        };
        observer.next(stats);
        observer.complete();
      }).catch(err => observer.error(err));
    });
  }

  // Places CRUD
  getAllPlaces(): Observable<Place[]> {
    return this.http.get<Place[]>(`${API_BASE_URL}/places`);
  }

  getPlaceById(id: number): Observable<Place> {
    return this.http.get<Place>(`${API_BASE_URL}/places/${id}`);
  }

  createPlace(place: Place): Observable<Place> {
    return this.http.post<Place>(`${API_BASE_URL}/places`, place, { headers: this.headers });
  }

  updatePlace(id: number, place: Place): Observable<Place> {
    return this.http.put<Place>(`${API_BASE_URL}/places/${id}`, place, { headers: this.headers });
  }

  deletePlace(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/places/${id}`);
  }

  // Users Management
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${API_BASE_URL}/users`);
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${API_BASE_URL}/users/${id}`);
  }

  updateUser(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${API_BASE_URL}/users/${id}`, user, { headers: this.headers });
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/users/${id}`);
  }

  // Reviews Management
  getAllReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(`${API_BASE_URL}/reviews`);
  }

  getReviewById(id: number): Observable<Review> {
    return this.http.get<Review>(`${API_BASE_URL}/reviews/${id}`);
  }

  deleteReview(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/reviews/${id}`);
  }

  getReviewsByUser(userId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${API_BASE_URL}/reviews/user/${userId}`);
  }

  // Guides Management
  getAllGuides(): Observable<User[]> {
    return this.http.get<User[]>(`${API_BASE_URL}/users`);
  }

  getGuideById(id: number): Observable<User> {
    return this.http.get<User>(`${API_BASE_URL}/users/${id}`);
  }

  // Tours Management
  getAllTours(): Observable<Tour[]> {
    return this.http.get<Tour[]>(`${API_BASE_URL}/tours`);
  }

  getTourById(id: number): Observable<Tour> {
    return this.http.get<Tour>(`${API_BASE_URL}/tours/${id}`);
  }

  getToursByGuide(guideId: number): Observable<Tour[]> {
    return this.http.get<Tour[]>(`${API_BASE_URL}/tours/guide/${guideId}`);
  }

  deleteTour(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/tours/${id}`);
  }

  // Reservations
  getAllReservations(): Observable<any[]> {
    return this.http.get<any[]>(`${API_BASE_URL}/reservations`);
  }
}

