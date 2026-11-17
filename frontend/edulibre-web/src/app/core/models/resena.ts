export interface CrearResenaRequest {
  calificacion: number;
  comentario: string;
}

export interface Resena {
  id: string;
  usuario: string;
  calificacion: number;
  comentario: string;
  creadoEn: string;
}
