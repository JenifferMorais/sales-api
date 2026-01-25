export interface Sale {
  id: number;
  code: string;
  customerCode: string;
  customerName: string;
  sellerCode: string;
  sellerName: string;
  paymentMethod: string;
  cardNumber?: string;
  amountPaid: number;
  subtotal: number;
  taxAmount: number;
  totalAmount: number;
  change: number;
  items: SaleItem[];
  createdAt: string;
}

export interface SaleItem {
  id: number;
  productCode: string;
  productName: string;
  quantity: number;
  unitPrice: number;
}

export interface SaleRequest {
  code?: string;
  customerCode: string;
  customerName: string;
  sellerCode: string;
  sellerName: string;
  paymentMethod: string;
  cardNumber?: string;
  items: SaleItemRequest[];
}

export interface SaleItemRequest {
  productCode: string;
  productName: string;
  quantity: number;
  unitPrice: number;
}

export interface SaleResponse {
  id: number;
  code: string;
  customerCode: string;
  customerName: string;
  sellerCode: string;
  sellerName: string;
  paymentMethod: string;
  cardNumber?: string;
  amountPaid: number;
  subtotal: number;
  taxAmount: number;
  totalAmount: number;
  change: number;
  items: SaleItemResponse[];
  createdAt: string;
}

export interface SaleItemResponse {
  id: number;
  productCode: string;
  productName: string;
  quantity: number;
  unitPrice: number;
}
