import { Routes } from '@angular/router';
import { Landing } from './features/landing/landing';
import { MisPostulaciones } from './features/estudiante/mis-postulaciones/mis-postulaciones';
import { MisCursos } from './features/estudiante/mis-cursos/mis-cursos';
import { MiProgreso } from './features/estudiante/mi-progreso/mi-progreso';
import { PostulacionesPendientes } from './features/admin/postulaciones-pendientes/postulaciones-pendientes';
import { AdminResumen } from './features/admin/resumen/admin-resumen';
import { AdminCursos } from './features/admin/cursos/admin-cursos';
import { AdminCategorias } from './features/admin/categorias/admin-categorias';
import { AdminCursoEditor } from './features/admin/cursos/admin-curso-editor';
import { CursoDetalle } from './features/curso/curso-detalle/curso-detalle';
import { Catalogo } from './features/curso/catalogo/catalogo';
import { Perfil } from './features/estudiante/perfil/perfil';
import { PanelLayout } from './shared/panel-layout/panel-layout';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', component: Landing },
  { path: 'cursos', component: Catalogo },
  { path: 'curso/:id', component: CursoDetalle },
  {
    path: '',
    component: PanelLayout,
    canActivate: [authGuard],
    children: [
      { path: 'perfil', component: Perfil },
      { path: 'mis-cursos', component: MisCursos },
      { path: 'mi-progreso', component: MiProgreso },
      { path: 'mis-postulaciones', component: MisPostulaciones },
      {
        path: 'admin',
        canActivate: [adminGuard],
        children: [
          { path: '', component: AdminResumen },
          { path: 'postulaciones', component: PostulacionesPendientes },
          { path: 'cursos', component: AdminCursos },
          { path: 'categorias', component: AdminCategorias },
          { path: 'cursos/nuevo', component: AdminCursoEditor },
          { path: 'cursos/:id', component: AdminCursoEditor }
        ]
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
