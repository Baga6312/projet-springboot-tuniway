import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // Check if we're in browser environment
  if (typeof window !== 'undefined' && window.localStorage) {
    const token = localStorage.getItem('jwtToken');
    
    if (token) {
      // Clone request and add Authorization header
      const clonedRequest = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      
      console.log('JWT Interceptor: Token attached to request', req.url);
      return next(clonedRequest);
    } else {
      console.log('JWT Interceptor: No token found');
    }
  }
  
  return next(req);
};