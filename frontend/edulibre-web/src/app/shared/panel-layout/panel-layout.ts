import { Component, computed, HostListener, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth';

interface NavItem {
  path: string;
  icon: string;
  label: string;
  exact: boolean;
}

@Component({
  selector: 'app-panel-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './panel-layout.html'
})
export class PanelLayout {
  public readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly drawerOpen = signal(false);

  private readonly studentItems: NavItem[] = [
    { path: '/perfil', icon: 'dashboard', label: 'Mi Panel', exact: true },
    { path: '/mis-cursos', icon: 'school', label: 'Mis Cursos', exact: true },
    { path: '/mi-progreso', icon: 'trending_up', label: 'Mi Progreso', exact: true },
    { path: '/mis-postulaciones', icon: 'assignment', label: 'Mis Postulaciones', exact: true }
  ];

  private readonly adminItems: NavItem[] = [
    { path: '/admin', icon: 'space_dashboard', label: 'Resumen', exact: true },
    { path: '/admin/postulaciones', icon: 'fact_check', label: 'Postulaciones', exact: true },
    { path: '/admin/cursos', icon: 'school', label: 'Cursos', exact: false },
    { path: '/admin/categorias', icon: 'category', label: 'Categorías', exact: true }
  ];

  protected readonly items = computed(() =>
    this.authService.isAdmin() ? this.adminItems : this.studentItems
  );

  protected readonly seccionTitulo = computed(() =>
    this.authService.isAdmin() ? 'ADMINISTRACIÓN' : 'MENÚ'
  );

  constructor() {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => this.drawerOpen.set(false));
  }

  toggleDrawer() {
    this.drawerOpen.update(value => !value);
  }

  cerrarDrawer() {
    this.drawerOpen.set(false);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    this.drawerOpen.set(false);
  }
}
