import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { EstadoInscripcion } from '../../../core/models/inscripcion';
import { InscripcionService } from '../../../core/services/inscripcion';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';
import { PageHeader } from '../../../shared/page-header/page-header';

interface PostulacionVista {
  id: string;
  cursoId: string;
  curso: string;
  fecha: string;
  estado: EstadoInscripcion;
  motivoRechazo: string | null;
}

@Component({
  selector: 'app-mis-postulaciones',
  standalone: true,
  imports: [CommonModule, RouterModule, PageHeader],
  templateUrl: './mis-postulaciones.html'
})
export class MisPostulaciones implements OnInit {
  private readonly inscripcionService = inject(InscripcionService);

  postulaciones = signal<PostulacionVista[]>([]);
  cargando = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    this.inscripcionService
      .mis()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (inscripciones) =>
          this.postulaciones.set(
            inscripciones.map((inscripcion) => ({
              id: inscripcion.id,
              cursoId: inscripcion.cursoId,
              curso: inscripcion.curso,
              fecha: this.formatearFecha(inscripcion.creadoEn),
              estado: inscripcion.estado,
              motivoRechazo: inscripcion.motivoRechazo
            }))
          ),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  private formatearFecha(valor: string): string {
    const fecha = new Date(valor);
    if (Number.isNaN(fecha.getTime())) {
      return valor;
    }

    return new Intl.DateTimeFormat('es', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    }).format(fecha);
  }
}
