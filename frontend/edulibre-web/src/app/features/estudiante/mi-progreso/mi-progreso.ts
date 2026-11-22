import { Component, OnInit, computed, inject, signal } from '@angular/core';
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

interface FilaProgreso {
  inscripcion: Inscripcion;
  curso: CursoDetalle | null;
  progreso: Progreso | null;
}

@Component({
  selector: 'app-mi-progreso',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeader],
  templateUrl: './mi-progreso.html',
  styleUrl: './mi-progreso.css'
})
export class MiProgreso implements OnInit {
  private readonly inscripcionService = inject(InscripcionService);
  private readonly cursoService = inject(CursoService);
  private readonly progresoService = inject(ProgresoService);

  protected readonly filas = signal<FilaProgreso[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly leccionesCompletadas = computed(() =>
    this.filas().reduce((total, fila) => total + (fila.progreso?.leccionesCompletadas ?? 0), 0));

  protected readonly enProgreso = computed(() =>
    this.filas().filter(fila => (fila.progreso?.porcentaje ?? 0) < 100).length);

  protected readonly completados = computed(() =>
    this.filas().filter(fila => (fila.progreso?.porcentaje ?? 0) === 100).length);

  protected readonly promedio = computed(() => {
    const filas = this.filas();
    if (filas.length === 0) {
      return 0;
    }
    const total = filas.reduce((suma, fila) => suma + (fila.progreso?.porcentaje ?? 0), 0);
    return Math.round(total / filas.length);
  });

  protected readonly conicGradient = computed(
    () => `conic-gradient(var(--color-secondary) ${this.promedio() * 3.6}deg, var(--color-surface-container) 0deg)`);

  ngOnInit() {
    this.inscripcionService
      .mis()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (postulaciones) => this.cargarProgreso(postulaciones.filter(p => p.estado === 'APROBADA')),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  private cargarProgreso(aprobadas: Inscripcion[]) {
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
}
