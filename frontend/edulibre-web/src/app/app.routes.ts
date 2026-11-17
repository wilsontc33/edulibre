import { Routes } from '@angular/router';
import { Landing } from './features/landing/landing';
import { MisPostulaciones } from './features/estudiante/mis-postulaciones/mis-postulaciones';
import { PostulacionesPendientes } from './features/admin/postulaciones-pendientes/postulaciones-pendientes';
import { CursoDetalle } from './features/curso/curso-detalle/curso-detalle';
import { Catalogo } from './features/curso/catalogo/catalogo';
import { Perfil } from './features/estudiante/perfil/perfil';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', component: Landing },
  { path: 'mis-postulaciones', component: MisPostulaciones, canActivate: [authGuard] },
  { path: 'admin', component: PostulacionesPendientes, canActivate: [adminGuard] },
  { path: 'cursos', component: Catalogo },
  { path: 'curso/:id', component: CursoDetalle },
  { path: 'perfil', component: Perfil, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
