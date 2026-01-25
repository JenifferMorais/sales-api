import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-delete-confirm',
  templateUrl: './modal-delete.component.html',
  styleUrls: ['./modal-delete.component.scss'],
  encapsulation: ViewEncapsulation.None,
  standalone: false
})
export class ModalDeleteComponent {
  @Input() open = false;
  @Input() loading = false;

  @Input() width = 420;
  @Input() height = 260;
  @Input() icon = 'mdi:alert-outline';

  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();

  onClose()  { this.close.emit(); }
  onConfirm(){ if (!this.loading) this.confirm.emit(); }
}
