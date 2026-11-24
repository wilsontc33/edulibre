export type Rol = 'ADMIN' | 'ESTUDIANTE';

export interface AuthResponse {
  token?: string | null;
  email: string;
  nombre: string;
  rol: 'ESTUDIANTE' | 'ADMIN';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
}
