import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Place } from '../../../services/admin.service';

@Component({
  selector: 'app-places-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './places-management.html',
  styleUrl: './places-management.css'
})
export class PlacesManagement implements OnInit {
  places: Place[] = [];
  loading = false;
  showModal = false;
  editingPlace: Place | null = null;
  placeForm: Place = {
    name: '',
    description: '',
    category: 'HISTORICAL',
    city: '',
    imageUrl: '',
    latitude: 0,
    longitude: 0
  };
  categories = ['HISTORICAL', 'BEACH', 'RESTAURANT', 'HOTEL', 'MUSEUM', 'NATURE', 'SHOPPING', 'ENTERTAINMENT'];
  searchTerm = '';

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadPlaces();
  }

  loadPlaces() {
    this.loading = true;
    this.adminService.getAllPlaces().subscribe({
      next: (places) => {
        this.places = places;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading places:', err);
        this.loading = false;
      }
    });
  }

  openAddModal() {
    this.editingPlace = null;
    this.placeForm = {
      name: '',
      description: '',
      category: 'HISTORICAL',
      city: '',
      imageUrl: '',
      latitude: 0,
      longitude: 0
    };
    this.showModal = true;
  }

  openEditModal(place: Place) {
    this.editingPlace = place;
    this.placeForm = { ...place };
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.editingPlace = null;
  }

  savePlace() {
    if (!this.placeForm.name || !this.placeForm.city) {
      alert('Please fill in required fields (Name and City)');
      return;
    }

    if (this.editingPlace && this.editingPlace.id) {
      // Update
      this.adminService.updatePlace(this.editingPlace.id, this.placeForm).subscribe({
        next: () => {
          this.loadPlaces();
          this.closeModal();
        },
        error: (err) => {
          console.error('Error updating place:', err);
          alert('Error updating place');
        }
      });
    } else {
      // Create
      this.adminService.createPlace(this.placeForm).subscribe({
        next: () => {
          this.loadPlaces();
          this.closeModal();
        },
        error: (err) => {
          console.error('Error creating place:', err);
          alert('Error creating place');
        }
      });
    }
  }

  deletePlace(id: number | undefined) {
    if (!id) return;
    
    if (confirm('Are you sure you want to delete this place?')) {
      this.adminService.deletePlace(id).subscribe({
        next: () => {
          this.loadPlaces();
        },
        error: (err) => {
          console.error('Error deleting place:', err);
          alert('Error deleting place. It may have associated reviews.');
        }
      });
    }
  }

  get filteredPlaces() {
    if (!this.searchTerm) return this.places;
    const term = this.searchTerm.toLowerCase();
    return this.places.filter(p => 
      p.name.toLowerCase().includes(term) ||
      p.city.toLowerCase().includes(term) ||
      p.category.toLowerCase().includes(term)
    );
  }
}


