export interface CursoAdmin {
  id: string;
  titulo: string;
  descripcion: string | null;
  categoria: string;
  categoriaSlug: string;
  instructor: string;
  imagenUrl: string | null;
  nivel: string | null;
  duracionSemanas: number | null;
  publicado: boolean;
  creadoEn: string;
  totalLecciones: number;
  totalInscritos: number;
}

export interface CursoAdminDetalle extends CursoAdmin {
  lecciones: LeccionAdmin[];
}

export interface LeccionAdmin {
  id: string;
  titulo: string;
  orden: number;
  youtubeId: string;
  duracionMin: number | null;
  duracion: string | null;
}

export interface CursoAdminRequest {
  titulo: string;
  descripcion: string | null;
  categoriaSlug: string;
  instructor: string;
  imagenUrl: string | null;
  nivel: string | null;
  duracionSemanas: number | null;
  publicado: boolean;
}

export interface LeccionRequest {
  titulo: string;
  orden: number | null;
  youtubeId: string;
  duracionMin: number | null;
}
