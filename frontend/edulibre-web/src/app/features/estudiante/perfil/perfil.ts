import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { Usuario } from '../../../core/models/usuario';
import { UsuarioService } from '../../../core/services/usuario';
import { AuthService } from '../../../core/services/auth';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';
import { PageHeader } from '../../../shared/page-header/page-header';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, PageHeader],
  templateUrl: './perfil.html'
})
export class Perfil implements OnInit {
  private readonly usuarioService = inject(UsuarioService);
  public readonly authService = inject(AuthService);

  usuario = signal<Usuario | null>(null);
  cargando = signal(true);
  editando = signal(false);
  guardando = signal(false);
  error = signal<string | null>(null);
  mensaje = signal<string | null>(null);
  nombre = signal('');

  ngOnInit() {
    this.usuarioService
      .me()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (usuario) => this.usuario.set(usuario),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  activarEdicion() {
    this.nombre.set(this.usuario()?.nombre ?? '');
    this.mensaje.set(null);
    this.editando.set(true);
  }

  cancelarEdicion() {
    this.editando.set(false);
    this.error.set(null);
  }

  guardar() {
    const nombre = this.nombre().trim();
    if (!nombre) {
      this.error.set('El nombre no puede estar vacío.');
      return;
    }

    this.guardando.set(true);
    this.error.set(null);

    this.usuarioService
      .actualizar(nombre)
      .pipe(finalize(() => this.guardando.set(false)))
      .subscribe({
        next: (usuario) => {
          this.usuario.set(usuario);
          this.editando.set(false);
          this.mensaje.set('Perfil actualizado correctamente.');
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  formatearFecha(valor: string): string {
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
