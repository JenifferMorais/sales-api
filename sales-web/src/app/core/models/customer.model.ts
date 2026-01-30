export interface Address {
  zipCode: string;
  street: string;
  number: string;
  complement?: string;
  neighborhood: string;
  city: string;
  state: string;
}

export interface Customer {
  id: number;
  code: string;
  fullName: string;
  motherName: string;
  cpf: string;
  rg: string;
  address: Address;
  birthDate: string;
  cellPhone: string;
  email: string;
  createdAt: string;
}

export interface CustomerRequest {
  fullName: string;
  motherName: string;
  cpf: string;
  rg: string;
  address: Address;
  birthDate: string;
  cellPhone: string;
  email: string;
}

export interface CustomerResponse {
  id: number;
  code: string;
  fullName: string;
  motherName: string;
  cpf: string;
  rg: string;
  address: Address;
  birthDate: string;
  cellPhone: string;
  email: string;
  createdAt: string;
}

export interface PageableResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
