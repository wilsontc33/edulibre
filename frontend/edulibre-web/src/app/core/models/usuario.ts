import { Rol } from './auth';

export interface Usuario {
  id: string;
  nombre: string;
  email: string;
  rol: Rol;
  creadoEn: string;
}
