import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, User } from '../../../services/admin.service';

@Component({
  selector: 'app-users-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users-management.html',
  styleUrl: './users-management.css'
})
export class UsersManagement implements OnInit {
  users: User[] = [];
  loading = false;
  showModal = false;
  editingUser: User | null = null;
  userForm: User = {
    username: '',
    email: '',
    role: 'CLIENT',
    profilePicture: ''
  };
  searchTerm = '';
  roleFilter: 'ALL' | 'CLIENT' | 'GUIDE' | 'ADMIN' = 'ALL';

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.loading = false;
      }
    });
  }

  openEditModal(user: User) {
    this.editingUser = user;
    this.userForm = { ...user };
    delete this.userForm.password; // Don't show password
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.editingUser = null;
  }

  saveUser() {
    if (!this.editingUser || !this.editingUser.id) return;

    this.adminService.updateUser(this.editingUser.id, this.userForm).subscribe({
      next: () => {
        this.loadUsers();
        this.closeModal();
      },
      error: (err) => {
        console.error('Error updating user:', err);
        alert('Error updating user');
      }
    });
  }

  deleteUser(id: number | undefined) {
    if (!id) return;
    
    if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
      this.adminService.deleteUser(id).subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (err) => {
          console.error('Error deleting user:', err);
          alert('Error deleting user. User may have associated data (reviews, reservations, tours).');
        }
      });
    }
  }

  deactivateUser(user: User) {
    // TODO: Implement deactivation logic when backend supports it
    alert('Deactivation feature will be implemented when backend supports it');
  }

  get filteredUsers() {
    let filtered = this.users;
    
    if (this.roleFilter !== 'ALL') {
      filtered = filtered.filter(u => u.role === this.roleFilter);
    }
    
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(u => 
        u.username.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term)
      );
    }
    
    return filtered;
  }

  getRoleBadgeClass(role: string) {
    switch(role) {
      case 'ADMIN': return 'bg-red-100 text-red-800';
      case 'GUIDE': return 'bg-blue-100 text-blue-800';
      case 'CLIENT': return 'bg-green-100 text-green-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }
}


