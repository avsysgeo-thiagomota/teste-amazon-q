import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Receita, ReceitaRequest } from '../models/recipe.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getRecipes(filters?: { nome?: string; dificuldade?: string; withDetails?: boolean }): Observable<Receita[]> {
    let params = new HttpParams();
    
    if (filters?.nome) {
      params = params.set('nome', filters.nome);
    }
    if (filters?.dificuldade) {
      params = params.set('dificuldade', filters.dificuldade);
    }
    if (filters?.withDetails) {
      params = params.set('withDetails', 'true');
    }

    return this.http.get<Receita[]>(`${this.apiUrl}/receitas`, { params });
  }

  getRecipeById(id: number): Observable<Receita> {
    return this.http.get<Receita>(`${this.apiUrl}/receitas/${id}`);
  }

  createRecipe(recipe: ReceitaRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/receitas`, recipe);
  }

  updateRecipe(id: number, recipe: ReceitaRequest): Observable<any> {
    return this.http.put(`${this.apiUrl}/receitas/${id}`, recipe);
  }

  deleteRecipe(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/receitas/${id}`);
  }

  getRecipeCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/receitas/count`);
  }
}
