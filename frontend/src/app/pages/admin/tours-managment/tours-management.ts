import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Tour } from '../../../services/admin.service';

@Component({
  selector: 'app-tours-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tours-management.html',
  styleUrl: './tours-management.css'
})
export class ToursManagement implements OnInit {
  tours: Tour[] = [];
  loading = false;
  searchTerm = '';

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadTours();
  }

  loadTours() {
    this.loading = true;
    this.adminService.getAllTours().subscribe({
      next: (tours) => {
        this.tours = tours;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading tours:', err);
        this.loading = false;
      }
    });
  }

  deleteTour(id: number | undefined) {
    if (!id) return;
    
    if (confirm('Are you sure you want to delete this tour?')) {
      this.adminService.deleteTour(id).subscribe({
        next: () => {
          this.loadTours();
        },
        error: (err) => {
          console.error('Error deleting tour:', err);
          alert('Error deleting tour');
        }
      });
    }
  }

  get filteredTours() {
    if (!this.searchTerm) return this.tours;
    const term = this.searchTerm.toLowerCase();
    return this.tours.filter(t => 
      t.titre.toLowerCase().includes(term) ||
      t.description?.toLowerCase().includes(term) ||
      t.guide?.username.toLowerCase().includes(term) ||
      t.client?.username.toLowerCase().includes(term)
    );
  }
}

