import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  MonthlyRevenueRequest,
  MonthlyRevenueResponse,
  TopRevenueProductsResponse,
  OldestProductsResponse,
  NewCustomersRequest,
  NewCustomersResponse
} from '../models/report.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/reports`;

  getMonthlyRevenue(request: MonthlyRevenueRequest): Observable<MonthlyRevenueResponse> {
    return this.http.post<MonthlyRevenueResponse>(`${this.apiUrl}/monthly-revenue`, request);
  }

  getTopRevenueProducts(): Observable<TopRevenueProductsResponse> {
    return this.http.get<TopRevenueProductsResponse>(`${this.apiUrl}/top-revenue-products`);
  }

  getOldestProducts(): Observable<OldestProductsResponse> {
    return this.http.get<OldestProductsResponse>(`${this.apiUrl}/oldest-products`);
  }

  getNewCustomers(request: NewCustomersRequest): Observable<NewCustomersResponse> {
    return this.http.post<NewCustomersResponse>(`${this.apiUrl}/new-customers`, request);
  }
}
