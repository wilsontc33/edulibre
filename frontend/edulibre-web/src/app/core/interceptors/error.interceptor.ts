import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ApiError } from '../models/api-error';
import { AuthService } from '../services/auth';

export const errorInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && authService.getToken()) {
        authService.logout();
      }
      return throwError(() => error);
    })
  );
};

export function extraerMensajeError(error: unknown): string {
  const httpError = error as HttpErrorResponse;
  const apiError = httpError?.error as ApiError | undefined;

  if (apiError?.fieldErrors && Object.keys(apiError.fieldErrors).length > 0) {
    return Object.values(apiError.fieldErrors).join(' ');
  }

  if (apiError?.message) {
    return apiError.message;
  }

  if (httpError?.status === 0) {
    return 'No se pudo conectar con el servidor.';
  }

  return 'Ha ocurrido un error inesperado.';
}
