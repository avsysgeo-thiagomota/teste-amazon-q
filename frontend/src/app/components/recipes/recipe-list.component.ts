import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatChipsModule } from '@angular/material/chips';
import { RecipeService } from '../../services/recipe.service';
import { Receita, DIFICULDADES } from '../../models/recipe.model';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-recipe-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
    MatChipsModule
  ],
  template: `
    <div class="recipe-list-container">
      <div class="header">
        <h1>Minhas Receitas</h1>
        <button mat-fab color="primary" (click)="createRecipe()" class="fab-button">
          <mat-icon>add</mat-icon>
        </button>
      </div>

      <div class="filters">
        <mat-form-field class="search-field">
          <mat-label>Buscar receitas</mat-label>
          <input matInput [formControl]="searchControl" placeholder="Digite o nome da receita">
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>

        <mat-form-field class="filter-field">
          <mat-label>Filtrar por dificuldade</mat-label>
          <mat-select [formControl]="difficultyControl">
            <mat-option value="">Todas</mat-option>
            <mat-option *ngFor="let dificuldade of dificuldades" [value]="dificuldade">
              {{ dificuldade }}
            </mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      <div class="loading-container" *ngIf="loading">
        <mat-spinner></mat-spinner>
      </div>

      <div class="recipes-grid" *ngIf="!loading">
        <mat-card *ngFor="let receita of recipes" class="recipe-card" (click)="viewRecipe(receita.id!)">
          <mat-card-header>
            <mat-card-title>{{ receita.nome }}</mat-card-title>
            <mat-card-subtitle>
              <mat-chip-set>
                <mat-chip *ngIf="receita.dificuldade" [color]="getDifficultyColor(receita.dificuldade)">
                  {{ receita.dificuldade }}
                </mat-chip>
                <mat-chip *ngIf="receita.tempoPreparoMin">
                  <mat-icon>schedule</mat-icon>
                  {{ receita.tempoPreparoMin }} min
                </mat-chip>
                <mat-chip *ngIf="receita.porcoes">
                  <mat-icon>people</mat-icon>
                  {{ receita.porcoes }} porções
                </mat-chip>
              </mat-chip-set>
            </mat-card-subtitle>
          </mat-card-header>

          <mat-card-content>
            <p class="recipe-description">{{ receita.descricao || 'Sem descrição' }}</p>
            <div class="recipe-stats">
              <span *ngIf="receita.ingredientes?.length">
                <mat-icon>restaurant</mat-icon>
                {{ receita.ingredientes.length }} ingredientes
              </span>
              <span *ngIf="receita.passos?.length">
                <mat-icon>list</mat-icon>
                {{ receita.passos.length }} passos
              </span>
            </div>
          </mat-card-content>

          <mat-card-actions>
            <button mat-button color="primary" (click)="editRecipe(receita.id!, $event)">
              <mat-icon>edit</mat-icon>
              Editar
            </button>
            <button mat-button color="warn" (click)="deleteRecipe(receita.id!, receita.nome, $event)">
              <mat-icon>delete</mat-icon>
              Excluir
            </button>
          </mat-card-actions>
        </mat-card>

        <div *ngIf="recipes.length === 0" class="no-recipes">
          <mat-icon>restaurant_menu</mat-icon>
          <h3>Nenhuma receita encontrada</h3>
          <p>Comece criando sua primeira receita!</p>
          <button mat-raised-button color="primary" (click)="createRecipe()">
            <mat-icon>add</mat-icon>
            Criar Receita
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .recipe-list-container {
      padding: 24px;
      max-width: 1200px;
      margin: 0 auto;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
    }

    .fab-button {
      position: fixed;
      bottom: 24px;
      right: 24px;
      z-index: 1000;
    }

    .filters {
      display: flex;
      gap: 16px;
      margin-bottom: 24px;
      flex-wrap: wrap;
    }

    .search-field {
      flex: 1;
      min-width: 300px;
    }

    .filter-field {
      min-width: 200px;
    }

    .recipes-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 24px;
    }

    .recipe-card {
      cursor: pointer;
      transition: transform 0.2s ease, box-shadow 0.2s ease;
    }

    .recipe-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    }

    .recipe-description {
      color: rgba(0,0,0,0.6);
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .recipe-stats {
      display: flex;
      gap: 16px;
      align-items: center;
      font-size: 0.875rem;
      color: rgba(0,0,0,0.6);
    }

    .recipe-stats span {
      display: flex;
      align-items: center;
      gap: 4px;
    }

    .recipe-stats mat-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
    }

    .no-recipes {
      grid-column: 1 / -1;
      text-align: center;
      padding: 48px 24px;
      color: rgba(0,0,0,0.6);
    }

    .no-recipes mat-icon {
      font-size: 64px;
      width: 64px;
      height: 64px;
      margin-bottom: 16px;
      color: rgba(0,0,0,0.3);
    }

    mat-chip-set {
      margin-bottom: 8px;
    }

    mat-chip {
      font-size: 0.75rem;
    }

    mat-chip mat-icon {
      font-size: 14px;
      width: 14px;
      height: 14px;
      margin-right: 4px;
    }

    @media (max-width: 768px) {
      .recipe-list-container {
        padding: 16px;
      }

      .recipes-grid {
        grid-template-columns: 1fr;
        gap: 16px;
      }

      .filters {
        flex-direction: column;
      }

      .search-field,
      .filter-field {
        min-width: unset;
      }

      .fab-button {
        bottom: 16px;
        right: 16px;
      }
    }
  `]
})
export class RecipeListComponent implements OnInit {
  recipes: Receita[] = [];
  loading = false;
  dificuldades = DIFICULDADES;
  
  searchControl = new FormControl('');
  difficultyControl = new FormControl('');

  constructor(
    private recipeService: RecipeService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadRecipes();
    this.setupFilters();
  }

  setupFilters(): void {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => this.loadRecipes());

    this.difficultyControl.valueChanges
      .subscribe(() => this.loadRecipes());
  }

  loadRecipes(): void {
    this.loading = true;
    
    const filters = {
      nome: this.searchControl.value || undefined,
      dificuldade: this.difficultyControl.value || undefined,
      withDetails: true
    };

    this.recipeService.getRecipes(filters).subscribe({
      next: (recipes) => {
        this.recipes = recipes;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open('Erro ao carregar receitas', 'Fechar', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  createRecipe(): void {
    this.router.navigate(['/recipes/new']);
  }

  viewRecipe(id: number): void {
    this.router.navigate(['/recipes', id]);
  }

  editRecipe(id: number, event: Event): void {
    event.stopPropagation();
    this.router.navigate(['/recipes', id, 'edit']);
  }

  deleteRecipe(id: number, name: string, event: Event): void {
    event.stopPropagation();
    
    if (confirm(`Tem certeza que deseja excluir a receita "${name}"?`)) {
      this.recipeService.deleteRecipe(id).subscribe({
        next: () => {
          this.snackBar.open('Receita excluída com sucesso!', 'Fechar', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.loadRecipes();
        },
        error: (error) => {
          const message = error.error?.message || 'Erro ao excluir receita';
          this.snackBar.open(message, 'Fechar', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  getDifficultyColor(dificuldade: string): string {
    switch (dificuldade) {
      case 'Fácil': return 'primary';
      case 'Médio': return 'accent';
      case 'Difícil': return 'warn';
      default: return '';
    }
  }
}
