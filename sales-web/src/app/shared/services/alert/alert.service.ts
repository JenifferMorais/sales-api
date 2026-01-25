import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type AlertLevel = 'success' | 'error' | 'warning' | 'info' | 'custom';

export interface AlertMessage {
    id: string;
    text: string;
    level: AlertLevel;
    icon?: string;
    color?: string;
    duration?: number;
}

function uid() {
    return Math.random().toString(36).slice(2) + Date.now().toString(36);
}

@Injectable({ providedIn: 'root' })
export class AlertService {
    private readonly _items$ = new BehaviorSubject<AlertMessage[]>([]);
    readonly items$ = this._items$.asObservable();

    private push(msg: AlertMessage) {
        this._items$.next([...this._items$.value, msg]);
        const ttl = msg.duration ?? 3500;
        if (ttl > 0) setTimeout(() => this.remove(msg.id), ttl);
        return msg.id;
    }

    remove(id: string) {
        this._items$.next(this._items$.value.filter(x => x.id !== id));
    }

    clear() { this._items$.next([]); }

    success(text: string, duration?: number) {
        return this.push({ id: uid(), text, level: 'success', icon: 'mdi:check-circle-outline', color: 'success', duration });
    }

    error(text: string, duration?: number) {
        return this.push({ id: uid(), text, level: 'error', icon: 'mdi:alert-circle-outline', color: 'danger', duration });
    }

    warning(text: string, duration?: number) {
        return this.push({ id: uid(), text, level: 'warning', icon: 'mdi:alert-outline', color: 'warning', duration });
    }

    info(text: string, duration?: number) {
        return this.push({ id: uid(), text, level: 'info', icon: 'mdi:information-outline', color: 'primary', duration });
    }

    notify(opts: { text: string; icon?: string; color?: string; duration?: number }) {
        return this.push({ id: uid(), text: opts.text, level: 'custom', icon: opts.icon, color: opts.color, duration: opts.duration });
    }
}
