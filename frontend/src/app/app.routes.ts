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
import { Messages } from './pages/messages/messages';
import { OAuth2RedirectComponent } from './pages/oauth2-redirect/oauth2-redirect.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'explore', component: ExplorePage },
  { path: 'tours', component: ToursPage },
  { path: 'oauth2/redirect', component: OAuth2RedirectComponent },
  { path: 'profile', component: UserProfile, canActivate: [authGuard] },
  { path: 'guide/profile', component: GuideProfile, canActivate: [authGuard], },
  { path: 'messages',  component: Messages, canActivate: [authGuard] },

  { path: 'admin', component: AdminDashboard,
    canActivate: [authGuard], children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: DashboardOverview },
      { path: 'places', component: PlacesManagement },
      { path: 'users', component: UsersManagement },
      { path: 'guides', component: GuidesManagement },
      { path: 'reviews', component: ReviewsManagement },
      { path: 'tours', component: ToursManagement },
    ],
  },

  { path: '**', redirectTo: '' },
];
