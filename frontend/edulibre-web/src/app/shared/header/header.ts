import { Component, HostListener, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { AuthModalService } from '../../core/services/auth-modal';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, RouterLink],
  templateUrl: './header.html'
})
export class Header {
  private readonly authModalService = inject(AuthModalService);
  private readonly router = inject(Router);
  public readonly authService = inject(AuthService);

  protected readonly menuAbierto = signal(false);
  protected readonly mobileMenuAbierto = signal(false);

  abrirLogin() {
    this.cerrarMenus();
    this.authModalService.abrirModal('login');
  }

  toggleMenu() {
    this.menuAbierto.update(value => !value);
  }

  toggleMobileMenu() {
    this.mobileMenuAbierto.update(value => !value);
  }

  cerrarMenus() {
    this.menuAbierto.set(false);
    this.mobileMenuAbierto.set(false);
  }

  cerrarMenu() {
    this.menuAbierto.set(false);
  }

  cerrarMobileMenu() {
    this.mobileMenuAbierto.set(false);
  }

  logout() {
    this.cerrarMenus();
    this.authService.logout();
    this.router.navigate(['/']);
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    this.cerrarMenus();
  }
}
