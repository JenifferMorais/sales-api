import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  MessageResponse,
  User
} from '../models/user.model';
import { environment } from '../../../environments/environment';
import { AlertService } from '../../shared/services/alert/alert.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private alerts = inject(AlertService);

  private readonly TOKEN_KEY = 'access_token';
  private readonly USER_KEY = 'current_user';

  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  private lastActivityTimestamp = Date.now();
  private inactivityCheckInterval: any;

  constructor() {
    this.startInactivityCheck();
    this.setupActivityListeners();
  }

  // Authentication methods
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/v1/auth/login`, credentials)
      .pipe(
        tap(response => {
          this.saveToken(response.access_token);
          if (response.user) {
            this.saveUser(response.user);
            this.currentUserSubject.next(response.user);
          }
          this.resetInactivityTimer();
        })
      );
  }

  register(data: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${environment.apiUrl}/v1/auth/register`, data);
  }

  logout(): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${environment.apiUrl}/v1/auth/logout`, {})
      .pipe(
        tap(() => {
          this.clearAuthData();
        })
      );
  }

  forgotPassword(data: ForgotPasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${environment.apiUrl}/v1/auth/forgot-password`, data);
  }

  resetPassword(data: ResetPasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${environment.apiUrl}/v1/auth/reset-password`, data);
  }

  // Token management
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    // Check if token is expired
    try {
      const payload = this.parseJwt(token);
      const expirationTime = payload.exp * 1000; // Convert to milliseconds
      return Date.now() < expirationTime;
    } catch {
      return false;
    }
  }

  // User management
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  private saveUser(user: User): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  private getUserFromStorage(): User | null {
    const userJson = localStorage.getItem(this.USER_KEY);
    return userJson ? JSON.parse(userJson) : null;
  }

  // Clear auth data
  private clearAuthData(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
    this.stopInactivityCheck();
    this.router.navigate(['/auth/login']);
  }

  // JWT parsing
  private parseJwt(token: string): any {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  }

  // Inactivity management
  private setupActivityListeners(): void {
    const events = ['mousedown', 'keydown', 'scroll', 'touchstart', 'click'];

    events.forEach(event => {
      document.addEventListener(event, () => this.resetInactivityTimer(), true);
    });
  }

  private resetInactivityTimer(): void {
    this.lastActivityTimestamp = Date.now();
  }

  private startInactivityCheck(): void {
    // Check every 30 seconds
    this.inactivityCheckInterval = setInterval(() => {
      if (!this.isAuthenticated()) {
        return;
      }

      const inactiveTime = Date.now() - this.lastActivityTimestamp;
      const maxInactiveTime = environment.inactivityTimeoutMinutes * 60 * 1000;

      if (inactiveTime >= maxInactiveTime) {
        this.handleInactivityTimeout();
      }
    }, 30000);
  }

  private stopInactivityCheck(): void {
    if (this.inactivityCheckInterval) {
      clearInterval(this.inactivityCheckInterval);
    }
  }

  private handleInactivityTimeout(): void {
    this.clearAuthData();
    this.alerts.warning('Sua sessão expirou por inatividade. Faça login novamente.');
  }
}
