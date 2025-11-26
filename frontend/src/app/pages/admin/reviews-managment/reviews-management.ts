import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Review } from '../../../services/admin.service';

@Component({
  selector: 'app-reviews-management',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './reviews-management.html',
  styleUrl: './reviews-management.css'
})
export class ReviewsManagement implements OnInit {
  reviews: Review[] = [];
  loading = false;
  searchTerm = '';

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadReviews();
  }

  loadReviews() {
    this.loading = true;
    this.adminService.getAllReviews().subscribe({
      next: (reviews) => {
        this.reviews = reviews;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading reviews:', err);
        this.loading = false;
      }
    });
  }

  deleteReview(id: number | undefined) {
    if (!id) return;
    
    if (confirm('Are you sure you want to delete this review? This action cannot be undone.')) {
      this.adminService.deleteReview(id).subscribe({
        next: () => {
          this.loadReviews();
        },
        error: (err) => {
          console.error('Error deleting review:', err);
          alert('Error deleting review');
        }
      });
    }
  }

  acceptReview(review: Review) {
    // Reviews are accepted by default, this is just for UI purposes
    alert('Review is already visible to users');
  }

  get filteredReviews() {
    if (!this.searchTerm) return this.reviews;
    const term = this.searchTerm.toLowerCase();
    return this.reviews.filter(r => 
      r.comment?.toLowerCase().includes(term) ||
      r.place?.name.toLowerCase().includes(term) ||
      r.user?.username.toLowerCase().includes(term)
    );
  }

  getStars(rating: number) {
    return Array(rating).fill(0);
  }
}

