import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Disable SSR for authenticated routes (client-side only)
  {
    path: 'profile',
    renderMode: RenderMode.Client
  },
  {
    path: 'guide/profile',
    renderMode: RenderMode.Client
  },
  {
    path: 'admin/**',
    renderMode: RenderMode.Client
  },
  // Enable SSR for public routes
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];