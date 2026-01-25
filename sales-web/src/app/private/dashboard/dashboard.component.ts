import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { ReportService } from '../../core/services/report.service';
import { AlertService } from '../../shared/services/alert/alert.service';
import { AuthService } from '../../core/services/auth.service';
import { TopRevenueProductData, OldestProductData } from '../../core/models/report.model';
import { forkJoin } from 'rxjs';

interface ChartPoint {
  month: string;
  shortMonth: string;
  year: string;
  sales: number;
  revenue: number;
}

interface SaleRecord {
  id: number;
  customer: string;
  product: string;
  amount: number;
  date: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  isLoading = false;

  stats = {
    totalSales: 0,
    totalRevenue: 0,
    totalCustomers: 0,
    totalProducts: 0,
    salesVariation: 0,
    revenueVariation: 0,
    customersVariation: 0
  };

  recentSales: SaleRecord[] = [];
  topRevenueProducts: TopRevenueProductData[] = [];
  oldestProducts: OldestProductData[] = [];
  chartData: ChartPoint[] = [];
  private chartMax = 0;

  hoveredValue: number | null = null;
  hoveredIndex = -1;
  hoveredType: 'sales' | 'revenue' | null = null;

  dateRanges = [
    { key: 'week', label: 'Últimos 7 dias' },
    { key: 'month', label: 'Este mês' },
    { key: 'quarter', label: 'Último trimestre' },
    { key: 'year', label: 'Ano' }
  ];

  selectedRange = this.dateRanges[1].key;
  isRangeMenuOpen = false;
  dateRangeLabel = this.dateRanges[1].label;

  constructor(
    private dashboardService: DashboardService,
    private reportService: ReportService,
    private alerts: AlertService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Verificar se está autenticado antes de carregar
    if (!this.authService.isAuthenticated()) {
      return;
    }

    // Pequeno delay para garantir que o token esteja pronto
    setTimeout(() => {
      this.loadDashboardData(this.selectedRange);
    }, 100);
  }

  toggleRangeMenu(): void {
    this.isRangeMenuOpen = !this.isRangeMenuOpen;
  }

  onRangeChange(range: string): void {
    this.selectedRange = range;
    this.isRangeMenuOpen = false;
    const selected = this.dateRanges.find(r => r.key === range);
    if (selected) {
      this.dateRangeLabel = selected.label;
    }
    this.loadDashboardData(range);
  }

  private loadDashboardData(range: string): void {
    this.isLoading = true;

    forkJoin({
      stats: this.dashboardService.getStats(),
      chartData: this.dashboardService.getChartData(range),
      recentSales: this.dashboardService.getRecentSales(5),
      topRevenue: this.reportService.getTopRevenueProducts(),
      oldestProducts: this.reportService.getOldestProducts()
    }).subscribe({
      next: (response) => {
        // Update stats
        if (response.stats) {
          this.stats = {
            totalSales: response.stats.totalSales || 0,
            totalRevenue: response.stats.totalRevenue || 0,
            totalCustomers: response.stats.totalCustomers || 0,
            totalProducts: response.stats.totalProducts || 0,
            salesVariation: response.stats.salesVariation || 0,
            revenueVariation: response.stats.revenueVariation || 0,
            customersVariation: response.stats.customersVariation || 0
          };
        }

        // Update chart data
        if (response.chartData && response.chartData.chartData && Array.isArray(response.chartData.chartData)) {
          this.chartData = response.chartData.chartData.map(point => ({
            month: point.label || '',
            shortMonth: point.shortLabel || '',
            year: point.date ? point.date.toString().substring(0, 4) : '',
            sales: point.salesCount || 0,
            revenue: point.revenue || 0
          }));
          this.updateChartMax();
        }

        // Update recent sales
        if (response.recentSales && response.recentSales.sales && Array.isArray(response.recentSales.sales)) {
          this.recentSales = response.recentSales.sales.map(sale => ({
            id: sale.id || 0,
            customer: sale.customerName || '',
            product: sale.productName || '',
            amount: sale.totalAmount || 0,
            date: sale.saleDate ? this.formatDate(sale.saleDate) : ''
          }));
        }

        // Update top revenue products
        if (response.topRevenue && response.topRevenue.products && Array.isArray(response.topRevenue.products)) {
          this.topRevenueProducts = response.topRevenue.products;
        }

        // Update oldest products
        if (response.oldestProducts && response.oldestProducts.products && Array.isArray(response.oldestProducts.products)) {
          this.oldestProducts = response.oldestProducts.products;
        }

        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;

        // Não mostrar erro se for 401 (não autorizado) - o interceptor já trata
        if (err.status === 401) {
          return;
        }

        // Silenciar erros durante o primeiro carregamento se for erro de conexão
        if (err.status === 0) {
          console.error('Erro ao carregar dashboard:', err);
          return;
        }

        this.alerts.error(err.userMessage || 'Não foi possível carregar os dados do dashboard');
      }
    });
  }

  private formatDate(dateString: string): string {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  }

  formatDateShort(dateString: string): string {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear().toString().substring(2);
    return `${day}/${month}/${year}`;
  }

  formatVariation(value: number): string {
    const sign = value >= 0 ? '+' : '';
    return `${sign}${value.toFixed(1)}% vs mês anterior`;
  }

  barHeight(value: number): string {
    if (!this.chartMax) return '0';
    return `${(value / this.chartMax) * 100}%`;
  }

  onBarEnter(value: number, index: number, type: 'sales' | 'revenue'): void {
    this.hoveredValue = value;
    this.hoveredIndex = index;
    this.hoveredType = type;
  }

  onBarLeave(): void {
    this.hoveredValue = null;
    this.hoveredIndex = -1;
    this.hoveredType = null;
  }

  private updateChartMax(): void {
    if (!this.chartData || this.chartData.length === 0) {
      this.chartMax = 0;
      return;
    }

    const salesMax = Math.max(...this.chartData.map(d => d.sales || 0));
    const revenueMax = Math.max(...this.chartData.map(d => (d.revenue || 0) / 100));
    this.chartMax = Math.max(salesMax, revenueMax);
  }
}
