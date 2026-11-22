import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { Inscripcion } from '../../../core/models/inscripcion';
import { CursoDetalle } from '../../../core/models/curso';
import { Progreso } from '../../../core/models/progreso';
import { InscripcionService } from '../../../core/services/inscripcion';
import { CursoService } from '../../../core/services/curso';
import { ProgresoService } from '../../../core/services/progreso';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';
import { PageHeader } from '../../../shared/page-header/page-header';

interface CursoAprobado {
  inscripcion: Inscripcion;
  curso: CursoDetalle | null;
  progreso: Progreso | null;
}

@Component({
  selector: 'app-mis-cursos',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeader],
  templateUrl: './mis-cursos.html',
  styleUrl: './mis-cursos.css'
})
export class MisCursos implements OnInit {
  private readonly inscripcionService = inject(InscripcionService);
  private readonly cursoService = inject(CursoService);
  private readonly progresoService = inject(ProgresoService);

  protected readonly filas = signal<CursoAprobado[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit() {
    this.inscripcionService
      .mis()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (postulaciones) => this.cargarCursos(postulaciones.filter(p => p.estado === 'APROBADA')),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  private cargarCursos(aprobadas: Inscripcion[]) {
    if (aprobadas.length === 0) {
      return;
    }

    forkJoin(
      aprobadas.map(inscripcion =>
        forkJoin({
          curso: this.cursoService.detalle(inscripcion.cursoId),
          progreso: this.progresoService.obtener(inscripcion.cursoId)
        })
      )
    ).subscribe({
      next: (resultados) =>
        this.filas.set(aprobadas.map((inscripcion, i) => ({
          inscripcion,
          curso: resultados[i].curso,
          progreso: resultados[i].progreso
        }))),
      error: (err) => this.error.set(extraerMensajeError(err))
    });
  }

  protected readonly enProgreso = () =>
    this.filas().filter(f => (f.progreso?.porcentaje ?? 0) < 100).length;

  protected readonly completados = () =>
    this.filas().filter(f => (f.progreso?.porcentaje ?? 0) === 100).length;

  protected textoBoton(fila: CursoAprobado): string {
    const porcentaje = fila.progreso?.porcentaje ?? 0;
    if (porcentaje === 100) {
      return 'Repasar curso';
    }
    return porcentaje === 0 ? 'Empezar curso' : 'Continuar';
  }
}
