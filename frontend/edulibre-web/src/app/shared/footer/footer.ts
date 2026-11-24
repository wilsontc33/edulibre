import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthModalService } from '../../core/services/auth-modal';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './footer.html'
})
export class Footer {
  private readonly authModalService = inject(AuthModalService);
  public readonly authService = inject(AuthService);

  readonly year = new Date().getFullYear();

  abrirLogin() {
    this.authModalService.abrirModal('login');
  }
}
