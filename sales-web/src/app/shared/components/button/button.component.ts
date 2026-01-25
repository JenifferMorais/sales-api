import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button
      [type]="type"
      [disabled]="disabled"
      [class]="'btn ' + styleClass"
      (click)="handleClick($event)">
      <ng-content></ng-content>
    </button>
  `,
  styles: [`
    :host {
      display: inline-block;
    }

    .btn {
      padding: 0 18px;
      height: 40px;
      border: none;
      border-radius: 8px;
      font-family: Inter, system-ui, sans-serif;
      font-weight: 700;
      font-size: 14px;
      cursor: pointer;
      transition: all 0.15s ease;
      display: inline-flex;
      align-items: center;
      gap: 8px;
      text-transform: uppercase;
      letter-spacing: 0;
      justify-content: center;
      line-height: 100%;
    }

    .btn:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .btn-blue {
      background: #1e40af;
      color: #fff;
    }

    .btn-blue:hover:not(:disabled) {
      background: #1e3a8a;
    }

    .btn-outline {
      background: #3b82f6;
      color: #fff;
    }

    .btn-outline:hover:not(:disabled) {
      background: #2563eb;
    }

    .btn-red {
      background: #ef4444;
      color: white;
    }

    .btn-red:hover:not(:disabled) {
      background: #dc2626;
    }
  `]
})
export class ButtonComponent {
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() disabled = false;
  @Input() styleClass = '';
  @Output() clicked = new EventEmitter<MouseEvent>();

  handleClick(event: MouseEvent): void {
    if (!this.disabled) {
      this.clicked.emit(event);
    }
  }
}
