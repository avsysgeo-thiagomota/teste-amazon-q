export interface Ingrediente {
  id?: number;
  nome: string;
  quantidade?: number;
  unidade?: string;
  receitaId?: number;
}

export interface Passo {
  id?: number;
  ordem: number;
  descricao: string;
  receitaId?: number;
}

export interface Receita {
  id?: number;
  nome: string;
  descricao?: string;
  tempoPreparoMin?: number;
  porcoes?: number;
  dificuldade?: string;
  usuarioId?: number;
  ingredientes: Ingrediente[];
  passos: Passo[];
}

export interface ReceitaRequest {
  nome: string;
  descricao?: string;
  tempoPreparoMin?: number;
  porcoes?: number;
  dificuldade?: string;
  ingredientes: IngredienteRequest[];
  passos: PassoRequest[];
}

export interface IngredienteRequest {
  nome: string;
  quantidade?: number;
  unidade?: string;
}

export interface PassoRequest {
  ordem?: number;
  descricao: string;
}

export const DIFICULDADES = [
  'Fácil',
  'Médio',
  'Difícil'
];

export const UNIDADES = [
  'xícara',
  'xícaras',
  'colher de sopa',
  'colheres de sopa',
  'colher de chá',
  'colheres de chá',
  'kg',
  'g',
  'ml',
  'l',
  'unidade',
  'unidades',
  'dente',
  'dentes',
  'pitada',
  'a gosto'
];
