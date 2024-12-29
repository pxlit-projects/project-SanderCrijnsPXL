import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-review-comment',
  standalone: true,
  imports: [],
  templateUrl: './review-comment.component.html',
  styleUrl: './review-comment.component.css'
})
export class ReviewCommentComponent {
  @Input() comment!: String;
}
