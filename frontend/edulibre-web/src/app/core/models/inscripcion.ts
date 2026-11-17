export type EstadoInscripcion = 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';

export interface CrearInscripcionRequest {
  cursoId: string;
  motivacion: string;
  nivelPrevio: string;
  horasSemana: number;
}

export interface Inscripcion {
  id: string;
  cursoId: string;
  curso: string;
  estado: EstadoInscripcion;
  motivoRechazo: string | null;
  creadoEn: string;
  resueltoEn: string | null;
}

export interface AdminInscripcion {
  id: string;
  nombre: string;
  email: string;
  cursoId: string;
  curso: string;
  nivel: string;
  motivacion: string;
  estado: EstadoInscripcion;
  comentarioAdmin: string | null;
  creadoEn: string;
  resueltoEn: string | null;
}
