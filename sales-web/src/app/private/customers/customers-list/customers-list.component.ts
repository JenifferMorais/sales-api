import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { CustomerService } from '../../../core/services/customer.service';
import { AlertService } from '../../../shared/services/alert/alert.service';
import { SimpleColumn, RowAction } from '../../../shared/components/data-table-outline/data-table-outline.component';

@Component({
  selector: 'app-customers-list',
  templateUrl: './customers-list.component.html',
  styleUrls: ['./customers-list.component.scss'],
  standalone: false
})
export class CustomersListComponent implements OnInit {
  filteredCustomers: any[] = [];
  searchQuery = '';

  columns: SimpleColumn[] = [
    { key: 'code', label: 'Código', sortable: true, width: '100px' },
    { key: 'fullName', label: 'Nome', sortable: true },
    { key: 'email', label: 'Email', sortable: true },
    { key: 'cellPhone', label: 'Telefone', sortable: true, width: '140px' },
    { key: 'actions', label: '', isActions: true, width: '56px', align: 'right' }
  ];

  rowActions: RowAction[] = [
    { text: 'Visualizar', icon: 'visibility', tone: 'view', onClick: (row) => this.viewCustomer(row) },
    { text: 'Editar', icon: 'edit', tone: 'edit', onClick: (row) => this.editCustomer(row) },
    { text: 'Remover', icon: 'delete', tone: 'remove', onClick: (row) => this.openDelete(row) }
  ];

  totalItems = 0;
  pageIndex = 0;
  pageSize = 10;

  deleteOpen = false;
  deleting = false;
  toDeleteId: number | null = null;

  constructor(
    private customerService: CustomerService,
    private router: Router,
    private alerts: AlertService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  private loadCustomers(): void {
    this.customerService.search(this.searchQuery, this.pageIndex, this.pageSize).subscribe({
      next: (response) => {
        this.filteredCustomers = response.content;
        this.totalItems = response.totalElements;
      },
      error: (err) => {
        this.alerts.error(err.userMessage || 'Não foi possível carregar a lista de clientes.');
      }
    });
  }

  onSearch(query: string): void {
    this.searchQuery = query || '';
    this.pageIndex = 0;
    this.loadCustomers();
  }

  onPage(evt: PageEvent): void {
    this.pageIndex = evt.pageIndex;
    this.pageSize = evt.pageSize;
    this.loadCustomers();
  }

  viewCustomer(customer: any): void {
    this.router.navigate(['/customers', customer.code]);
  }

  editCustomer(customer: any): void {
    this.router.navigate(['/customers', customer.code, 'edit']);
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
    this.customerService.delete(this.toDeleteId).subscribe({
      next: () => {
        this.deleting = false;
        this.closeDelete();
        this.loadCustomers();
        this.alerts.success('Cliente removido com sucesso.');
      },
      error: (err) => {
        this.deleting = false;
        this.alerts.error(err.userMessage || 'Não foi possível remover o cliente.');
      }
    });
  }

  createCustomer(): void {
    this.router.navigate(['/customers/new']);
  }
}
