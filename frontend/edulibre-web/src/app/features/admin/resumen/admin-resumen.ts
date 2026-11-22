import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { AdminInscripcion } from '../../../core/models/inscripcion';
import { CursoAdmin } from '../../../core/models/admin-curso';
import { AdminInscripcionService } from '../../../core/services/admin-inscripcion';
import { AdminCursoService } from '../../../core/services/admin-curso';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';
import { PageHeader } from '../../../shared/page-header/page-header';

@Component({
  selector: 'app-admin-resumen',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeader],
  templateUrl: './admin-resumen.html',
  styleUrl: './admin-resumen.css'
})
export class AdminResumen implements OnInit {
  private readonly adminInscripcionService = inject(AdminInscripcionService);
  private readonly adminCursoService = inject(AdminCursoService);

  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly pendientes = signal<AdminInscripcion[]>([]);
  protected readonly aprobadas = signal<AdminInscripcion[]>([]);
  protected readonly rechazadas = signal<AdminInscripcion[]>([]);
  protected readonly cursos = signal<CursoAdmin[]>([]);

  protected readonly cursosPublicados = computed(() =>
    this.cursos().filter(curso => curso.publicado).length);

  protected readonly recientes = computed(() => this.pendientes().slice(0, 3));

  protected readonly populares = computed(() =>
    [...this.cursos()].sort((a, b) => b.totalInscritos - a.totalInscritos).slice(0, 3));

  ngOnInit() {
    forkJoin({
      pendientes: this.adminInscripcionService.listar('PENDIENTE'),
      aprobadas: this.adminInscripcionService.listar('APROBADA'),
      rechazadas: this.adminInscripcionService.listar('RECHAZADA'),
      cursos: this.adminCursoService.listar()
    })
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: ({ pendientes, aprobadas, rechazadas, cursos }) => {
          this.pendientes.set(pendientes);
          this.aprobadas.set(aprobadas);
          this.rechazadas.set(rechazadas);
          this.cursos.set(cursos);
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }
}
