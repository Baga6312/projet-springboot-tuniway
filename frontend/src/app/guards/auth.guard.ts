import { inject, PLATFORM_ID } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  console.log('🔐 Auth Guard Running...');
  console.log('  - Platform is browser?', isPlatformBrowser(platformId));
  console.log('  - Route:', state.url);

  // Check if we're in browser
  if (!isPlatformBrowser(platformId)) {
    console.log('⚠️ Running on SERVER - redirecting to login');
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  // Get data from localStorage
  const token = localStorage.getItem('jwtToken');
  const userStr = localStorage.getItem('currentUser');

  console.log('  - Token exists:', !!token);
  console.log('  - Token value:', token ? token.substring(0, 20) + '...' : 'null');
  console.log('  - User exists:', !!userStr);
  console.log('  - User value:', userStr);

  if (token && userStr) {
    try {
      const user = JSON.parse(userStr);
      console.log('✅ Auth guard PASSED - User:', user.username);
      return true;
    } catch (e) {
      console.error('❌ Failed to parse user data:', e);
      console.error('   User string was:', userStr);
      localStorage.removeItem('currentUser');
      localStorage.removeItem('jwtToken');
    }
  } else {
    console.log('❌ Missing token or user:');
    console.log('   - token is', token ? 'present' : 'missing');
    console.log('   - user is', userStr ? 'present' : 'missing');
  }

  console.log('❌ Auth guard FAILED - Redirecting to login');
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};