import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService, User, Tour, Review } from '../../../services/admin.service';

@Component({
  selector: 'app-guides-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './guides-management.html',
  styleUrl: './guides-management.css'
})
export class GuidesManagement implements OnInit {
  guides: User[] = [];
  loading = false;
  selectedGuide: User | null = null;
  guideTours: Tour[] = [];
  guideReviews: Review[] = [];
  showDetails = false;

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadGuides();
  }

  loadGuides() {
    this.loading = true;
    this.adminService.getAllGuides().subscribe({
      next: (users) => {
        this.guides = users.filter(u => u.role === 'GUIDE');
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading guides:', err);
        this.loading = false;
      }
    });
  }

  viewGuideDetails(guide: User) {
    this.selectedGuide = guide;
    this.showDetails = true;
    this.loadGuideTours(guide.id!);
    this.loadGuideReviews(guide.id!);
  }

  loadGuideTours(guideId: number) {
    this.adminService.getToursByGuide(guideId).subscribe({
      next: (tours) => {
        this.guideTours = tours;
      },
      error: (err) => {
        console.error('Error loading guide tours:', err);
      }
    });
  }

  loadGuideReviews(guideId: number) {
    this.adminService.getReviewsByUser(guideId).subscribe({
      next: (reviews) => {
        this.guideReviews = reviews;
      },
      error: (err) => {
        console.error('Error loading guide reviews:', err);
      }
    });
  }

  closeDetails() {
    this.showDetails = false;
    this.selectedGuide = null;
    this.guideTours = [];
    this.guideReviews = [];
  }

  deleteGuide(id: number | undefined) {
    if (!id) return;
    
    if (confirm('Are you sure you want to delete this guide? This will also delete all their tours.')) {
      this.adminService.deleteUser(id).subscribe({
        next: () => {
          this.loadGuides();
          if (this.selectedGuide?.id === id) {
            this.closeDetails();
          }
        },
        error: (err) => {
          console.error('Error deleting guide:', err);
          alert('Error deleting guide. Guide may have associated data.');
        }
      });
    }
  }

  getStars(rating: number) {
    return Array(rating).fill(0);
  }
}

