// Monthly Revenue Report
export interface MonthlyRevenueRequest {
  referenceDate: string; // YYYY-MM-DD
}

export interface MonthlyRevenueResponse {
  monthlyData: MonthlyRevenueData[];
  totalRevenue: number;
  totalTax: number;
  grandTotal: number;
}

export interface MonthlyRevenueData {
  month: number;
  year: number;
  monthName: string;
  subtotal: number;
  taxAmount: number;
  total: number;
}

// Top Revenue Products Report
export interface TopRevenueProductsResponse {
  products: TopRevenueProductData[];
}

export interface TopRevenueProductData {
  productCode: string;
  productName: string;
  salePrice: number;
  totalRevenue: number;
}

// Oldest Products Report
export interface OldestProductsResponse {
  products: OldestProductData[];
}

export interface OldestProductData {
  name: string;
  weight: number;
  registrationDate: string;
  purchasePrice: number;
}

// New Customers Report
export interface NewCustomersRequest {
  year: number;
}

export interface NewCustomersResponse {
  year: number;
  totalCustomers: number;
  customersByMonth: NewCustomersMonthData[];
}

export interface NewCustomersMonthData {
  month: number;
  monthName: string;
  count: number;
}
