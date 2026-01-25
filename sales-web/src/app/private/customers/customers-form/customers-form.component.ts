import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from '../../../core/services/customer.service';
import { AlertService } from '../../../shared/services/alert/alert.service';
import { Customer, CustomerRequest } from '../../../core/models/customer.model';

@Component({
  selector: 'app-customers-form',
  templateUrl: './customers-form.component.html',
  styleUrls: ['./customers-form.component.scss'],
  standalone: false
})
export class CustomersFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  customerCode?: string;
  customerId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private customerService: CustomerService,
    private alerts: AlertService
  ) {
    this.form = this.fb.group({
      fullName: ['', [Validators.required]],
      motherName: ['', [Validators.required]],
      cpf: ['', [Validators.required]],
      rg: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      cellPhone: ['', [Validators.required]],
      birthDate: ['', [Validators.required]],
      address: this.fb.group({
        zipCode: ['', [Validators.required]],
        street: ['', [Validators.required]],
        number: ['', [Validators.required]],
        complement: [''],
        neighborhood: ['', [Validators.required]],
        city: ['', [Validators.required]],
        state: ['', [Validators.required]]
      })
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const code = params.get('code');
      if (code && code !== 'new') {
        this.isEdit = true;
        this.customerCode = code;
        this.loadCustomer(this.customerCode);
      }
    });
  }

  loadCustomer(code: string): void {
    this.customerService.getByCode(code).subscribe({
      next: (customer: Customer) => {
        this.customerId = customer.id;
        this.form.patchValue(customer);
      },
      error: (err) => {
        this.alerts.error(err.userMessage || 'Erro ao carregar cliente');
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.alerts.warning('Preencha todos os campos obrigatórios');
      return;
    }

    const data: CustomerRequest = this.form.value;

    if (this.isEdit && this.customerId) {
      this.customerService.update(this.customerId, data).subscribe({
        next: () => {
          this.alerts.success('Cliente atualizado com sucesso');
          this.router.navigate(['/customers']);
        },
        error: (err) => {
          this.alerts.error(err.userMessage || 'Erro ao atualizar cliente');
        }
      });
    } else {
      this.customerService.create(data).subscribe({
        next: () => {
          this.alerts.success('Cliente criado com sucesso');
          this.router.navigate(['/customers']);
        },
        error: (err) => {
          this.alerts.error(err.userMessage || 'Erro ao criar cliente');
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/customers']);
  }
}
