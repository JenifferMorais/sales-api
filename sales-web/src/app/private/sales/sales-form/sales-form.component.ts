import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SaleService } from '../../../core/services/sale.service';
import { ProductService } from '../../../core/services/product.service';
import { CustomerService } from '../../../core/services/customer.service';
import { AlertService } from '../../../shared/services/alert/alert.service';
import { SaleRequest, SaleItemRequest } from '../../../core/models/sale.model';

@Component({
  selector: 'app-sales-form',
  templateUrl: './sales-form.component.html',
  styleUrls: ['./sales-form.component.scss'],
  standalone: false
})
export class SalesFormComponent implements OnInit {
  loading = false;
  isEdit = false;
  saleCode?: string;
  saleId?: number;

  form: FormGroup;
  customers: any[] = [];
  products: any[] = [];

  paymentMethods = [
    { label: 'Dinheiro', value: 'CASH' },
    { label: 'Cartão de Crédito', value: 'CREDIT_CARD' },
    { label: 'Cartão de Débito', value: 'DEBIT_CARD' },
    { label: 'PIX', value: 'PIX' }
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private saleService: SaleService,
    private productService: ProductService,
    private customerService: CustomerService,
    private alerts: AlertService
  ) {
    this.form = this.fb.group({
      customerCode: ['', Validators.required],
      paymentMethod: ['CASH', Validators.required],
      cardNumber: [''],
      items: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.loadCustomers();
    this.loadProducts();

    this.route.paramMap.subscribe(params => {
      const code = params.get('code');
      if (code && code !== 'new') {
        this.isEdit = true;
        this.saleCode = code;
        this.loadSale(code);
      } else {
        this.addItem();
      }
    });

    this.form.get('paymentMethod')?.valueChanges.subscribe(value => {
      const cardNumberControl = this.form.get('cardNumber');
      if (value === 'CREDIT_CARD' || value === 'DEBIT_CARD') {
        cardNumberControl?.setValidators([Validators.required]);
      } else {
        cardNumberControl?.clearValidators();
      }
      cardNumberControl?.updateValueAndValidity();
    });
  }

  get items(): FormArray {
    return this.form.get('items') as FormArray;
  }

  loadCustomers(): void {
    this.customerService.search('', 0, 1000).subscribe({
      next: (response) => {
        this.customers = response.content;
        console.log('Clientes carregados:', this.customers.length);
      },
      error: (err) => {
        console.error('Erro ao carregar clientes:', err);
        this.alerts.error('Erro ao carregar clientes');
      }
    });
  }

  loadProducts(): void {
    this.productService.search('', 0, 1000).subscribe({
      next: (response) => {
        this.products = response.content;
        console.log('Produtos carregados:', this.products.length);
      },
      error: (err) => {
        console.error('Erro ao carregar produtos:', err);
        this.alerts.error('Erro ao carregar produtos');
      }
    });
  }

  loadSale(code: string): void {
    this.loading = true;
    this.saleService.getByCode(code).subscribe({
      next: (sale) => {
        this.saleId = sale.id;
        this.form.patchValue({
          customerCode: sale.customerCode,
          paymentMethod: sale.paymentMethod,
          cardNumber: sale.cardNumber
        });

        sale.items.forEach(item => {
          this.items.push(this.fb.group({
            productCode: [item.productCode, Validators.required],
            quantity: [item.quantity, [Validators.required, Validators.min(1)]],
            unitPrice: [item.unitPrice, [Validators.required, Validators.min(0)]]
          }));
        });

        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.alerts.error('Erro ao carregar venda');
      }
    });
  }

  addItem(): void {
    this.items.push(this.fb.group({
      productCode: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      unitPrice: [0, [Validators.required, Validators.min(0)]]
    }));
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
  }

  onProductChange(index: number): void {
    const item = this.items.at(index);
    const productCode = item.get('productCode')?.value;
    const product = this.products.find(p => p.code === productCode);

    if (product) {
      item.patchValue({
        unitPrice: product.salePrice
      });
    }
  }

  getItemSubtotal(index: number): number {
    const item = this.items.at(index);
    const quantity = item.get('quantity')?.value || 0;
    const unitPrice = item.get('unitPrice')?.value || 0;
    return quantity * unitPrice;
  }

  getSubtotal(): number {
    return this.items.controls.reduce((sum, item) => {
      const quantity = item.get('quantity')?.value || 0;
      const unitPrice = item.get('unitPrice')?.value || 0;
      return sum + (quantity * unitPrice);
    }, 0);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.alerts.warning('Preencha todos os campos obrigatórios');
      return;
    }

    if (this.items.length === 0) {
      this.alerts.warning('Adicione pelo menos um item à venda');
      return;
    }

    this.loading = true;

    const customer = this.customers.find(c => c.code === this.form.value.customerCode);
    const seller = { code: 'SELLER001', name: 'Vendedor Padrão' }; // TODO: Get from auth

    const items: SaleItemRequest[] = this.items.controls.map(item => {
      const productCode = item.get('productCode')?.value;
      const product = this.products.find(p => p.code === productCode);

      return {
        productCode: productCode,
        productName: product?.name || '',
        quantity: item.get('quantity')?.value,
        unitPrice: item.get('unitPrice')?.value
      };
    });

    const data: SaleRequest = {
      customerCode: this.form.value.customerCode,
      customerName: customer?.fullName || '',
      sellerCode: seller.code,
      sellerName: seller.name,
      paymentMethod: this.form.value.paymentMethod,
      cardNumber: this.form.value.cardNumber,
      items: items
    };

    const action = this.isEdit && this.saleId
      ? this.saleService.update(this.saleId, data)
      : this.saleService.create(data);

    action.subscribe({
      next: () => {
        this.loading = false;
        this.alerts.success(`Venda ${this.isEdit ? 'atualizada' : 'criada'} com sucesso`);
        this.router.navigate(['/sales']);
      },
      error: (err) => {
        this.loading = false;
        this.alerts.error(err.userMessage || `Erro ao ${this.isEdit ? 'atualizar' : 'criar'} venda`);
      }
    });
  }

  cancel(): void {
    if (this.isEdit && this.saleCode) {
      this.router.navigate(['/sales', this.saleCode]);
    } else {
      this.router.navigate(['/sales']);
    }
  }
}
