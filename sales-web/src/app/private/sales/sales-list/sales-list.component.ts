import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { SaleService } from '../../../core/services/sale.service';
import { AlertService } from '../../../shared/services/alert/alert.service';
import { SimpleColumn, RowAction } from '../../../shared/components/data-table-outline/data-table-outline.component';

@Component({
  selector: 'app-sales-list',
  templateUrl: './sales-list.component.html',
  styleUrls: ['./sales-list.component.scss'],
  standalone: false
})
export class SalesListComponent implements OnInit {
  filteredSales: any[] = [];
  searchQuery = '';

  columns: SimpleColumn[] = [
    { key: 'code', label: 'Código', sortable: true, width: '100px' },
    { key: 'customerName', label: 'Cliente', sortable: true },
    { key: 'createdAt', label: 'Data', sortable: true, width: '120px', format: 'date' },
    { key: 'paymentMethod', label: 'Pagamento', sortable: true, width: '150px', format: 'paymentMethod' },
    { key: 'totalAmount', label: 'Total', sortable: true, width: '120px', format: 'currency' },
    { key: 'actions', label: '', isActions: true, width: '56px', align: 'right' }
  ];

  rowActions: RowAction[] = [
    { text: 'Visualizar', icon: 'visibility', tone: 'view', onClick: (row) => this.view(row) },
    { text: 'Editar', icon: 'edit', tone: 'edit', onClick: (row) => this.edit(row) },
    { text: 'Remover', icon: 'delete', tone: 'remove', onClick: (row) => this.openDelete(row) }
  ];

  totalItems = 0;
  pageIndex = 0;
  pageSize = 10;

  deleteOpen = false;
  deleting = false;
  toDeleteId: number | null = null;

  constructor(
    private saleService: SaleService,
    private router: Router,
    private alerts: AlertService
  ) {}

  ngOnInit(): void {
    this.loadSales();
  }

  private loadSales(): void {
    this.saleService.search(this.searchQuery, this.pageIndex, this.pageSize).subscribe({
      next: (response) => {
        this.filteredSales = response.content;
        this.totalItems = response.totalElements;
      },
      error: (err) => {
        this.alerts.error(err.userMessage || 'Não foi possível carregar a lista de vendas.');
      }
    });
  }

  onSearch(value: string): void {
    this.searchQuery = value || '';
    this.pageIndex = 0;
    this.loadSales();
  }

  onPage(evt: PageEvent): void {
    this.pageIndex = evt.pageIndex;
    this.pageSize = evt.pageSize;
    this.loadSales();
  }

  createSale(): void {
    this.router.navigate(['/sales/new']);
  }

  onRow(row: any): void {
    this.router.navigate(['/sales', row.code]);
  }

  view(row: any): void {
    this.router.navigate(['/sales', row.code]);
  }

  edit(row: any): void {
    this.router.navigate(['/sales', row.code, 'edit']);
  }

  openDelete(row: any): void {
    this.toDeleteId = row.id;
    this.deleteOpen = true;
  }

  closeDelete(): void {
    this.deleteOpen = false;
    this.deleting = false;
    this.toDeleteId = null;
  }

  confirmDelete(): void {
    if (!this.toDeleteId) return;
    this.deleting = true;
    this.saleService.delete(this.toDeleteId).subscribe({
      next: () => {
        this.deleting = false;
        this.closeDelete();
        this.loadSales();
        this.alerts.success('Venda removida com sucesso.');
      },
      error: (err) => {
        this.deleting = false;
        this.alerts.error(err.userMessage || 'Não foi possível remover a venda.');
      }
    });
  }
}
