import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CursoDetalle, CursoResumen } from '../models/curso';
import { Leccion } from '../models/leccion';

@Injectable({ providedIn: 'root' })
export class CursoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/cursos`;

  listar(categoriaSlug?: string): Observable<CursoResumen[]> {
    let params = new HttpParams();
    if (categoriaSlug) {
      params = params.set('categoria', categoriaSlug);
    }
    return this.http.get<CursoResumen[]>(this.apiUrl, { params });
  }

  detalle(cursoId: string): Observable<CursoDetalle> {
    return this.http.get<CursoDetalle>(`${this.apiUrl}/${cursoId}`);
  }

  lecciones(cursoId: string): Observable<Leccion[]> {
    return this.http.get<Leccion[]>(`${this.apiUrl}/${cursoId}/lecciones`);
  }
}
