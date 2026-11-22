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
  private authModalService = inject(AuthModalService);
  private router = inject(Router);
  public authService = inject(AuthService);

  protected readonly menuAbierto = signal(false);

  abrirLogin() {
    this.authModalService.abrirModal('login');
  }

  toggleMenu() {
    this.menuAbierto.update(value => !value);
  }

  cerrarMenu() {
    this.menuAbierto.set(false);
  }

  logout() {
    this.cerrarMenu();
    this.authService.logout();
    this.router.navigate(['/']);
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    this.menuAbierto.set(false);
  }
}
