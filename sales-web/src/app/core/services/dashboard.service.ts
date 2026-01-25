import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  DashboardStatsResponse,
  DashboardChartResponse,
  RecentSalesResponse
} from '../models/dashboard.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/dashboard`;

  getStats(): Observable<DashboardStatsResponse> {
    return this.http.get<DashboardStatsResponse>(`${this.apiUrl}/stats`);
  }

  getChartData(range: string = 'month'): Observable<DashboardChartResponse> {
    return this.http.get<DashboardChartResponse>(`${this.apiUrl}/chart-data?range=${range}`);
  }

  getRecentSales(limit: number = 5): Observable<RecentSalesResponse> {
    return this.http.get<RecentSalesResponse>(`${this.apiUrl}/recent-sales?limit=${limit}`);
  }
}
