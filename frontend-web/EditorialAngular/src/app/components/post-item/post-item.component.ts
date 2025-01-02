import { Component, Inject, Input } from '@angular/core';
import { PostItem } from '../../models/post-item.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-post-item',
  standalone: true,
  imports: [],
  templateUrl: './post-item.component.html',
  styleUrl: './post-item.component.css',
  providers: [DatePipe]
})
export class PostItemComponent {
  @Input() post!: PostItem;

  constructor(private datePipe: DatePipe) {}

  get formattedDate(): string {
    return this.datePipe.transform(this.post.dateCreated, 'dd-MM-yyyy') || '';
  }
}
