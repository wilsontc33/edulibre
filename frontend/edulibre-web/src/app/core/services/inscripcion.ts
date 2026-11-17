import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CrearInscripcionRequest, Inscripcion } from '../models/inscripcion';

@Injectable({ providedIn: 'root' })
export class InscripcionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/inscripciones`;

  crear(request: CrearInscripcionRequest): Observable<Inscripcion> {
    return this.http.post<Inscripcion>(this.apiUrl, request);
  }

  mis(): Observable<Inscripcion[]> {
    return this.http.get<Inscripcion[]>(`${this.apiUrl}/mis`);
  }
}
