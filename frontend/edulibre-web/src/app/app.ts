import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './shared/header/header';
import { AuthModal } from './shared/auth-modal/auth-modal';

@Component({
  imports: [RouterOutlet, Header, AuthModal],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('edulibre-web');
}
