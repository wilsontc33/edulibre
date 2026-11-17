import { Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthModalService } from '../../core/services/auth-modal';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './header.html'
})
export class Header {
  private authModalService = inject(AuthModalService);
  private router = inject(Router);
  public authService = inject(AuthService);

  abrirLogin() {
    this.authModalService.abrirModal('login');
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
