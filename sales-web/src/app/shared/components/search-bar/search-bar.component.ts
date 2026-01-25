import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-search-bar',
  standalone: false,
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss'],
})
export class SearchBarComponent {
  @Input() placeholder = 'Buscar';
  @Input() value = '';
  @Input() showFilter = true;

  @Output() valueChange  = new EventEmitter<string>();
  @Output() submitQuery  = new EventEmitter<string>();
  @Output() openFilters  = new EventEmitter<void>();

  onInput(v: string)     { this.value = v; this.valueChange.emit(v); }
  onSubmit()             { this.submitQuery.emit(this.value.trim()); }
  onKeyupEnter()         { this.onSubmit(); }
  onOpenFiltersClicked() { this.openFilters.emit(); }
}
