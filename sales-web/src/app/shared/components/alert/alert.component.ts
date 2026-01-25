import { Component, OnDestroy, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { AlertMessage, AlertService } from '../../services/alert/alert.service';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.scss'],
  standalone: true,
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlertComponent implements OnInit, OnDestroy {
  items: AlertMessage[] = [];
  private sub?: Subscription;

  constructor(private alerts: AlertService) { }

  ngOnInit(): void { this.sub = this.alerts.items$.subscribe(v => (this.items = v)); }
  ngOnDestroy(): void { this.sub?.unsubscribe(); }
  close(id: string) { this.alerts.remove(id); }
  trackById(_: number, it: AlertMessage) { return it.id; }
}
