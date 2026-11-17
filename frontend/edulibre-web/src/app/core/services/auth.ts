import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth';
import { Usuario } from '../models/usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/auth`;
  private readonly tokenKey = 'edulibre_token';
  private readonly userKey = 'edulibre_user';

  usuarioActual = signal<AuthResponse | null>(this.leerUsuarioGuardado());
  isLoggedIn = computed(() => this.usuarioActual() !== null);
  isAdmin = computed(() => this.usuarioActual()?.rol === 'ADMIN');

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, request)
      .pipe(tap((response) => this.guardarSesion(response)));
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/register`, request)
      .pipe(tap((response) => this.guardarSesion(response)));
  }

  me(): Observable<Usuario> {
    return this.http
      .get<Usuario>(`${this.apiUrl}/me`)
      .pipe(tap((usuario) => this.actualizarDatosUsuario(usuario)));
  }

  refrescarSesion(): Observable<Usuario | null> {
    if (!this.getToken()) {
      return of(null);
    }

    return this.me().pipe(
      catchError(() => {
        this.logout();
        return of(null);
      })
    );
  }

  actualizarDatosUsuario(usuario: Usuario): void {
    const actual = this.usuarioActual();
    const token = actual?.token ?? this.getToken();

    if (!token) {
      return;
    }

    const actualizado: AuthResponse = {
      token,
      email: usuario.email,
      nombre: usuario.nombre,
      rol: usuario.rol
    };
    sessionStorage.setItem(this.userKey, JSON.stringify(actualizado));
    this.usuarioActual.set(actualizado);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.tokenKey);
  }

  logout(): void {
    sessionStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.userKey);
    this.usuarioActual.set(null);
  }

  private guardarSesion(response: AuthResponse): void {
    sessionStorage.setItem(this.tokenKey, response.token);
    sessionStorage.setItem(this.userKey, JSON.stringify(response));
    this.usuarioActual.set(response);
  }

  private leerUsuarioGuardado(): AuthResponse | null {
    const guardado = sessionStorage.getItem(this.userKey);
    if (!guardado) {
      return null;
    }

    try {
      return JSON.parse(guardado) as AuthResponse;
    } catch {
      sessionStorage.removeItem(this.userKey);
      return null;
    }
  }
}
