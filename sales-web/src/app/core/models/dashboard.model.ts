export interface DashboardStatsResponse {
  totalSales: number;
  totalRevenue: number;
  totalCustomers: number;
  totalProducts: number;
  salesVariation: number;
  revenueVariation: number;
  customersVariation: number;
}

export interface DashboardChartDataPoint {
  label: string;
  shortLabel: string;
  date: string;
  salesCount: number;
  revenue: number;
}

export interface DashboardChartResponse {
  chartData: DashboardChartDataPoint[];
}

export interface RecentSaleData {
  id: number;
  code: string;
  customerName: string;
  productName: string;
  totalAmount: number;
  saleDate: string;
}

export interface RecentSalesResponse {
  sales: RecentSaleData[];
}
