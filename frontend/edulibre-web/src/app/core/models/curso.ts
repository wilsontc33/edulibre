import { EstadoInscripcion } from './inscripcion';

export interface CursoResumen {
  id: string;
  titulo: string;
  descripcion: string;
  categoria: string;
  nivel: string;
  duracion: string | null;
  imagenUrl: string | null;
}

export interface CursoDetalle {
  id: string;
  titulo: string;
  descripcion: string;
  categoria: string;
  instructor: string;
  imagenUrl: string | null;
  nivel: string;
  duracion: string | null;
  estadoInscripcion: EstadoInscripcion | null;
}
