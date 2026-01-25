import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, ProductRequest, ProductResponse } from '../models/product.model';
import { PageResponse } from '../models/page-response.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/products`;

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  search(filter: string = '', page: number = 0, size: number = 10): Observable<PageResponse<Product>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filter) {
      params = params.set('filter', filter);
    }

    return this.http.get<PageResponse<Product>>(`${this.apiUrl}/search`, { params });
  }

  getByCode(code: string): Observable<ProductResponse> {
    return this.http.get<ProductResponse>(`${this.apiUrl}/code/${code}`);
  }

  getById(id: number): Observable<ProductResponse> {
    return this.http.get<ProductResponse>(`${this.apiUrl}/${id}`);
  }

  create(data: ProductRequest): Observable<ProductResponse> {
    return this.http.post<ProductResponse>(this.apiUrl, data);
  }

  update(id: number, data: ProductRequest): Observable<ProductResponse> {
    return this.http.put<ProductResponse>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
