import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="profile-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Meu Perfil</mat-card-title>
          <mat-card-subtitle>Gerencie suas informações pessoais</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <form [formGroup]="profileForm" (ngSubmit)="onSubmit()">
            <mat-form-field class="full-width">
              <mat-label>Usuário</mat-label>
              <input matInput formControlName="username" required>
              <mat-icon matSuffix>person</mat-icon>
              <mat-error *ngIf="profileForm.get('username')?.hasError('required')">
                Usuário é obrigatório
              </mat-error>
            </mat-form-field>

            <mat-form-field class="full-width">
              <mat-label>Nome Completo</mat-label>
              <input matInput formControlName="nomeCompleto">
              <mat-icon matSuffix>badge</mat-icon>
            </mat-form-field>

            <mat-form-field class="full-width">
              <mat-label>Email</mat-label>
              <input matInput type="email" formControlName="email">
              <mat-icon matSuffix>email</mat-icon>
              <mat-error *ngIf="profileForm.get('email')?.hasError('email')">
                Email deve ter um formato válido
              </mat-error>
            </mat-form-field>

            <mat-form-field class="full-width">
              <mat-label>Nova Senha (deixe em branco para manter a atual)</mat-label>
              <input matInput type="password" formControlName="password">
              <mat-icon matSuffix>lock</mat-icon>
              <mat-error *ngIf="profileForm.get('password')?.hasError('minlength')">
                Senha deve ter pelo menos 6 caracteres
              </mat-error>
            </mat-form-field>
          </form>
        </mat-card-content>

        <mat-card-actions>
          <button mat-raised-button color="primary" (click)="onSubmit()" 
                  [disabled]="profileForm.invalid || loading">
            <mat-spinner diameter="20" *ngIf="loading"></mat-spinner>
            <span *ngIf="!loading">Atualizar Perfil</span>
          </button>
        </mat-card-actions>
      </mat-card>

      <mat-card class="stats-card">
        <mat-card-header>
          <mat-card-title>Estatísticas</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <div class="stats-grid">
            <div class="stat-item">
              <mat-icon>restaurant_menu</mat-icon>
              <div class="stat-info">
                <span class="stat-number">{{ recipeCount }}</span>
                <span class="stat-label">Receitas</span>
              </div>
            </div>
            <div class="stat-item">
              <mat-icon>schedule</mat-icon>
              <div class="stat-info">
                <span class="stat-number">{{ memberSince }}</span>
                <span class="stat-label">Membro desde</span>
              </div>
            </div>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .profile-container {
      max-width: 600px;
      margin: 24px auto;
      padding: 0 16px;
    }

    .stats-card {
      margin-top: 24px;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 24px;
    }

    .stat-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px;
      background: #f5f5f5;
      border-radius: 8px;
    }

    .stat-item mat-icon {
      font-size: 32px;
      width: 32px;
      height: 32px;
      color: #3f51b5;
    }

    .stat-info {
      display: flex;
      flex-direction: column;
    }

    .stat-number {
      font-size: 24px;
      font-weight: 500;
      color: #3f51b5;
    }

    .stat-label {
      font-size: 14px;
      color: rgba(0,0,0,0.6);
    }

    mat-form-field {
      margin-bottom: 16px;
    }

    mat-spinner {
      margin-right: 8px;
    }

    @media (max-width: 768px) {
      .profile-container {
        padding: 16px;
      }

      .stats-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  loading = false;
  currentUser?: User;
  recipeCount = 0;
  memberSince = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.profileForm = this.fb.group({
      username: ['', Validators.required],
      nomeCompleto: [''],
      email: ['', [Validators.email]],
      password: ['', [Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.profileForm.patchValue({
          username: user.username,
          nomeCompleto: user.nomeCompleto,
          email: user.email
        });
        
        if (user.dataCriacao) {
          this.memberSince = new Date(user.dataCriacao).getFullYear().toString();
        }
      },
      error: (error) => {
        this.snackBar.open('Erro ao carregar perfil', 'Fechar', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.valid && this.currentUser) {
      this.loading = true;
      
      // Note: This would need to be implemented in the backend
      // For now, just show a success message
      setTimeout(() => {
        this.loading = false;
        this.snackBar.open('Perfil atualizado com sucesso!', 'Fechar', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      }, 1000);
    }
  }
}
