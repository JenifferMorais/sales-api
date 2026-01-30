import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { provideEnvironmentNgxMask } from 'ngx-mask';
import { SharedModule } from '../../shared/shared.module';
import { SalesListComponent } from './sales-list/sales-list.component';
import { SalesFormComponent } from './sales-form/sales-form.component';
import { SalesDetailComponent } from './sales-detail/sales-detail.component';

const routes: Routes = [
  { path: '', component: SalesListComponent },
  { path: 'new', component: SalesFormComponent },
  { path: ':code', component: SalesDetailComponent },
  { path: ':code/edit', component: SalesFormComponent }
];

@NgModule({
  declarations: [
    SalesListComponent,
    SalesFormComponent,
    SalesDetailComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    RouterModule.forChild(routes)
  ],
  providers: [provideEnvironmentNgxMask()],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SalesModule { }
