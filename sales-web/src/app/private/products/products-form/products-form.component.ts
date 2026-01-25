import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { AlertService } from '../../../shared/services/alert/alert.service';
import { Product, ProductRequest } from '../../../core/models/product.model';

@Component({
  selector: 'app-products-form',
  templateUrl: './products-form.component.html',
  styleUrls: ['./products-form.component.scss'],
  standalone: false
})
export class ProductsFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  productCode?: string;
  productId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private alerts: AlertService
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required]],
      type: ['', [Validators.required]],
      details: [''],
      weight: [0, [Validators.required, Validators.min(0)]],
      purchasePrice: [0, [Validators.required, Validators.min(0)]],
      salePrice: [0, [Validators.required, Validators.min(0)]],
      height: [0, [Validators.required, Validators.min(0)]],
      width: [0, [Validators.required, Validators.min(0)]],
      depth: [0, [Validators.required, Validators.min(0)]],
      destinationVehicle: ['']
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const code = params.get('code');
      if (code && code !== 'new') {
        this.isEdit = true;
        this.productCode = code;
        this.loadProduct(this.productCode);
      }
    });
  }

  loadProduct(code: string): void {
    this.productService.getByCode(code).subscribe({
      next: (product: Product) => {
        this.productId = product.id;
        this.form.patchValue(product);
      },
      error: (err) => {
        this.alerts.error(err.userMessage || 'Erro ao carregar produto');
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.alerts.warning('Preencha todos os campos obrigatórios');
      return;
    }

    const data: ProductRequest = this.form.value;
    const action = this.isEdit && this.productId
      ? this.productService.update(this.productId, data)
      : this.productService.create(data);

    action.subscribe({
      next: () => {
        this.alerts.success(`Produto ${this.isEdit ? 'atualizado' : 'criado'} com sucesso`);
        this.router.navigate(['/products']);
      },
      error: (err) => {
        this.alerts.error(err.userMessage || `Erro ao ${this.isEdit ? 'atualizar' : 'criar'} produto`);
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/products']);
  }
}
