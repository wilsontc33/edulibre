export interface Leccion {
  id: string;
  titulo: string;
  orden: number;
  youtubeId: string | null;
  duracionMin: number | null;
  duracion: string | null;
}
