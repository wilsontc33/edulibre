import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Usuario } from '../models/usuario';
import { AuthService } from './auth';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly apiUrl = `${environment.apiBaseUrl}/usuarios`;

  me(): Observable<Usuario> {
    return this.http
      .get<Usuario>(`${this.apiUrl}/me`)
      .pipe(tap((usuario) => this.authService.actualizarDatosUsuario(usuario)));
  }

  actualizar(nombre: string): Observable<Usuario> {
    return this.http
      .put<Usuario>(`${this.apiUrl}/me`, { nombre })
      .pipe(tap((usuario) => this.authService.actualizarDatosUsuario(usuario)));
  }
}
