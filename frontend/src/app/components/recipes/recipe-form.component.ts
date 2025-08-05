import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RecipeService } from '../../services/recipe.service';
import { DIFICULDADES, UNIDADES } from '../../models/recipe.model';

@Component({
  selector: 'app-recipe-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="form-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>{{ isEditMode ? 'Editar Receita' : 'Nova Receita' }}</mat-card-title>
        </mat-card-header>

        <mat-card-content>
          <form [formGroup]="recipeForm" (ngSubmit)="onSubmit()">
            <!-- Basic Info -->
            <div class="form-section">
              <h3>Informações Básicas</h3>
              
              <mat-form-field class="full-width">
                <mat-label>Nome da Receita</mat-label>
                <input matInput formControlName="nome" required>
                <mat-error *ngIf="recipeForm.get('nome')?.hasError('required')">
                  Nome é obrigatório
                </mat-error>
              </mat-form-field>

              <mat-form-field class="full-width">
                <mat-label>Descrição</mat-label>
                <textarea matInput formControlName="descricao" rows="3"></textarea>
              </mat-form-field>

              <div class="form-row">
                <mat-form-field>
                  <mat-label>Tempo de Preparo (min)</mat-label>
                  <input matInput type="number" formControlName="tempoPreparoMin">
                </mat-form-field>

                <mat-form-field>
                  <mat-label>Porções</mat-label>
                  <input matInput type="number" formControlName="porcoes">
                </mat-form-field>

                <mat-form-field>
                  <mat-label>Dificuldade</mat-label>
                  <mat-select formControlName="dificuldade">
                    <mat-option *ngFor="let dificuldade of dificuldades" [value]="dificuldade">
                      {{ dificuldade }}
                    </mat-option>
                  </mat-select>
                </mat-form-field>
              </div>
            </div>

            <!-- Ingredients -->
            <div class="form-section">
              <div class="section-header">
                <h3>Ingredientes</h3>
                <button mat-icon-button type="button" (click)="addIngredient()">
                  <mat-icon>add</mat-icon>
                </button>
              </div>

              <div formArrayName="ingredientes">
                <div *ngFor="let ingredient of ingredientes.controls; let i = index" 
                     [formGroupName]="i" class="ingredient-row">
                  <mat-form-field class="ingredient-name">
                    <mat-label>Ingrediente</mat-label>
                    <input matInput formControlName="nome" required>
                  </mat-form-field>

                  <mat-form-field class="ingredient-quantity">
                    <mat-label>Quantidade</mat-label>
                    <input matInput type="number" formControlName="quantidade">
                  </mat-form-field>

                  <mat-form-field class="ingredient-unit">
                    <mat-label>Unidade</mat-label>
                    <mat-select formControlName="unidade">
                      <mat-option *ngFor="let unidade of unidades" [value]="unidade">
                        {{ unidade }}
                      </mat-option>
                    </mat-select>
                  </mat-form-field>

                  <button mat-icon-button type="button" (click)="removeIngredient(i)" 
                          [disabled]="ingredientes.length <= 1">
                    <mat-icon>delete</mat-icon>
                  </button>
                </div>
              </div>
            </div>

            <!-- Steps -->
            <div class="form-section">
              <div class="section-header">
                <h3>Modo de Preparo</h3>
                <button mat-icon-button type="button" (click)="addStep()">
                  <mat-icon>add</mat-icon>
                </button>
              </div>

              <div formArrayName="passos">
                <div *ngFor="let step of passos.controls; let i = index" 
                     [formGroupName]="i" class="step-row">
                  <span class="step-number">{{ i + 1 }}.</span>
                  
                  <mat-form-field class="step-description">
                    <mat-label>Descrição do passo</mat-label>
                    <textarea matInput formControlName="descricao" rows="2" required></textarea>
                  </mat-form-field>

                  <button mat-icon-button type="button" (click)="removeStep(i)" 
                          [disabled]="passos.length <= 1">
                    <mat-icon>delete</mat-icon>
                  </button>
                </div>
              </div>
            </div>
          </form>
        </mat-card-content>

        <mat-card-actions>
          <button mat-button (click)="cancel()">Cancelar</button>
          <button mat-raised-button color="primary" (click)="onSubmit()" 
                  [disabled]="recipeForm.invalid || loading">
            <mat-spinner diameter="20" *ngIf="loading"></mat-spinner>
            <span *ngIf="!loading">{{ isEditMode ? 'Atualizar' : 'Criar' }}</span>
          </button>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [`
    .form-container {
      max-width: 800px;
      margin: 24px auto;
      padding: 0 16px;
    }

    .form-section {
      margin-bottom: 32px;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
    }

    .form-row {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
    }

    .form-row mat-form-field {
      flex: 1;
      min-width: 150px;
    }

    .ingredient-row,
    .step-row {
      display: flex;
      align-items: flex-start;
      gap: 16px;
      margin-bottom: 16px;
    }

    .ingredient-name {
      flex: 2;
      min-width: 200px;
    }

    .ingredient-quantity {
      flex: 1;
      min-width: 100px;
    }

    .ingredient-unit {
      flex: 1;
      min-width: 120px;
    }

    .step-number {
      font-weight: 500;
      margin-top: 16px;
      min-width: 24px;
    }

    .step-description {
      flex: 1;
    }

    mat-card-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
    }

    @media (max-width: 768px) {
      .form-row {
        flex-direction: column;
      }

      .ingredient-row,
      .step-row {
        flex-direction: column;
        align-items: stretch;
      }

      .step-number {
        margin-top: 0;
      }
    }
  `]
})
export class RecipeFormComponent implements OnInit {
  recipeForm: FormGroup;
  loading = false;
  isEditMode = false;
  recipeId?: number;
  dificuldades = DIFICULDADES;
  unidades = UNIDADES;

