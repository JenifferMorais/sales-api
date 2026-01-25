import {
  Component, Input, Output, EventEmitter, OnChanges, SimpleChanges, ViewChild, AfterViewInit
} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { CurrencyBrlPipe } from '../../pipes/currency-brl.pipe';
import { DateBrPipe } from '../../pipes/date-br.pipe';
import { PaymentMethodPipe } from '../../pipes/payment-method.pipe';
import { ProductTypePipe } from '../../pipes/product-type.pipe';

export type RowAction = {
  text?: string;
  textFactory?: (row: any) => string;
  icon?: string;
  onClick?: (row: any) => void;
  tone?: 'view' | 'edit' | 'remove';
  showAction?: (row: any) => boolean;
};

export type SimpleColumn = {
  key: string;
  label: string;
  sortable?: boolean;
  isActions?: boolean;
  width?: string;
  align?: 'left' | 'center' | 'right';
  format?: 'currency' | 'date' | 'paymentMethod' | 'productType';
};

export type PageEvent = { pageIndex: number; pageSize: number; length: number };

@Component({
  selector: 'app-data-table-outline',
  templateUrl: './data-table-outline.component.html',
  styleUrls: ['./data-table-outline.component.scss'],
  standalone: false
})
export class DataTableOutlineComponent implements OnChanges, AfterViewInit {
  @Input() columns: SimpleColumn[] = [];
  @Input() data: any[] = [];

  @Input() totalItems = 0;
  @Input() pageSize = 10;
  @Input() pageIndex = 0;

  @Input() rowActions: RowAction[] = [];

  @Output() rowClick = new EventEmitter<any>();
  @Output() pageChange = new EventEmitter<PageEvent>();

  @ViewChild(MatSort) sort!: MatSort;

  tableDataSource = new MatTableDataSource<any>([]);
  displayed: string[] = [];
  currentRow: any;

  private currencyPipe = new CurrencyBrlPipe();
  private datePipe = new DateBrPipe();
  private paymentMethodPipe = new PaymentMethodPipe();
  private productTypePipe = new ProductTypePipe();

  get length(): number {
    return this.totalItems || this.tableDataSource.data.length || 0;
  }
  get totalPages(): number {
    const t = Math.ceil((this.length || 0) / (this.pageSize || 1));
    return Math.max(1, t);
  }
  get items(): (number | string)[] {
    const total = this.totalPages;
    const max = 4;  
    if (total <= max) return Array.from({ length: total }, (_, i) => i + 1);

    const current = this.pageIndex + 1;
    let start = current - Math.floor((max - 1) / 2);
    let end = current + Math.floor((max - 1) / 2);
    if (start < 1) { end += (1 - start); start = 1; }
    if (end > total) { start -= (end - total); end = total; }
    start = Math.max(1, start);

    const mid: number[] = [];
    for (let p = start; p <= end; p++) mid.push(p);

    const res: (number | string)[] = [];
    if (start > 1) { res.push(1); if (start > 2) res.push('…'); }
    res.push(...mid);
    if (end < total) { if (end < total - 1) res.push('…'); res.push(total); }
    return res;
  }

  ngOnChanges(ch: SimpleChanges): void {
    if (ch['data']) {
      this.tableDataSource = new MatTableDataSource(this.data ?? []);
      if (this.sort) this.tableDataSource.sort = this.sort;
    }
    if (ch['columns']) {
      this.displayed = (this.columns ?? []).map(c => c.key);
    }
  }

  ngAfterViewInit(): void {
    this.tableDataSource.sort = this.sort;
  }

  setCurrentRow(row: any) { this.currentRow = row; }

  prev() {
    if (this.pageIndex > 0) this.onPageEvent(this.pageIndex - 1);
  }
  next() {
    if (this.pageIndex < this.totalPages - 1) this.onPageEvent(this.pageIndex + 1);
  }
  isOverflow(el: HTMLElement | null): boolean {
    if (!el) return false;
    return el.scrollWidth > el.clientWidth || el.scrollHeight > el.clientHeight;
  }

  formatCell(element: any, col: SimpleColumn): string {
    const value = element ? element[col.key] : null;

    if (value === null || value === undefined || value === '') {
      return col.format === 'currency' ? 'R$ 0,00' : '-';
    }

    switch (col.format) {
      case 'currency':
        return this.currencyPipe.transform(value);
      case 'date':
        return this.datePipe.transform(value);
      case 'paymentMethod':
        return this.paymentMethodPipe.transform(value);
      case 'productType':
        return this.productTypePipe.transform(value);
      default:
        return String(value);
    }
  }

  selectPage(p: number) {
    this.onPageEvent(p - 1);
  }
  private onPageEvent(pageIndex: number) {
    this.pageIndex = pageIndex;
    this.pageChange.emit({ pageIndex, pageSize: this.pageSize, length: this.length });
  }
}
