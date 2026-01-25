export interface Product {
  id: number;
  code: string;
  name: string;
  type: string;
  details?: string;
  weight: number;
  purchasePrice: number;
  salePrice: number;
  height: number;
  width: number;
  depth: number;
  destinationVehicle?: string;
  stockQuantity: number;
  createdAt: string;
}

export interface ProductRequest {
  code?: string;
  name: string;
  type: string;
  details?: string;
  weight: number;
  purchasePrice: number;
  salePrice: number;
  height: number;
  width: number;
  depth: number;
  destinationVehicle?: string;
}

export interface ProductResponse {
  id: number;
  code: string;
  name: string;
  type: string;
  details?: string;
  weight: number;
  purchasePrice: number;
  salePrice: number;
  height: number;
  width: number;
  depth: number;
  destinationVehicle?: string;
  stockQuantity: number;
  createdAt: string;
}