  constructor(
    private fb: FormBuilder,
    private recipeService: RecipeService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.recipeForm = this.createForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.recipeId = +params['id'];
        this.loadRecipe();
      }
    });
  }

  createForm(): FormGroup {
    return this.fb.group({
      nome: ['', Validators.required],
      descricao: [''],
      tempoPreparoMin: [''],
      porcoes: [''],
      dificuldade: [''],
      ingredientes: this.fb.array([this.createIngredientGroup()]),
      passos: this.fb.array([this.createStepGroup()])
    });
  }

  createIngredientGroup(): FormGroup {
    return this.fb.group({
      nome: ['', Validators.required],
      quantidade: [''],
      unidade: ['']
    });
  }

  createStepGroup(): FormGroup {
    return this.fb.group({
      descricao: ['', Validators.required]
    });
  }

  get ingredientes(): FormArray {
    return this.recipeForm.get('ingredientes') as FormArray;
  }

  get passos(): FormArray {
    return this.recipeForm.get('passos') as FormArray;
  }

  addIngredient(): void {
    this.ingredientes.push(this.createIngredientGroup());
  }

  removeIngredient(index: number): void {
    if (this.ingredientes.length > 1) {
      this.ingredientes.removeAt(index);
    }
  }

  addStep(): void {
    this.passos.push(this.createStepGroup());
  }

  removeStep(index: number): void {
    if (this.passos.length > 1) {
      this.passos.removeAt(index);
    }
  }

  loadRecipe(): void {
    if (!this.recipeId) return;

    this.loading = true;
    this.recipeService.getRecipeById(this.recipeId).subscribe({
      next: (recipe) => {
        this.populateForm(recipe);
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open('Erro ao carregar receita', 'Fechar', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        this.router.navigate(['/recipes']);
      }
    });
  }

  populateForm(recipe: any): void {
    this.recipeForm.patchValue({
      nome: recipe.nome,
      descricao: recipe.descricao,
      tempoPreparoMin: recipe.tempoPreparoMin,
      porcoes: recipe.porcoes,
      dificuldade: recipe.dificuldade
    });

    // Clear existing arrays
    while (this.ingredientes.length > 0) {
      this.ingredientes.removeAt(0);
    }
    while (this.passos.length > 0) {
      this.passos.removeAt(0);
    }

    // Add ingredients
    if (recipe.ingredientes && recipe.ingredientes.length > 0) {
      recipe.ingredientes.forEach((ing: any) => {
        const group = this.createIngredientGroup();
        group.patchValue(ing);
        this.ingredientes.push(group);
      });
    } else {
      this.ingredientes.push(this.createIngredientGroup());
    }

    // Add steps
    if (recipe.passos && recipe.passos.length > 0) {
      recipe.passos.forEach((passo: any) => {
        const group = this.createStepGroup();
        group.patchValue(passo);
        this.passos.push(group);
      });
    } else {
      this.passos.push(this.createStepGroup());
    }
  }

  onSubmit(): void {
    if (this.recipeForm.valid) {
      this.loading = true;
      
      const formData = { ...this.recipeForm.value };
      
      // Add ordem to steps
      formData.passos = formData.passos.map((passo: any, index: number) => ({
        ...passo,
        ordem: index + 1
      }));

      const request = this.isEditMode 
        ? this.recipeService.updateRecipe(this.recipeId!, formData)
        : this.recipeService.createRecipe(formData);

      request.subscribe({
        next: (response) => {
          this.loading = false;
          const message = this.isEditMode ? 'Receita atualizada com sucesso!' : 'Receita criada com sucesso!';
          this.snackBar.open(message, 'Fechar', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.router.navigate(['/recipes']);
        },
        error: (error) => {
          this.loading = false;
          const message = error.error?.message || 'Erro ao salvar receita';
          this.snackBar.open(message, 'Fechar', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/recipes']);
  }
}
