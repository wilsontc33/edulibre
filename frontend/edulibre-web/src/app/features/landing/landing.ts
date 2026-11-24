import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { Categoria } from '../../core/models/categoria';
import { CursoResumen } from '../../core/models/curso';
import { CrearInscripcionRequest } from '../../core/models/inscripcion';
import { CategoriaService } from '../../core/services/categoria';
import { CursoService } from '../../core/services/curso';
import { InscripcionService } from '../../core/services/inscripcion';
import { AuthService } from '../../core/services/auth';
import { AuthModalService } from '../../core/services/auth-modal';
import { extraerMensajeError } from '../../core/interceptors/error.interceptor';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './landing.html'
})
export class Landing implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cursoService = inject(CursoService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly inscripcionService = inject(InscripcionService);
  private readonly authModalService = inject(AuthModalService);
  public readonly authService = inject(AuthService);

  readonly postulacionForm: FormGroup;
  readonly mostrarExito = signal(false);
  readonly cargando = signal(false);
  readonly cargandoCursos = signal(true);
  readonly error = signal<string | null>(null);
  readonly categorias = signal<Categoria[]>([]);
  readonly categoriaSeleccionada = signal('TODOS');
  readonly cursos = signal<CursoResumen[]>([]);
  readonly categoriasConTodas = computed(() => [
    { id: 'todas', nombre: 'TODOS', slug: 'TODOS' },
    ...this.categorias()
  ]);

  readonly pasos = [
    {
      numero: '01',
      icono: 'person_add',
      titulo: 'Crea tu cuenta gratuita',
      descripcion: 'Sin tarjetas de crédito ni costos ocultos. Regístrate en menos de 30 segundos y accede instantáneamente a todo nuestro ecosistema pedagógico.',
      pieIcono: 'bolt',
      pieTexto: 'Acceso en 30 segundos'
    },
    {
      numero: '02',
      icono: 'menu_book',
      titulo: 'Aprende a tu propio ritmo',
      descripcion: 'Proyectos prácticos interactivos, retos reales basados en el mercado y lecciones audiovisuales de alta producción disponibles 24/7.',
      pieIcono: 'schedule',
      pieTexto: 'Flexibilidad 100% asíncrona'
    },
    {
      numero: '03',
      icono: 'workspace_premium',
      titulo: 'Obtén tu certificación',
      descripcion: 'Valida tus competencias tecnológicas con credenciales digitales verificables mediante código criptográfico. Impulsa tu CV y perfil de LinkedIn.',
      pieIcono: 'share',
      pieTexto: 'Insignias integrables a LinkedIn'
    }
  ] as const;

  private postulacionPendiente = signal<CrearInscripcionRequest | null>(null);

  constructor() {
    this.postulacionForm = this.fb.group({
      cursoId: ['', Validators.required],
      nombreCompleto: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      nivelPrevio: ['', Validators.required],
      horasSemana: [null, [Validators.required, Validators.min(1), Validators.max(80)]],
      motivacion: ['', [Validators.required, Validators.minLength(20)]],
      aceptaTerminos: [false, Validators.requiredTrue]
    });

    effect(() => {
      const usuario = this.authService.usuarioActual();
      if (!usuario) {
        return;
      }

      this.postulacionForm.patchValue(
        { nombreCompleto: usuario.nombre, email: usuario.email },
        { emitEvent: false }
      );

      const pendiente = this.postulacionPendiente();
      if (pendiente) {
        this.postulacionPendiente.set(null);
        this.enviarAlBackend(pendiente);
      }
    });
  }

  ngOnInit() {
    this.cargarCategorias();
    this.cargarCursos();
  }

  abrirRegistro() {
    this.authModalService.abrirModal('registro');
  }

  filtrarPorSlug(slug: string) {
    this.categoriaSeleccionada.set(slug);
    this.cargarCursos(slug);
  }

  seleccionarCurso(cursoId: string) {
    this.postulacionForm.patchValue({ cursoId });
    document.getElementById('formulario-inscripcion')?.scrollIntoView({ behavior: 'smooth' });
  }

  enviarPostulacion() {
    if (this.postulacionForm.invalid) {
      this.postulacionForm.markAllAsTouched();
      return;
    }

    const valor = this.postulacionForm.getRawValue();
    const request: CrearInscripcionRequest = {
      cursoId: valor.cursoId,
      motivacion: valor.motivacion,
      nivelPrevio: valor.nivelPrevio,
      horasSemana: Number(valor.horasSemana)
    };

    if (!this.authService.isLoggedIn()) {
      this.postulacionPendiente.set(request);
      this.authModalService.abrirModal('login');
      return;
    }

    this.enviarAlBackend(request);
  }

  controlInvalido(nombre: string): boolean {
    const control = this.postulacionForm.get(nombre);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  private enviarAlBackend(request: CrearInscripcionRequest) {
    this.cargando.set(true);
    this.error.set(null);

    this.inscripcionService
      .crear(request)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: () => {
          const usuario = this.authService.usuarioActual();
          this.mostrarExito.set(true);
          this.postulacionForm.reset({
            cursoId: '',
            nombreCompleto: usuario?.nombre ?? '',
            email: usuario?.email ?? '',
            nivelPrevio: '',
            horasSemana: null,
            motivacion: '',
            aceptaTerminos: false
          });
          setTimeout(() => this.mostrarExito.set(false), 6000);
        },
        error: (err) => {
          if (err?.status === 401) {
            this.postulacionPendiente.set(request);
            this.authModalService.abrirModal('login');
          }
          this.error.set(extraerMensajeError(err));
        }
      });
  }

  private cargarCategorias() {
    this.categoriaService.listar().subscribe({
      next: (categorias) => this.categorias.set(categorias),
      error: () => this.categorias.set([])
    });
  }

  private cargarCursos(slug?: string) {
    this.cargandoCursos.set(true);
    this.cursoService
      .listar(slug && slug !== 'TODOS' ? slug : undefined)
      .pipe(finalize(() => this.cargandoCursos.set(false)))
      .subscribe({
        next: (cursos) => this.cursos.set(cursos),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }
}
