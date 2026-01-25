import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./public/public.module').then(m => m.PublicModule)
  },
  {
    path: '',
    loadChildren: () => import('./private/private.module').then(m => m.PrivateModule)
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
