import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly KEY = 'app-theme';
  private theme$ = new BehaviorSubject<Theme>('light');
  private defaultTheme: Theme = 'light';

  current$ = this.theme$.asObservable();

  /**
   * Initialize theme service with default theme from environment
   * Called by APP_INITIALIZER before app bootstrap
   */
  init(defaultTheme: Theme): void {
    this.defaultTheme = defaultTheme;
    const theme = this.loadTheme();
    this.theme$.next(theme);
    this.apply(theme);
  }

  toggle(): void {
    const next: Theme = this.theme$.value === 'light' ? 'dark' : 'light';
    this.set(next);
  }

  set(theme: Theme): void {
    this.theme$.next(theme);
    this.apply(theme);
    localStorage.setItem(this.KEY, theme);
  }

  private loadTheme(): Theme {
    const stored = localStorage.getItem(this.KEY);
    if (stored === 'light' || stored === 'dark') return stored;

    // Fall back to environment default (do not auto-follow system theme)
    return this.defaultTheme;
  }

  private apply(theme: Theme): void {
    document.documentElement.setAttribute('data-theme', theme);
  }
}
