import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Home } from './pages/home/home';
import { AdminDashboard } from './pages/admin/dashboard/admin-dashboard';
import { DashboardOverview } from './pages/admin/overview/dashboard-overview';
import { UsersManagement } from './pages/admin/users-management/users-management';
import { GuidesManagement } from './pages/admin/guide-managment/guides-management';
import { ReviewsManagement } from './pages/admin/reviews-managment/reviews-management';
import { ToursManagement } from './pages/admin/tours-managment/tours-management';
import { PlacesManagement } from './pages/admin/places-managment/places-management';
import { UserProfile } from './pages/profile/user-profile/user-profile';
import { GuideProfile } from './pages/profile/guide-profile/guide-profile';
import { ExplorePage } from './pages/explore/explore';
import { ToursPage } from './pages/tours/tours';

export const routes: Routes = [
  { path: '', component: Home }, // Homepage
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'profile', component: UserProfile },
  { path: 'guide/profile', component: GuideProfile },
  { path: 'explore', component: ExplorePage },
  { path: 'tours', component: ToursPage },

  // Admin routes (nested)
  {
    path: 'admin',
    component: AdminDashboard,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' }, // /admin redirects to /admin/overview
      { path: 'overview', component: DashboardOverview },
      { path: 'places', component: PlacesManagement },
      { path: 'users', component: UsersManagement },
      { path: 'guides', component: GuidesManagement },
      { path: 'reviews', component: ReviewsManagement },
      { path: 'tours', component: ToursManagement }
    ]
  },

  // Catch-all redirect to homepage
  { path: '**', redirectTo: '' }
];
