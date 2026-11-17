import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AdminInscripcion, EstadoInscripcion } from '../models/inscripcion';

@Injectable({ providedIn: 'root' })
export class AdminInscripcionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/admin/inscripciones`;

  listar(estado?: EstadoInscripcion): Observable<AdminInscripcion[]> {
    let params = new HttpParams();
    if (estado) {
      params = params.set('estado', estado);
    }
    return this.http.get<AdminInscripcion[]>(this.apiUrl, { params });
  }

  aprobar(id: string): Observable<AdminInscripcion> {
    return this.http.patch<AdminInscripcion>(`${this.apiUrl}/${id}/aprobar`, {});
  }

  rechazar(id: string, comentarioAdmin: string): Observable<AdminInscripcion> {
    return this.http.patch<AdminInscripcion>(`${this.apiUrl}/${id}/rechazar`, {
      comentarioAdmin
    });
  }
}
