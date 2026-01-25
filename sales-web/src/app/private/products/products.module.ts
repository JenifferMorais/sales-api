import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { ProductsListComponent } from './products-list/products-list.component';
import { ProductsFormComponent } from './products-form/products-form.component';
import { ProductsDetailComponent } from './products-detail/products-detail.component';

const routes: Routes = [
  { path: '', component: ProductsListComponent },
  { path: 'new', component: ProductsFormComponent },
  { path: ':code', component: ProductsDetailComponent },
  { path: ':code/edit', component: ProductsFormComponent }
];

@NgModule({
  declarations: [
    ProductsListComponent,
    ProductsFormComponent,
    ProductsDetailComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    RouterModule.forChild(routes)
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProductsModule { }
