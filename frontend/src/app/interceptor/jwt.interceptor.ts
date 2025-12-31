import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('/oauth2/') || req.url.includes('/login/oauth2/')) {
    return next(req);
  }

  if (typeof window !== 'undefined' && window.localStorage) {
    const token = localStorage.getItem('jwtToken');
    
    if (token) {
      const clonedRequest = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      
      return next(clonedRequest);
    } 
  }
  
  return next(req);
};
