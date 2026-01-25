import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TableActionDef, TableActionEvent, TableColumn } from '../../models/table';

@Component({
  selector: 'app-data-table',
  standalone: false,
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent {
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() page = 1;
  @Input() pageSize = 10;
  @Input() total = 0;
  @Input() defaultTruncate = 49;
  @Input() rowActions: TableActionDef[] | ((row: any) => TableActionDef[]) = [];
  @Input() statusClassMap: Record<string, string> = {};
  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();
  @Output() action = new EventEmitter<TableActionEvent>();

  get totalPages(): number {
    return Math.max(1, Math.ceil((this.total || 0) / (this.pageSize || 10)));
  }

  pagesToShow(): number[] {
    const current = this.page || 1;
    const last = this.totalPages;
    const span = 2;
    const from = Math.max(1, current - span);
    const to = Math.min(last, current + span);
    return Array.from({ length: to - from + 1 }, (_, i) => from + i);
  }

  onPrev() { if (this.page > 1) this.pageChange.emit(this.page - 1); }
  onNext() { if (this.page < this.totalPages) this.pageChange.emit(this.page + 1); }
  goTo(p: number) { if (p >= 1 && p <= this.totalPages) this.pageChange.emit(p); }

  isTruncated(text: string | null | undefined, limit?: number): boolean {
    if (text == null) return false;
    const max = limit ?? this.defaultTruncate;
    return String(text).length > max;
  }

  actionsFor(row: any): TableActionDef[] {
    return typeof this.rowActions === 'function'
      ? (this.rowActions as (r: any) => TableActionDef[])(row)
      : (this.rowActions as TableActionDef[]);
  }

  handleActionClick(a: TableActionDef, row: any) {
    a.onClick?.(row);
    this.action.emit({ action: a.id, row });
  }

  statusClassFor(row: any, col: TableColumn): string {
    const raw = (row?.[col.key] ?? '').toString();
    const norm = raw
      .normalize('NFD').replace(/\p{Diacritic}/gu, '')
      .trim().toLowerCase();

    if (col.statusClassKey && row[col.statusClassKey]) return row[col.statusClassKey];
    if (col.statusMap && col.statusMap[norm]) return col.statusMap[norm];
    if (this.statusClassMap && this.statusClassMap[norm]) return this.statusClassMap[norm];
    if (norm.includes('inativo') || norm.includes('cancelado')) return 'cor-inativo';
    if (norm.includes('ativo') || norm.includes('alugado')) return 'cor-ativo';
    if (norm.includes('livre')) return 'cor-livre';

    return 'cor-neutro';
  }


  resolveTextAlign(c: TableColumn): 'left' | 'center' | 'right' {
    const a = (c.align ?? 'start').toString().toLowerCase();
    if (a === 'center') return 'center';
    if (a === 'end' || a === 'right') return 'right';
    return 'left';
  }


}
