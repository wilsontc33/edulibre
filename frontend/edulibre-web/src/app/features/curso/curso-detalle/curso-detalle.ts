import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { finalize } from 'rxjs/operators';
import { CursoDetalle as CursoDetalleModelo } from '../../../core/models/curso';
import { Leccion } from '../../../core/models/leccion';
import { Progreso } from '../../../core/models/progreso';
import { Resena } from '../../../core/models/resena';
import { CursoService } from '../../../core/services/curso';
import { ProgresoService } from '../../../core/services/progreso';
import { ResenaService } from '../../../core/services/resena';
import { AuthService } from '../../../core/services/auth';
import { AuthModalService } from '../../../core/services/auth-modal';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';

interface CursoVista extends CursoDetalleModelo {
  lecciones: Leccion[];
}

@Component({
  selector: 'app-curso-detalle',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './curso-detalle.html'
})
export class CursoDetalle implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly cursoService = inject(CursoService);
  private readonly progresoService = inject(ProgresoService);
  private readonly resenaService = inject(ResenaService);
  private readonly authModalService = inject(AuthModalService);
  public readonly authService = inject(AuthService);

  curso = signal<CursoVista | null>(null);
  leccionActiva = signal<Leccion | null>(null);
  urlVideoSegura = signal<SafeResourceUrl | null>(null);
  progreso = signal<Progreso | null>(null);
  resenas = signal<Resena[]>([]);
  cargando = signal(true);
  error = signal<string | null>(null);
  errorLecciones = signal<string | null>(null);
  errorResenas = signal<string | null>(null);
  completando = signal<string | null>(null);
  enviandoResena = signal(false);
  mensajeResena = signal<string | null>(null);
  yaReseno = signal(false);
  nuevaCalificacion = signal(5);
  nuevoComentario = signal('');

  esAprobado = computed(() =>
    this.authService.isAdmin() || this.curso()?.estadoInscripcion === 'APROBADA');

  puedeCompletar = computed(() =>
    !this.authService.isAdmin() && this.curso()?.estadoInscripcion === 'APROBADA');

  ngOnInit() {
    const cursoId = this.route.snapshot.paramMap.get('id');
    if (!cursoId) {
      this.error.set('No se encontró el curso solicitado.');
      this.cargando.set(false);
      return;
    }

    this.cursoService
      .detalle(cursoId)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (detalle) => {
          this.curso.set({ ...detalle, lecciones: [] });
          this.cargarResenas(cursoId);

          this.cargarLecciones(cursoId);

          if (!this.authService.isAdmin() && detalle.estadoInscripcion === 'APROBADA') {
            this.cargarProgreso(cursoId);
          }
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  seleccionarLeccion(leccion: Leccion) {
    if (!this.esAprobado()) {
      return;
    }

    this.leccionActiva.set(leccion);

    if (!leccion.youtubeId) {
      this.urlVideoSegura.set(null);
      return;
    }

    const url = `https://www.youtube.com/embed/${leccion.youtubeId}?autoplay=0&rel=0`;
    this.urlVideoSegura.set(this.sanitizer.bypassSecurityTrustResourceUrl(url));
  }

  completarLeccion(leccion: Leccion) {
    if (!this.puedeCompletar() || this.estaCompletada(leccion.id)) {
      return;
    }

    this.completando.set(leccion.id);
    this.progresoService
      .completar(leccion.id)
      .pipe(finalize(() => this.completando.set(null)))
      .subscribe({
        next: (progreso) => this.progreso.set(progreso),
        error: (err) => this.errorLecciones.set(extraerMensajeError(err))
      });
  }

  estaCompletada(leccionId: string): boolean {
    return this.progreso()?.leccionesCompletadasIds.includes(leccionId) ?? false;
  }

  enviarResena() {
    if (!this.authService.isLoggedIn()) {
      this.authModalService.abrirModal('login');
      return;
    }

    const comentario = this.nuevoComentario().trim();
    if (!comentario) {
      this.mensajeResena.set('Escribe un comentario para publicar tu reseña.');
      return;
    }

    const cursoId = this.curso()?.id;
    if (!cursoId) {
      return;
    }

    this.enviandoResena.set(true);
    this.mensajeResena.set(null);

    this.resenaService
      .crear(cursoId, {
        calificacion: this.nuevaCalificacion(),
        comentario
      })
      .pipe(finalize(() => this.enviandoResena.set(false)))
      .subscribe({
        next: (resena) => {
          this.resenas.update((resenas) => [resena, ...resenas]);
          this.nuevoComentario.set('');
          this.nuevaCalificacion.set(5);
          this.yaReseno.set(true);
          this.mensajeResena.set('¡Gracias! Tu reseña fue publicada.');
        },
        error: (err) => {
          if (err?.status === 409) {
            this.yaReseno.set(true);
          }
          this.mensajeResena.set(extraerMensajeError(err));
        }
      });
  }

  private cargarLecciones(cursoId: string) {
    this.cursoService.lecciones(cursoId).subscribe({
      next: (lecciones) => {
        this.curso.update((curso) =>
          curso ? { ...curso, lecciones } : curso
        );

        if (lecciones.length > 0 && this.esAprobado()) {
          this.seleccionarLeccion(lecciones[0]);
        }
      },
      error: (err) => this.errorLecciones.set(extraerMensajeError(err))
    });
  }

  private cargarProgreso(cursoId: string) {
    this.progresoService.obtener(cursoId).subscribe({
      next: (progreso) => this.progreso.set(progreso),
      error: (err) => this.errorLecciones.set(extraerMensajeError(err))
    });
  }

  private cargarResenas(cursoId: string) {
    this.resenaService.listar(cursoId).subscribe({
      next: (resenas) => this.resenas.set(resenas),
      error: (err) => this.errorResenas.set(extraerMensajeError(err))
    });
  }
}
