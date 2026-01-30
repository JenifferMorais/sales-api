import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { AlertService } from '../../../shared/services/alert/alert.service';
import { Product, ProductRequest } from '../../../core/models/product.model';
import { SelectOption } from '../../../shared/components/custom-select/custom-select.component';

@Component({
  selector: 'app-products-form',
  templateUrl: './products-form.component.html',
  styleUrls: ['./products-form.component.scss'],
  standalone: false,
  encapsulation: ViewEncapsulation.None
})
export class ProductsFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  productCode?: string;
  productId?: number;

  categoryOptions: SelectOption[] = [
    { value: 'LIPS', label: 'Lábios' },
    { value: 'FACE', label: 'Rosto' },
    { value: 'EYES', label: 'Olhos' },
    { value: 'NAILS', label: 'Unhas' },
    { value: 'SKIN_CARE', label: 'Cuidados com a Pele' },
    { value: 'HAIR', label: 'Cabelos' },
    { value: 'FRAGRANCE', label: 'Fragrância' },
    { value: 'OTHER', label: 'Outros' }
  ];

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
      weight: [null, [Validators.required, Validators.min(0)]],
      purchasePrice: [null, [Validators.required, Validators.min(0)]],
      salePrice: [null, [Validators.required, Validators.min(0)]],
      height: [null, [Validators.required, Validators.min(0)]],
      width: [null, [Validators.required, Validators.min(0)]],
      depth: [null, [Validators.required, Validators.min(0)]],
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
