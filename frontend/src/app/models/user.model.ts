export interface User {
  id: number;
  username: string;
  nomeCompleto?: string;
  email?: string;
  ativo: boolean;
  dataCriacao: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  nomeCompleto?: string;
  email?: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  nomeCompleto?: string;
  email?: string;
}
