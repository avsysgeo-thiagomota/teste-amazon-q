import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="register-container">
      <mat-card class="register-card">
        <mat-card-header>
          <mat-card-title>Criar Conta</mat-card-title>
          <mat-card-subtitle>Cadastre-se para começar a usar</mat-card-subtitle>
        </mat-card-header>
        
        <mat-card-content>
          <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
            <mat-form-field class="full-width">
              <mat-label>Usuário</mat-label>
              <input matInput formControlName="username" required>
              <mat-icon matSuffix>person</mat-icon>
              <mat-error *ngIf="registerForm.get('username')?.hasError('required')">
                Usuário é obrigatório
              </mat-error>
              <mat-error *ngIf="registerForm.get('username')?.hasError('minlength')">
                Usuário deve ter pelo menos 3 caracteres
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
              <mat-error *ngIf="registerForm.get('email')?.hasError('email')">
                Email deve ter um formato válido
              </mat-error>
            </mat-form-field>

            <mat-form-field class="full-width">
              <mat-label>Senha</mat-label>
              <input matInput type="password" formControlName="password" required>
              <mat-icon matSuffix>lock</mat-icon>
              <mat-error *ngIf="registerForm.get('password')?.hasError('required')">
                Senha é obrigatória
              </mat-error>
              <mat-error *ngIf="registerForm.get('password')?.hasError('minlength')">
                Senha deve ter pelo menos 6 caracteres
              </mat-error>
            </mat-form-field>

            <mat-form-field class="full-width">
              <mat-label>Confirmar Senha</mat-label>
              <input matInput type="password" formControlName="confirmPassword" required>
              <mat-icon matSuffix>lock</mat-icon>
              <mat-error *ngIf="registerForm.get('confirmPassword')?.hasError('required')">
                Confirmação de senha é obrigatória
              </mat-error>
              <mat-error *ngIf="registerForm.hasError('passwordMismatch')">
                Senhas não coincidem
              </mat-error>
            </mat-form-field>

            <div class="register-actions">
              <button mat-raised-button color="primary" type="submit" 
                      [disabled]="registerForm.invalid || loading" class="full-width">
                <mat-spinner diameter="20" *ngIf="loading"></mat-spinner>
                <span *ngIf="!loading">Cadastrar</span>
              </button>
            </div>
          </form>
        </mat-card-content>
        
        <mat-card-actions>
          <p class="text-center">
            Já tem uma conta? 
            <a routerLink="/login" class="login-link">Faça login aqui</a>
          </p>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [`
    .register-container {
      min-height: 100vh;
      display: flex;
      justify-content: center;
      align-items: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }

    .register-card {
      width: 100%;
      max-width: 450px;
      padding: 20px;
    }

    .register-actions {
      margin-top: 20px;
    }

    .login-link {
      color: #3f51b5;
      text-decoration: none;
    }

    .login-link:hover {
      text-decoration: underline;
    }

    mat-form-field {
      margin-bottom: 16px;
    }

    mat-spinner {
      margin-right: 8px;
    }
  `]
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      nomeCompleto: [''],
      email: ['', [Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });

    // Redirect if already logged in
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/recipes']);
    }
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.loading = true;
      
      const { confirmPassword, ...userData } = this.registerForm.value;
      
      this.authService.register(userData).subscribe({
        next: (response) => {
          this.loading = false;
          this.snackBar.open('Cadastro realizado com sucesso! Faça login para continuar.', 'Fechar', {
            duration: 5000,
            panelClass: ['success-snackbar']
          });
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.loading = false;
          const message = error.error?.message || 'Erro ao criar conta. Tente novamente.';
          this.snackBar.open(message, 'Fechar', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }
}
