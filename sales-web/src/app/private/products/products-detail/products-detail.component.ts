import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-products-detail',
  templateUrl: './products-detail.component.html',
  styleUrls: ['./products-detail.component.scss'],
  standalone: false
})
export class ProductsDetailComponent implements OnInit {
  product: Product | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const code = params.get('code');
      if (code) this.loadProduct(code);
    });
  }

  loadProduct(code: string): void {
    this.productService.getByCode(code).subscribe({
      next: (data: Product) => this.product = data,
      error: (err) => {
        this.router.navigate(['/products']);
      }
    });
  }

  onEdit(): void {
    if (this.product) {
      this.router.navigate(['/products', this.product.code, 'edit']);
    }
  }

  onBack(): void {
    this.router.navigate(['/products']);
  }
}
