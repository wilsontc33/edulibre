import { EnvironmentProviders, Provider } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { authInterceptor } from '../interceptors/auth.interceptor';
import { errorInterceptor } from '../interceptors/error.interceptor';
import { AuthService } from '../services/auth';

export const testProviders: Array<Provider | EnvironmentProviders> = [
  provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
  provideHttpClientTesting(),
  provideRouter([]),
  {
    provide: AuthService,
    useValue: {
      usuarioActual: () => null,
      isLoggedIn: () => false,
      isAdmin: () => false,
      login: () => of(null),
      register: () => of(null),
      logout: () => undefined,
      refrescarSesion: () => of(null),
      limpiarSesionLocal: () => undefined
    }
  }
];
