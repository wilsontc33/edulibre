import { Component, OnInit, inject, signal, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { ModalRechazo } from '../modal-rechazo/modal-rechazo';
import { AdminInscripcion, EstadoInscripcion } from '../../../core/models/inscripcion';
import { AdminInscripcionService } from '../../../core/services/admin-inscripcion';
import { extraerMensajeError } from '../../../core/interceptors/error.interceptor';
import { PageHeader } from '../../../shared/page-header/page-header';

@Component({
  selector: 'app-postulaciones-pendientes',
  standalone: true,
  imports: [CommonModule, ModalRechazo, PageHeader],
  templateUrl: './postulaciones-pendientes.html'
})
export class PostulacionesPendientes implements OnInit {
  private readonly adminInscripcionService = inject(AdminInscripcionService);

  modalRechazo = viewChild(ModalRechazo);
  solicitudes = signal<AdminInscripcion[]>([]);
  estadoFiltro = signal<EstadoInscripcion>('PENDIENTE');
  cargando = signal(true);
  procesando = signal<string | null>(null);
  error = signal<string | null>(null);
  mensaje = signal<string | null>(null);
  estados: EstadoInscripcion[] = ['PENDIENTE', 'APROBADA', 'RECHAZADA'];

  ngOnInit() {
    this.cargar();
  }

  cambiarFiltro(estado: EstadoInscripcion) {
    this.estadoFiltro.set(estado);
    this.cargar();
  }

  aprobar(id: string) {
    this.procesando.set(id);
    this.error.set(null);
    this.mensaje.set(null);

    this.adminInscripcionService
      .aprobar(id)
      .pipe(finalize(() => this.procesando.set(null)))
      .subscribe({
        next: () => {
          this.mensaje.set('Postulación aprobada correctamente.');
          this.quitarDeLista(id);
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  onRechazado() {
    this.mensaje.set('Postulación rechazada correctamente.');
    this.cargar();
  }

  abrirModalRechazo(solicitud: AdminInscripcion) {
    this.modalRechazo()?.abrir(solicitud.id, solicitud.nombre, solicitud.curso);
  }

  private cargar() {
    this.cargando.set(true);
    this.error.set(null);

    this.adminInscripcionService
      .listar(this.estadoFiltro())
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (solicitudes) => this.solicitudes.set(solicitudes),
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  private quitarDeLista(id: string) {
    this.solicitudes.update((solicitudes) =>
      solicitudes.filter((solicitud) => solicitud.id !== id)
    );
  }
}
