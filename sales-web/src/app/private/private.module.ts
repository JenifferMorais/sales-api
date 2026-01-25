import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PrivateRoutingModule } from './private-routing.module';
import { PrivateLayoutComponent } from './private-layout/private-layout.component';
import { LayoutModule } from '../layout/layout.module';

@NgModule({
  declarations: [
    PrivateLayoutComponent
  ],
  imports: [
    CommonModule,
    PrivateRoutingModule,
    LayoutModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PrivateModule { }
