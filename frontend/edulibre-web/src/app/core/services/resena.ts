import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CrearResenaRequest, Resena } from '../models/resena';

@Injectable({ providedIn: 'root' })
export class ResenaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiBaseUrl;

  listar(cursoId: string): Observable<Resena[]> {
    return this.http.get<Resena[]>(`${this.apiUrl}/cursos/${cursoId}/resenas`);
  }

  crear(cursoId: string, request: CrearResenaRequest): Observable<Resena> {
    return this.http.post<Resena>(`${this.apiUrl}/cursos/${cursoId}/resenas`, request);
  }
}
