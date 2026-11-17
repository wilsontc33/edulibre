import { Component, inject, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { AdminInscripcionService } from '../../../core/services/admin-inscripcion';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';

@Component({
  selector: 'app-modal-rechazo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-rechazo.html'
})
export class ModalRechazo {
  private readonly adminInscripcionService = inject(AdminInscripcionService);

  rechazado = output<void>();

  isOpen = signal(false);
  estudianteNombre = signal('');
  cursoNombre = signal('');
  motivo = signal('');
  idSolicitud = signal('');
  guardando = signal(false);
  error = signal<string | null>(null);

  abrir(id: string, nombre: string, curso: string) {
    this.idSolicitud.set(id);
    this.estudianteNombre.set(nombre);
    this.cursoNombre.set(curso);
    this.motivo.set('');
    this.error.set(null);
    this.isOpen.set(true);
  }

  cerrar() {
    this.isOpen.set(false);
  }

  confirmarRechazo() {
    const motivo = this.motivo().trim();
    if (!motivo) {
      this.error.set('Por favor, escribe un motivo de rechazo.');
      return;
    }

    this.guardando.set(true);
    this.error.set(null);

    this.adminInscripcionService
      .rechazar(this.idSolicitud(), motivo)
      .pipe(finalize(() => this.guardando.set(false)))
      .subscribe({
        next: () => {
          this.cerrar();
          this.rechazado.emit();
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }
}
