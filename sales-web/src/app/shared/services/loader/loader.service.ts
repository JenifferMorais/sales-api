import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class LoaderService {
  private counter$ = new BehaviorSubject<number>(0);
  private gate$ = new BehaviorSubject<boolean>(false);

  // Visibilidade com atraso mínimo para evitar flicker
  readonly isLoading$: Observable<boolean> = this.gate$.pipe(distinctUntilChanged());

  start(): void {
    const next = this.counter$.value + 1;
    this.counter$.next(next);
    if (!this.gate$.value) {
      // aguarda 120ms antes de abrir para evitar piscadas
      setTimeout(() => {
        if (this.counter$.value > 0) this.gate$.next(true);
      }, 120);
    }
  }

  stop(): void {
    const next = Math.max(0, this.counter$.value - 1);
    this.counter$.next(next);
    if (next === 0) {
      // mantém visível por 200ms para transição suave
      setTimeout(() => {
        if (this.counter$.value === 0) this.gate$.next(false);
      }, 200);
    }
  }
}
