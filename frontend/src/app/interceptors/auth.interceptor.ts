import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const token = authService.getToken();
  
  // Debug logging
  console.log('🔍 Auth Interceptor - Request URL:', req.url);
  console.log('🔍 Auth Interceptor - Token exists:', !!token);
  
  // Clone the request and add the authorization header if token exists
  let authReq = req;
  if (token) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    console.log('✅ Auth Interceptor - Authorization header added');
  } else {
    console.log('❌ Auth Interceptor - No token found, skipping authorization header');
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      console.log('🚨 Auth Interceptor - HTTP Error:', error.status, error.message);
      if (error.status === 401) {
        console.log('🚨 Auth Interceptor - 401 Unauthorized, logging out user');
        // Token expired or invalid, logout user
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
