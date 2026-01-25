import { Component, ElementRef, EventEmitter, HostListener, Input, Output } from '@angular/core';

type Option = { label: string; value: any };

@Component({
  selector: 'app-filter-bar',
  templateUrl: './filter-bar.component.html',
  styleUrls: ['./filter-bar.component.scss'],
  standalone: false
})
export class FilterBarComponent {
  @Input() placeholder = 'Buscar';
  @Input() dropdownOptions: Option[] = [];
  @Input() dropdownValue: any;
  @Input() textSearch = '';

  @Output() searchChange = new EventEmitter<string>();
  @Output() dropdownChange = new EventEmitter<any>();
  @Output() textSearchChange = new EventEmitter<string>();

  isOpen = false;

  constructor(private host: ElementRef<HTMLElement>) {}

  get currentLabel(): string {
    const found = this.dropdownOptions.find(o => o.value === this.dropdownValue);
    return found ? found.label : (this.dropdownOptions[0]?.label ?? '');
  }

  onInput(v: string) {
    this.textSearch = v;
    this.textSearchChange.emit(v);
    this.searchChange.emit(v);
  }

  toggle() {
    this.isOpen = !this.isOpen;
  }

  select(opt: Option) {
    this.dropdownValue = opt.value;
    this.dropdownChange.emit(opt.value);
    this.isOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onDocClick(e: MouseEvent) {
    if (!this.host.nativeElement.contains(e.target as Node)) this.isOpen = false;
  }
}
