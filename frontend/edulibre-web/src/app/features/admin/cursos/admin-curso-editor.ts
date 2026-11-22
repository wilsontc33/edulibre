import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { Categoria } from '../../../core/models/categoria';
import { CursoAdminDetalle, LeccionAdmin } from '../../../core/models/admin-curso';
import { AdminCursoService } from '../../../core/services/admin-curso';
import { CategoriaService } from '../../../core/services/categoria';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';
import { PageHeader } from '../../../shared/page-header/page-header';

@Component({
  selector: 'app-admin-curso-editor',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeader],
  templateUrl: './admin-curso-editor.html',
  styleUrl: './admin-curso-editor.css'
})
export class AdminCursoEditor implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly adminCursoService = inject(AdminCursoService);
  private readonly categoriaService = inject(CategoriaService);

  protected readonly cursoId = signal<string | null>(null);
  protected readonly esNuevo = computed(() => this.cursoId() === null);

  protected readonly titulo = signal('');
  protected readonly descripcion = signal('');
  protected readonly categoriaSlug = signal('');
  protected readonly instructor = signal('');
  protected readonly nivel = signal('');
  protected readonly duracionSemanas = signal<number | null>(null);
  protected readonly imagenUrl = signal('');
  protected readonly publicado = signal(true);

  protected readonly categorias = signal<Categoria[]>([]);
  protected readonly lecciones = signal<LeccionAdmin[]>([]);
  protected readonly totalInscritos = signal(0);

  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly ocultando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly mensaje = signal<string | null>(null);

  protected readonly nuevaLeccionTitulo = signal('');
  protected readonly nuevaLeccionYoutube = signal('');
  protected readonly nuevaLeccionDuracion = signal<number | null>(null);
  protected readonly agregandoLeccion = signal(false);
  protected readonly leccionEditando = signal<string | null>(null);
  protected readonly leccionEditTitulo = signal('');
  protected readonly leccionEditYoutube = signal('');
  protected readonly leccionEditDuracion = signal<number | null>(null);
  protected readonly borrandoLeccion = signal<string | null>(null);

  private readonly niveles = ['Básico', 'Intermedio', 'Avanzado'] as const;
  protected readonly nivelesDisponibles = this.niveles;

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.cursoId.set(idParam && idParam !== 'nuevo' ? idParam : null);

    this.categoriaService.listar().subscribe({
      next: (categorias) => {
        this.categorias.set(categorias);
        if (this.categorias().length > 0 && !this.categoriaSlug()) {
          this.categoriaSlug.set(this.categorias()[0].slug);
        }
      },
      error: (err) => this.error.set(extraerMensajeError(err))
    });

    if (this.cursoId()) {
      this.cargarCurso(this.cursoId()!);
    } else {
      this.cargando.set(false);
    }
  }

  private cargarCurso(id: string) {
    this.adminCursoService
      .detalle(id)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (curso) => this.aplicarCurso(curso),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  private aplicarCurso(curso: CursoAdminDetalle) {
    this.titulo.set(curso.titulo);
    this.descripcion.set(curso.descripcion ?? '');
    this.categoriaSlug.set(curso.categoriaSlug);
    this.instructor.set(curso.instructor);
    this.nivel.set(curso.nivel ?? '');
    this.duracionSemanas.set(curso.duracionSemanas);
    this.imagenUrl.set(curso.imagenUrl ?? '');
    this.publicado.set(curso.publicado);
    this.lecciones.set(curso.lecciones);
    this.totalInscritos.set(curso.totalInscritos);
  }

  protected guardar() {
    if (!this.titulo().trim() || !this.instructor().trim() || !this.categoriaSlug()) {
      this.error.set('Completa título, instructor y categoría antes de guardar.');
      return;
    }

    this.guardando.set(true);
    this.error.set(null);

    const request = {
      titulo: this.titulo().trim(),
      descripcion: this.descripcion().trim() || null,
      categoriaSlug: this.categoriaSlug(),
      instructor: this.instructor().trim(),
      imagenUrl: this.imagenUrl().trim() || null,
      nivel: this.nivel() || null,
      duracionSemanas: this.duracionSemanas(),
      publicado: this.publicado()
    };

    const peticion = this.cursoId()
      ? this.adminCursoService.actualizar(this.cursoId()!, request)
      : this.adminCursoService.crear(request);

    peticion
      .pipe(finalize(() => this.guardando.set(false)))
      .subscribe({
        next: (curso) => {
          this.mensaje.set('Curso guardado correctamente.');
          if (this.esNuevo()) {
            this.router.navigate(['/admin/cursos', curso.id], { replaceUrl: true });
          }
          this.aplicarCurso(curso);
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  protected ocultarCurso() {
    const id = this.cursoId();
    if (!id) {
      return;
    }

    this.ocultando.set(true);
    this.adminCursoService
      .cambiarPublicado(id, false)
      .pipe(finalize(() => this.ocultando.set(false)))
      .subscribe({
        next: () => {
          this.publicado.set(false);
          this.mensaje.set('Curso ocultado del catálogo.');
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  protected agregarLeccion() {
    const titulo = this.nuevaLeccionTitulo().trim();
    const youtube = this.nuevaLeccionYoutube().trim();

    if (!titulo || !youtube) {
      this.error.set('La lección necesita título y enlace de YouTube.');
      return;
    }

    const id = this.cursoId();
    if (!id) {
      return;
    }

    this.agregandoLeccion.set(true);
    this.adminCursoService
      .crearLeccion(id, {
        titulo,
        orden: null,
        youtubeId: youtube,
        duracionMin: this.nuevaLeccionDuracion()
      })
      .pipe(finalize(() => this.agregandoLeccion.set(false)))
      .subscribe({
        next: (leccion) => {
          this.lecciones.update(list => [...list, leccion]);
          this.nuevaLeccionTitulo.set('');
          this.nuevaLeccionYoutube.set('');
          this.nuevaLeccionDuracion.set(null);
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  protected iniciarEdicionLeccion(leccion: LeccionAdmin) {
    this.leccionEditando.set(leccion.id);
    this.leccionEditTitulo.set(leccion.titulo);
    this.leccionEditYoutube.set(leccion.youtubeId);
    this.leccionEditDuracion.set(leccion.duracionMin);
  }

  protected cancelarEdicionLeccion() {
    this.leccionEditando.set(null);
  }

  protected guardarLeccion(leccion: LeccionAdmin) {
    const titulo = this.leccionEditTitulo().trim();
    const youtube = this.leccionEditYoutube().trim();

    if (!titulo || !youtube) {
      this.error.set('La lección necesita título y enlace de YouTube.');
      return;
    }

    this.adminCursoService
      .actualizarLeccion(leccion.id, {
        titulo,
        orden: leccion.orden,
        youtubeId: youtube,
        duracionMin: this.leccionEditDuracion()
      })
      .subscribe({
        next: (actualizada) => {
          this.lecciones.update(list =>
            list.map(item => (item.id === actualizada.id ? actualizada : item)));
          this.leccionEditando.set(null);
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  protected confirmarBorradoLeccion(id: string) {
    this.borrandoLeccion.set(id);
  }

  protected cancelarBorradoLeccion() {
    this.borrandoLeccion.set(null);
  }

  protected borrarLeccion(leccion: LeccionAdmin) {
    this.adminCursoService.eliminarLeccion(leccion.id).subscribe({
      next: () => {
        this.lecciones.update(list => list.filter(item => item.id !== leccion.id));
        this.borrandoLeccion.set(null);
        this.mensaje.set('Lección eliminada.');
      },
      error: (err) => this.error.set(extraerMensajeError(err))
    });
  }
}
