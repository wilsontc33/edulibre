import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { CursoAdmin } from '../../../core/models/admin-curso';
import { AdminCursoService } from '../../../core/services/admin-curso';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';
import { PageHeader } from '../../../shared/page-header/page-header';

@Component({
  selector: 'app-admin-cursos',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeader],
  templateUrl: './admin-cursos.html',
  styleUrl: './admin-cursos.css'
})
export class AdminCursos implements OnInit {
  private readonly adminCursoService = inject(AdminCursoService);

  protected readonly cursos = signal<CursoAdmin[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly mensaje = signal<string | null>(null);
  protected readonly procesando = signal<string | null>(null);

  ngOnInit() {
    this.cargar();
  }

  private cargar() {
    this.adminCursoService
      .listar()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (cursos) => this.cursos.set(cursos),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  protected cambiarPublicado(curso: CursoAdmin) {
    this.procesando.set(curso.id);
    const publicado = !curso.publicado;

    this.adminCursoService
      .cambiarPublicado(curso.id, publicado)
      .pipe(finalize(() => this.procesando.set(null)))
      .subscribe({
        next: () => {
          this.cursos.update(cursos =>
            cursos.map(c => (c.id === curso.id ? { ...c, publicado } : c)));
          this.mensaje.set(publicado ? 'Curso publicado en el catálogo.' : 'Curso ocultado del catálogo.');
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }
}
