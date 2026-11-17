import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Progreso } from '../models/progreso';

@Injectable({ providedIn: 'root' })
export class ProgresoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiBaseUrl;

  completar(leccionId: string): Observable<Progreso> {
    return this.http.post<Progreso>(`${this.apiUrl}/lecciones/${leccionId}/completar`, {});
  }

  obtener(cursoId: string): Observable<Progreso> {
    return this.http.get<Progreso>(`${this.apiUrl}/cursos/${cursoId}/progreso`);
  }
}
