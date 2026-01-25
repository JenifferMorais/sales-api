import { Component, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { ReportService } from '../../../core/services/report.service';
import { AlertService } from '../../../shared/services/alert/alert.service';
import { SimpleColumn, RowAction } from '../../../shared/components/data-table-outline/data-table-outline.component';
import { MonthlyRevenueResponse } from '../../../core/models/report.model';

@Component({
  selector: 'app-reports-list',
  templateUrl: './reports-list.component.html',
  styleUrls: ['./reports-list.component.scss'],
  standalone: false
})
export class ReportsListComponent implements OnInit {
  allData: any[] = [];
  filteredData: any[] = [];
  searchQuery = '';

  columns: SimpleColumn[] = [
    { key: 'period', label: 'Período', sortable: true },
    { key: 'totalSales', label: 'Subtotal', sortable: true, width: '150px', format: 'currency' },
    { key: 'tax', label: 'Imposto (9%)', sortable: true, width: '150px', format: 'currency' },
    { key: 'revenue', label: 'Total', sortable: true, width: '150px', format: 'currency' },
    { key: 'actions', label: '', isActions: true, width: '56px', align: 'right' }
  ];

  rowActions: RowAction[] = [
    { text: 'Visualizar Detalhes', icon: 'visibility', tone: 'view', onClick: (row) => this.viewDetails(row) },
    { text: 'Exportar Período', icon: 'download', tone: 'edit', onClick: (row) => this.exportPeriod(row) }
  ];

  totalItems = 0;
  pageIndex = 0;
  pageSize = 10;

  constructor(
    private reportService: ReportService,
    private alerts: AlertService
  ) {}

  ngOnInit(): void {
    this.loadReports();
  }

  private loadReports(): void {
    const today = new Date();
    const referenceDate = today.toISOString().split('T')[0];

    this.reportService.getMonthlyRevenue({ referenceDate }).subscribe({
      next: (response: MonthlyRevenueResponse) => {
        this.allData = response.monthlyData
          .filter(item => item.subtotal > 0 || item.taxAmount > 0 || item.total > 0)
          .map(item => ({
            period: `${item.monthName} ${item.year}`,
            monthName: item.monthName,
            month: item.month,
            year: item.year,
            totalSales: item.subtotal,
            tax: item.taxAmount,
            revenue: item.total
          }));
        this.applyFilters();
      },
      error: (err) => {
        this.alerts.error(err.userMessage || 'Não foi possível carregar os relatórios');
      }
    });
  }

  private applyFilters(): void {
    let filtered = [...this.allData];

    if (this.searchQuery) {
      const search = this.searchQuery.toLowerCase();
      filtered = filtered.filter(item =>
        (item.period || '').toLowerCase().includes(search) ||
        (item.monthName || '').toLowerCase().includes(search) ||
        (item.year || '').toString().includes(search)
      );
    }

    this.totalItems = filtered.length;
    const start = this.pageIndex * this.pageSize;
    const end = start + this.pageSize;
    this.filteredData = filtered.slice(start, end);
  }

  onSearch(value: string): void {
    this.searchQuery = value || '';
    this.pageIndex = 0;
    this.applyFilters();
  }

  onPage(evt: PageEvent): void {
    this.pageIndex = evt.pageIndex;
    this.pageSize = evt.pageSize;
    this.applyFilters();
  }

  exportReport(): void {
    const today = new Date();
    const referenceDate = today.toISOString().split('T')[0];

    this.reportService.getMonthlyRevenue({ referenceDate }).subscribe({
      next: (response: MonthlyRevenueResponse) => {
        this.exportToCSV(response);
        this.alerts.success('Relatório exportado com sucesso');
      },
      error: (err) => {
        this.alerts.error(err.userMessage || 'Erro ao exportar relatório');
      }
    });
  }

  viewDetails(row: any): void {
    this.alerts.info(`Visualizando detalhes do período: ${row.period}`);
  }

  exportPeriod(row: any): void {
    const csvContent = this.createCSVForPeriod(row);
    this.downloadCSV(csvContent, `relatorio-${row.monthName}-${row.year}.csv`);
    this.alerts.success(`Relatório do período ${row.period} exportado com sucesso`);
  }

  private exportToCSV(response: MonthlyRevenueResponse): void {
    let csv = 'Período,Subtotal,Imposto (9%),Total\n';

    response.monthlyData
      .filter(item => item.subtotal > 0 || item.taxAmount > 0 || item.total > 0)
      .forEach(item => {
        csv += `"${item.monthName} ${item.year}",${item.subtotal.toFixed(2)},${item.taxAmount.toFixed(2)},${item.total.toFixed(2)}\n`;
      });

    csv += `\nTotal Geral,${response.totalRevenue.toFixed(2)},${response.totalTax.toFixed(2)},${response.grandTotal.toFixed(2)}`;

    this.downloadCSV(csv, 'relatorio-faturamento-mensal.csv');
  }

  private createCSVForPeriod(row: any): string {
    let csv = 'Período,Subtotal,Imposto (9%),Total\n';
    csv += `"${row.period}",${row.totalSales},${row.tax},${row.revenue}\n`;
    return csv;
  }

  private downloadCSV(csvContent: string, filename: string): void {
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);

    link.setAttribute('href', url);
    link.setAttribute('download', filename);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
