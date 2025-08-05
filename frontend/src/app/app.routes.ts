import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/recipes',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'recipes',
    canActivate: [AuthGuard],
    loadComponent: () => import('./components/recipes/recipe-list.component').then(m => m.RecipeListComponent)
  },
  {
    path: 'recipes/new',
    canActivate: [AuthGuard],
    loadComponent: () => import('./components/recipes/recipe-form.component').then(m => m.RecipeFormComponent)
  },
  {
    path: 'recipes/:id',
    canActivate: [AuthGuard],
    loadComponent: () => import('./components/recipes/recipe-detail.component').then(m => m.RecipeDetailComponent)
  },
  {
    path: 'recipes/:id/edit',
    canActivate: [AuthGuard],
    loadComponent: () => import('./components/recipes/recipe-form.component').then(m => m.RecipeFormComponent)
  },
  {
    path: 'profile',
    canActivate: [AuthGuard],
    loadComponent: () => import('./components/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: '**',
    redirectTo: '/recipes'
  }
];
