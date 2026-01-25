import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { ProductService } from '../../../core/services/product.service';
import { AlertService } from '../../../shared/services/alert/alert.service';
import { SimpleColumn, RowAction } from '../../../shared/components/data-table-outline/data-table-outline.component';

type CategoryFilter = 'todos' | 'lips' | 'face' | 'eyes' | 'nails' | 'skin_care' | 'hair' | 'fragrance' | 'other';

@Component({
  selector: 'app-products-list',
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.scss'],
  standalone: false
})
export class ProductsListComponent implements OnInit {
  private productService = inject(ProductService);
  private router = inject(Router);
  private alerts = inject(AlertService);

  searchText = '';
  category: CategoryFilter = 'todos';

  categoryOptions = [
    { label: 'Todos', value: 'todos' as CategoryFilter },
    { label: 'Lábios', value: 'lips' as CategoryFilter },
    { label: 'Rosto', value: 'face' as CategoryFilter },
    { label: 'Olhos', value: 'eyes' as CategoryFilter },
    { label: 'Unhas', value: 'nails' as CategoryFilter },
    { label: 'Cuidados com a Pele', value: 'skin_care' as CategoryFilter },
    { label: 'Cabelos', value: 'hair' as CategoryFilter },
    { label: 'Fragrância', value: 'fragrance' as CategoryFilter },
    { label: 'Outros', value: 'other' as CategoryFilter }
  ];

  columns: SimpleColumn[] = [
    { key: 'code', label: 'Código', sortable: true, width: '100px' },
    { key: 'name', label: 'Nome', sortable: true },
    { key: 'type', label: 'Categoria', sortable: true, width: '150px', format: 'productType' },
    { key: 'salePrice', label: 'Preço', sortable: true, width: '120px', format: 'currency' },
    { key: 'stockQuantity', label: 'Estoque', sortable: true, width: '100px' },
    { key: 'actions', label: '', isActions: true, width: '56px', align: 'right' }
  ];

  rowActions: RowAction[] = [
    { text: 'Visualizar', icon: 'visibility', tone: 'view', onClick: (row) => this.view(row) },
    { text: 'Editar', icon: 'edit', tone: 'edit', onClick: (row) => this.edit(row) },
    { text: 'Remover', icon: 'delete', tone: 'remove', onClick: (row) => this.openDelete(row) }
  ];

  rows: any[] = [];
  totalItems = 0;
  pageIndex = 0;
  pageSize = 10;

  deleteOpen = false;
  deleting = false;
  toDeleteId: number | null = null;

  ngOnInit(): void {
    this.load();
  }

  private load(): void {
    let filter = this.searchText || '';

    this.productService.search(filter, this.pageIndex, this.pageSize).subscribe({
      next: (response) => {
        let filtered = response.content;

        if (this.category !== 'todos') {
          filtered = filtered.filter(p =>
            (p.type || '').toLowerCase() === this.category.toLowerCase()
          );
          this.totalItems = filtered.length;
          this.rows = filtered;
        } else {
          this.rows = filtered;
          this.totalItems = response.totalElements;
        }
      },
      error: (err) => {
        this.alerts.error(err.userMessage || 'Não foi possível carregar a lista de produtos.');
      }
    });
  }

  onSearch(value: string): void {
    this.searchText = value || '';
    this.pageIndex = 0;
    this.load();
  }

  onCategoryChange(value: CategoryFilter): void {
    this.category = value;
    this.pageIndex = 0;
    this.load();
  }

  onPage(evt: PageEvent): void {
    this.pageIndex = evt.pageIndex;
    this.pageSize = evt.pageSize;
    this.load();
  }

  createProduct(): void {
    this.router.navigate(['/products/new']);
  }

  onRow(row: any): void {
    this.router.navigate(['/products', row.code]);
  }

  view(row: any): void {
    this.router.navigate(['/products', row.code]);
  }

  edit(row: any): void {
    this.router.navigate(['/products', row.code, 'edit']);
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
    this.productService.delete(this.toDeleteId).subscribe({
      next: () => {
        this.deleting = false;
        this.closeDelete();
        this.load();
        this.alerts.success('Produto removido com sucesso.');
      },
      error: (err) => {
        this.deleting = false;
        this.alerts.error(err.userMessage || 'Não foi possível remover o produto.');
      }
    });
  }
}
