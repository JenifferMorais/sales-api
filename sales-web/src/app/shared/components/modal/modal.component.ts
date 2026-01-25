import {
  Component, EventEmitter, HostListener, Input, OnChanges, Output,
  SimpleChanges, ElementRef, ViewChild, ViewEncapsulation
} from '@angular/core';

@Component({
  selector: 'app-modal',
  standalone: false,
  encapsulation: ViewEncapsulation.None,
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss'],
})
export class ModalComponent implements OnChanges {
  @Input() open = false;
  @Input() title = '';
  @Input() closeOnBackdrop = true;
  @Input() closeOnEsc = true;

  @Input() width?: number | string;
  @Input() height?: number | string;
  @Input() top?: number | string;
  @Input() left?: number | string;
  @Input() padding: number | string = 16;
  @Input() radius: number | string = 8;
  @Input() gap: number | string = 32;
  @Input() background: string = '';
  @Input() borderColor: string = '';

  @Output() close = new EventEmitter<void>();

  @ViewChild('dialog', { static: false }) dialogRef!: ElementRef<HTMLDivElement>;
  private lastActive: HTMLElement | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['open']) {
      if (this.open) {
        this.lastActive = document.activeElement as HTMLElement;
        setTimeout(() => this.dialogRef?.nativeElement?.focus(), 0);
        document.body.style.overflow = 'hidden';
      } else {
        document.body.style.overflow = '';
        this.lastActive?.focus?.();
      }
    }
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEsc(e: KeyboardEvent) {
    if (!this.open || !this.closeOnEsc) return;
    e.preventDefault();
    this.close.emit();
  }

  onBackdropClick(e: MouseEvent) {
    if (!this.closeOnBackdrop) return;
    if ((e.target as HTMLElement).classList.contains('modal-backdrop')) this.close.emit();
  }

  toCss(v?: number | string | null) {
    if (v === null || v === undefined || v === '') return null;
    return typeof v === 'number' ? `${v}px` : v;
  }
}
