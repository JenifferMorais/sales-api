import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { provideEnvironmentNgxMask } from 'ngx-mask';
import { SharedModule } from '../../shared/shared.module';
import { CustomersListComponent } from './customers-list/customers-list.component';
import { CustomersFormComponent } from './customers-form/customers-form.component';
import { CustomersDetailComponent } from './customers-detail/customers-detail.component';

const routes: Routes = [
  { path: '', component: CustomersListComponent },
  { path: 'new', component: CustomersFormComponent },
  { path: ':code', component: CustomersDetailComponent },
  { path: ':code/edit', component: CustomersFormComponent }
];

@NgModule({
  declarations: [
    CustomersListComponent,
    CustomersFormComponent,
    CustomersDetailComponent
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
export class CustomersModule { }
