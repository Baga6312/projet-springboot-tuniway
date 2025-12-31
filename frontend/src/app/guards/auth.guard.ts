import { inject, PLATFORM_ID } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);


  if (!isPlatformBrowser(platformId)) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  const token = localStorage.getItem('jwtToken');
  const userStr = localStorage.getItem('currentUser');



  if (token && userStr) {
    try {
      const user = JSON.parse(userStr);
      return true;
    } catch (e) {
 
      localStorage.removeItem('currentUser');
      localStorage.removeItem('jwtToken');
    }
  } 

  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
