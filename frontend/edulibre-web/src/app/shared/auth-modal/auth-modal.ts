import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { AuthModalService } from '../../core/services/auth-modal';
import { AuthService } from '../../core/services/auth';
import { extraerMensajeError } from '../../core/interceptors/error.interceptor';

@Component({
  selector: 'app-auth-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth-modal.html'
})
export class AuthModal {
  private authModalService = inject(AuthModalService);
  private authService = inject(AuthService);

  isOpen = signal(false);
  tabActual: 'login' | 'registro' = 'login';
  cargando = signal(false);
  error = signal<string | null>(null);

  loginData = { email: '', password: '' };
  registroData = { nombre: '', email: '', password: '' };

  constructor() {
    this.authModalService.modalState$.subscribe(estado => {
      this.isOpen.set(estado.abierto);
      if (estado.tab) this.tabActual = estado.tab;
      if (!estado.abierto) this.error.set(null);
    });
  }

  cerrarModal() {
    this.authModalService.cerrarModal();
  }

  cambiarTab(tab: 'login' | 'registro') {
    this.tabActual = tab;
    this.error.set(null);
  }

  onLogin() {
    if (!this.loginData.email || !this.loginData.password) {
      this.error.set('Ingresa tu correo y contraseña.');
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    this.authService
      .login(this.loginData)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: () => {
          this.loginData = { email: '', password: '' };
          this.cerrarModal();
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }

  onRegistro() {
    if (!this.registroData.nombre || !this.registroData.email || !this.registroData.password) {
      this.error.set('Completa todos los campos para crear tu cuenta.');
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    this.authService
      .register(this.registroData)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: () => {
          this.registroData = { nombre: '', email: '', password: '' };
          this.cerrarModal();
        },
        error: (err) => this.error.set(extraerMensajeError(err))
      });
  }
}
