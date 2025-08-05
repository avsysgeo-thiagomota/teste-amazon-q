import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RecipeService } from '../../services/recipe.service';
import { Receita } from '../../models/recipe.model';

@Component({
  selector: 'app-recipe-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="recipe-detail-container" *ngIf="!loading && recipe">
      <div class="header">
        <button mat-icon-button (click)="goBack()">
          <mat-icon>arrow_back</mat-icon>
        </button>
        <h1>{{ recipe.nome }}</h1>
        <div class="actions">
          <button mat-raised-button color="primary" (click)="editRecipe()">
            <mat-icon>edit</mat-icon>
            Editar
          </button>
        </div>
      </div>

      <div class="recipe-info">
        <mat-chip-set>
          <mat-chip *ngIf="recipe.dificuldade">{{ recipe.dificuldade }}</mat-chip>
          <mat-chip *ngIf="recipe.tempoPreparoMin">
            <mat-icon>schedule</mat-icon>
            {{ recipe.tempoPreparoMin }} min
          </mat-chip>
          <mat-chip *ngIf="recipe.porcoes">
            <mat-icon>people</mat-icon>
            {{ recipe.porcoes }} porções
          </mat-chip>
        </mat-chip-set>
      </div>

      <mat-card *ngIf="recipe.descricao" class="description-card">
        <mat-card-header>
          <mat-card-title>Descrição</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>{{ recipe.descricao }}</p>
        </mat-card-content>
      </mat-card>

      <mat-card class="ingredients-card">
        <mat-card-header>
          <mat-card-title>Ingredientes</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <ul class="ingredients-list">
            <li *ngFor="let ingrediente of recipe.ingredientes">
              <span class="ingredient-quantity" *ngIf="ingrediente.quantidade">
                {{ ingrediente.quantidade }}
              </span>
              <span class="ingredient-unit" *ngIf="ingrediente.unidade">
                {{ ingrediente.unidade }}
              </span>
              <span class="ingredient-name">{{ ingrediente.nome }}</span>
            </li>
          </ul>
        </mat-card-content>
      </mat-card>

      <mat-card class="steps-card">
        <mat-card-header>
          <mat-card-title>Modo de Preparo</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <ol class="steps-list">
            <li *ngFor="let passo of recipe.passos">
              {{ passo.descricao }}
            </li>
          </ol>
        </mat-card-content>
      </mat-card>
    </div>

    <div class="loading-container" *ngIf="loading">
      <mat-spinner></mat-spinner>
    </div>
  `,
  styles: [`
    .recipe-detail-container {
      max-width: 800px;
      margin: 0 auto;
      padding: 24px;
    }

    .header {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 24px;
    }

    .header h1 {
      flex: 1;
      margin: 0;
    }

    .recipe-info {
      margin-bottom: 24px;
    }

    .description-card,
    .ingredients-card,
    .steps-card {
      margin-bottom: 24px;
    }

    .ingredients-list {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    .ingredients-list li {
      padding: 8px 0;
      border-bottom: 1px solid #eee;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .ingredients-list li:last-child {
      border-bottom: none;
    }

    .ingredient-quantity {
      font-weight: 500;
      min-width: 60px;
    }

    .ingredient-unit {
      color: rgba(0,0,0,0.6);
      min-width: 80px;
    }

    .ingredient-name {
      flex: 1;
    }

    .steps-list {
      padding-left: 20px;
    }

    .steps-list li {
      margin-bottom: 16px;
      line-height: 1.5;
    }

    mat-chip mat-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
      margin-right: 4px;
    }

    @media (max-width: 768px) {
      .recipe-detail-container {
        padding: 16px;
      }

      .header {
        flex-wrap: wrap;
      }

      .actions {
        width: 100%;
        margin-top: 16px;
      }
    }
  `]
})
export class RecipeDetailComponent implements OnInit {
  recipe?: Receita;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private recipeService: RecipeService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      if (id) {
        this.loadRecipe(id);
      }
    });
  }

  loadRecipe(id: number): void {
    this.loading = true;
    this.recipeService.getRecipeById(id).subscribe({
      next: (recipe) => {
        this.recipe = recipe;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.router.navigate(['/recipes']);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/recipes']);
  }

  editRecipe(): void {
    if (this.recipe?.id) {
      this.router.navigate(['/recipes', this.recipe.id, 'edit']);
    }
  }
}
