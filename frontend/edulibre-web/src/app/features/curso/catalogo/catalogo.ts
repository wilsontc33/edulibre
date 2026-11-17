import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { Categoria } from '../../../core/models/categoria';
import { CursoResumen } from '../../../core/models/curso';
import { CategoriaService } from '../../../core/services/categoria';
import { CursoService } from '../../../core/services/curso';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './catalogo.html'
})
export class Catalogo implements OnInit {
  private readonly categoriaService = inject(CategoriaService);
  private readonly cursoService = inject(CursoService);

  categorias = signal<Categoria[]>([]);
  categoriaSeleccionada = signal('TODOS');
  cursos = signal<CursoResumen[]>([]);
  busqueda = signal('');
  cargando = signal(true);
  error = signal<string | null>(null);

  categoriasConTodas = computed(() => [
    { id: 'todas', nombre: 'TODOS', slug: 'TODOS' },
    ...this.categorias()
  ]);

  cursosFiltrados = computed(() => {
    const termino = this.busqueda().trim().toLowerCase();
    if (!termino) {
      return this.cursos();
    }

    return this.cursos().filter(
      (curso) =>
        curso.titulo.toLowerCase().includes(termino) ||
        curso.descripcion.toLowerCase().includes(termino)
    );
  });

  ngOnInit() {
    this.cargarCategorias();
    this.cargarCursos();
  }

  filtrarPorCategoria(slug: string) {
    this.categoriaSeleccionada.set(slug);
    this.cargarCursos(slug);
  }

  actualizarBusqueda(valor: string) {
    this.busqueda.set(valor);
  }

  private cargarCategorias() {
    this.categoriaService.listar().subscribe({
      next: (categorias) => this.categorias.set(categorias),
      error: () => this.categorias.set([])
    });
  }

  private cargarCursos(slug?: string) {
    this.cargando.set(true);
    this.error.set(null);

    this.cursoService
      .listar(slug && slug !== 'TODOS' ? slug : undefined)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (cursos) => this.cursos.set(cursos),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }
}
