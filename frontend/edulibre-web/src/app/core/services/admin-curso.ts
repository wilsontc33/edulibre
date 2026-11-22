import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CursoAdmin,
  CursoAdminDetalle,
  CursoAdminRequest,
  LeccionAdmin,
  LeccionRequest
} from '../models/admin-curso';

@Injectable({ providedIn: 'root' })
export class AdminCursoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/admin`;

  listar(): Observable<CursoAdmin[]> {
    return this.http.get<CursoAdmin[]>(`${this.apiUrl}/cursos`);
  }

  detalle(id: string): Observable<CursoAdminDetalle> {
    return this.http.get<CursoAdminDetalle>(`${this.apiUrl}/cursos/${id}`);
  }

  crear(request: CursoAdminRequest): Observable<CursoAdminDetalle> {
    return this.http.post<CursoAdminDetalle>(`${this.apiUrl}/cursos`, request);
  }

  actualizar(id: string, request: CursoAdminRequest): Observable<CursoAdminDetalle> {
    return this.http.put<CursoAdminDetalle>(`${this.apiUrl}/cursos/${id}`, request);
  }

  cambiarPublicado(id: string, publicado: boolean): Observable<CursoAdmin> {
    return this.http.patch<CursoAdmin>(`${this.apiUrl}/cursos/${id}/publicado`, { publicado });
  }

  listarLecciones(cursoId: string): Observable<LeccionAdmin[]> {
    return this.http.get<LeccionAdmin[]>(`${this.apiUrl}/cursos/${cursoId}/lecciones`);
  }

  crearLeccion(cursoId: string, request: LeccionRequest): Observable<LeccionAdmin> {
    return this.http.post<LeccionAdmin>(`${this.apiUrl}/cursos/${cursoId}/lecciones`, request);
  }

  actualizarLeccion(id: string, request: LeccionRequest): Observable<LeccionAdmin> {
    return this.http.put<LeccionAdmin>(`${this.apiUrl}/lecciones/${id}`, request);
  }

  eliminarLeccion(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/lecciones/${id}`);
  }
}
