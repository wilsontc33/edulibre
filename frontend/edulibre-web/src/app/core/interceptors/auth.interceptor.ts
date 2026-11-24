import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const esApi = request.url.startsWith(environment.apiBaseUrl) || request.url.startsWith('/api');
  if (!esApi || request.withCredentials) {
    return next(request);
  }

  return next(request.clone({ withCredentials: true }));
};
