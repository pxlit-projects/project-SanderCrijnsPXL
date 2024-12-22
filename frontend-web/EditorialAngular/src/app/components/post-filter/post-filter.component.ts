import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-filter',
  templateUrl: './post-filter.component.html',
  styleUrls: ['./post-filter.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class PostFilterComponent {
  filters = {
    content: '',
    author: '',
    date: ''
  };

  @Output() filterChanged = new EventEmitter<any>();

  applyFilters(): void {
    this.filterChanged.emit(this.filters);
  }

  clearFilters(): void {
    this.filters = { content: '', author: '', date: '' };
    this.applyFilters();
  }
}