import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sale, SaleRequest, SaleResponse } from '../models/sale.model';
import { PageResponse } from '../models/page-response.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SaleService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/sales`;

  getAll(): Observable<Sale[]> {
    return this.http.get<Sale[]>(this.apiUrl);
  }

  search(filter: string = '', page: number = 0, size: number = 10): Observable<PageResponse<Sale>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filter) {
      params = params.set('filter', filter);
    }

    return this.http.get<PageResponse<Sale>>(`${this.apiUrl}/search`, { params });
  }

  getByCode(code: string): Observable<SaleResponse> {
    return this.http.get<SaleResponse>(`${this.apiUrl}/code/${code}`);
  }

  getById(id: number): Observable<SaleResponse> {
    return this.http.get<SaleResponse>(`${this.apiUrl}/${id}`);
  }

  create(data: SaleRequest): Observable<SaleResponse> {
    return this.http.post<SaleResponse>(this.apiUrl, data);
  }

  update(id: number, data: SaleRequest): Observable<SaleResponse> {
    return this.http.put<SaleResponse>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
