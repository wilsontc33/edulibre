import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { Categoria } from '../../../core/models/categoria';
import { CategoriaService } from '../../../core/services/categoria';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';
import { PageHeader } from '../../../shared/page-header/page-header';

@Component({
  selector: 'app-admin-categorias',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeader],
  templateUrl: './admin-categorias.html',
  styleUrl: './admin-categorias.css'
})
export class AdminCategorias implements OnInit {
  private readonly categoriaService = inject(CategoriaService);

  protected readonly categorias = signal<Categoria[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly mensaje = signal<string | null>(null);

  protected readonly nombre = signal('');
  protected readonly slug = signal('');
  protected readonly guardando = signal(false);
  protected readonly filtro = signal('');

  protected readonly slugPreview = computed(() => {
    const nombre = this.nombre().trim();
    if (this.slug().trim()) {
      return this.slugificar(this.slug());
    }
    return nombre ? this.slugificar(nombre) : '';
  });

  protected readonly categoriasFiltradas = computed(() => {
    const filtro = this.filtro().trim().toLowerCase();
    if (!filtro) {
      return this.categorias();
    }
    return this.categorias().filter(
      categoria =>
        categoria.nombre.toLowerCase().includes(filtro) ||
        categoria.slug.toLowerCase().includes(filtro));
  });

  ngOnInit() {
    this.cargar();
  }

  private cargar() {
    this.categoriaService
      .listar()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (categorias) => this.categorias.set(categorias),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  protected crear() {
    const nombre = this.nombre().trim();

    if (!nombre) {
      this.error.set('Escribe el nombre de la categoría.');
      return;
    }

    this.guardando.set(true);
    this.error.set(null);
    this.mensaje.set(null);

    this.categoriaService
      .crear({ nombre, slug: this.slug().trim() || null })
      .pipe(finalize(() => this.guardando.set(false)))
      .subscribe({
        next: (creada) => {
          this.categorias.update(categorias =>
            [...categorias, creada].sort((a, b) => a.nombre.localeCompare(b.nombre)));
          this.mensaje.set(`Categoría "${creada.nombre}" creada correctamente.`);
          this.nombre.set('');
          this.slug.set('');
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  protected slugificar(valor: string): string {
    return valor
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/(^-+|-+$)/g, '')
      .replace(/-{2,}/g, '-');
  }
}
