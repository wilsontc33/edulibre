import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth';
import { Usuario } from '../models/usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/auth`;
  private readonly userKey = 'edulibre_user';

  readonly usuarioActual = signal<AuthResponse | null>(this.leerUsuarioGuardado());
  readonly isLoggedIn = computed(() => this.usuarioActual() !== null);
  readonly isAdmin = computed(() => this.usuarioActual()?.rol === 'ADMIN');

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, request, { withCredentials: true })
      .pipe(tap((response) => this.guardarSesion(response)));
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/register`, request, { withCredentials: true })
      .pipe(tap((response) => this.guardarSesion(response)));
  }

  me(): Observable<Usuario> {
    return this.http
      .get<Usuario>(`${this.apiUrl}/me`, { withCredentials: true })
      .pipe(tap((usuario) => this.actualizarDatosUsuario(usuario)));
  }

  refrescarSesion(): Observable<Usuario | null> {
    return this.me().pipe(
      catchError(() => {
        this.limpiarSesionLocal();
        return of(null);
      })
    );
  }

  actualizarDatosUsuario(usuario: Usuario): void {
    const actualizado = this.aSesion(usuario);
    sessionStorage.setItem(this.userKey, JSON.stringify(actualizado));
    this.usuarioActual.set(actualizado);
  }

  logout(): void {
    this.http
      .post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .pipe(
        catchError(() => of(void 0)),
        finalize(() => this.limpiarSesionLocal())
      )
      .subscribe();
  }

  limpiarSesionLocal(): void {
    sessionStorage.removeItem(this.userKey);
    sessionStorage.removeItem('edulibre_token');
    this.usuarioActual.set(null);
  }

  private guardarSesion(response: AuthResponse): void {
    const sesion = this.aSesion(response);
    sessionStorage.setItem(this.userKey, JSON.stringify(sesion));
    sessionStorage.removeItem('edulibre_token');
    this.usuarioActual.set(sesion);
  }

  private aSesion(datos: AuthResponse | Usuario): AuthResponse {
    return {
      email: datos.email,
      nombre: datos.nombre,
      rol: datos.rol
    };
  }

  private leerUsuarioGuardado(): AuthResponse | null {
    const guardado = sessionStorage.getItem(this.userKey);
    if (!guardado) {
      return null;
    }

    try {
      const datos = JSON.parse(guardado) as Partial<AuthResponse>;
      if (!datos.email || !datos.nombre || !datos.rol) {
        sessionStorage.removeItem(this.userKey);
        return null;
      }
      return { email: datos.email, nombre: datos.nombre, rol: datos.rol };
    } catch {
      sessionStorage.removeItem(this.userKey);
      return null;
    }
  }
}
