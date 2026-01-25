import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SaleService } from '../../../core/services/sale.service';
import { AlertService } from '../../../shared/services/alert/alert.service';
import { SaleResponse } from '../../../core/models/sale.model';

@Component({
  selector: 'app-sales-detail',
  templateUrl: './sales-detail.component.html',
  styleUrls: ['./sales-detail.component.scss'],
  standalone: false
})
export class SalesDetailComponent implements OnInit {
  sale?: SaleResponse;
  loading = true;

  paymentMethodLabels: { [key: string]: string } = {
    'CASH': 'Dinheiro',
    'CREDIT_CARD': 'Cartão de Crédito',
    'DEBIT_CARD': 'Cartão de Débito',
    'PIX': 'PIX'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private saleService: SaleService,
    private alerts: AlertService
  ) {}

  ngOnInit(): void {
    const code = this.route.snapshot.paramMap.get('code');
    if (code) {
      this.loadSale(code);
    }
  }

  loadSale(code: string): void {
    this.loading = true;
    this.saleService.getByCode(code).subscribe({
      next: (sale) => {
        this.sale = sale;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.alerts.error(err.userMessage || 'Erro ao carregar venda');
        this.router.navigate(['/sales']);
      }
    });
  }

  getPaymentMethodLabel(method: string): string {
    return this.paymentMethodLabels[method] || method;
  }

  editSale(): void {
    if (this.sale) {
      this.router.navigate(['/sales', this.sale.code, 'edit']);
    }
  }

  goBack(): void {
    this.router.navigate(['/sales']);
  }
}
