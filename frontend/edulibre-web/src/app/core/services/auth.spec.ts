import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  it('login guarda sesión y usa cookie', () => {
    service.login({ email: 'test@edulibre.com', password: 'secreto123' }).subscribe();

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);
    req.flush({ email: 'test@edulibre.com', nombre: 'Test', rol: 'ESTUDIANTE' });

    expect(service.isLoggedIn()).toBe(true);
    expect(service.usuarioActual()?.email).toBe('test@edulibre.com');
    expect(sessionStorage.getItem('edulibre_token')).toBeNull();
  });

  it('logout llama backend y limpia sesión local', () => {
    sessionStorage.setItem('edulibre_user', JSON.stringify({ email: 'a@b.com', nombre: 'A', rol: 'ESTUDIANTE' }));
    (service as any).usuarioActual.set({ email: 'a@b.com', nombre: 'A', rol: 'ESTUDIANTE' });

    service.logout();

    const req = httpMock.expectOne('/api/auth/logout');
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);
    req.flush(null);

    expect(service.isLoggedIn()).toBe(false);
    expect(sessionStorage.getItem('edulibre_user')).toBeNull();
  });
});
