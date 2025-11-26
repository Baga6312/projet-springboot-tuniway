import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],  // <-- FIXED
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboard {
  activeSection = 'dashboard';

  constructor(private router: Router) {}

  setActiveSection(section: string) {
    this.activeSection = section;
  }

  logout() {
    // TODO: Implement logout logic
    this.router.navigate(['/']);
  }
}
