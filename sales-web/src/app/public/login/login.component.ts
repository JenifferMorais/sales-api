import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AlertService } from '../../shared/services/alert/alert.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: false
})
export class LoginComponent {
  isLoading = false;
  hide = true;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private alerts: AlertService
  ) {}

  form: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.alerts.warning('Preencha e-mail e senha válidos.');
      return;
    }

    this.isLoading = true;
    const credentials = this.form.value;

    this.auth.login(credentials).subscribe({
      next: () => {
        const returnUrl = this.route.snapshot.queryParamMap.get('r') || '/dashboard';
        this.router.navigateByUrl(returnUrl, { replaceUrl: true });
        this.alerts.success('Login realizado com sucesso!');
      },
      error: (err) => {
        this.alerts.error(err.userMessage || 'Credenciais inválidas.');
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}
