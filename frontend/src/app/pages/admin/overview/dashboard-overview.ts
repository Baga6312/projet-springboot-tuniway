import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminService, DashboardStats } from '../../../services/admin.service';

@Component({
  selector: 'app-dashboard-overview',
  standalone: true,
  imports: [CommonModule ],
  templateUrl: './dashboard-overview.html',
  styleUrl: './dashboard-overview.css'
})
export class DashboardOverview implements OnInit {
  stats: DashboardStats = {
    totalUsers: 0,
    totalPlaces: 0,
    totalReservations: 0,
    totalReviews: 0,
    totalTours: 0,
    totalGuides: 0,
    totalClients: 0
  };
  loading = true;

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    this.loading = true;
    this.adminService.getDashboardStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading stats:', err);
        this.loading = false;
      }
    });
  }
}

