import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from '../../../core/services/customer.service';
import { Customer } from '../../../core/models/customer.model';

@Component({
  selector: 'app-customers-detail',
  templateUrl: './customers-detail.component.html',
  styleUrls: ['./customers-detail.component.scss'],
  standalone: false
})
export class CustomersDetailComponent implements OnInit {
  customer: Customer | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerService: CustomerService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const code = params.get('code');
      if (code) {
        this.loadCustomer(code);
      }
    });
  }

  loadCustomer(code: string): void {
    this.customerService.getByCode(code).subscribe({
      next: (data: Customer) => {
        this.customer = data;
      },
      error: (err) => {
        this.router.navigate(['/customers']);
      }
    });
  }

  onEdit(): void {
    if (this.customer) {
      this.router.navigate(['/customers', this.customer.code, 'edit']);
    }
  }

  onBack(): void {
    this.router.navigate(['/customers']);
  }
}
