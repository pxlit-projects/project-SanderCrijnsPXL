import { Component, Input } from '@angular/core';
import { ReviewService } from '../../services/review.service';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-review-buttons',
  templateUrl: './review-buttons.component.html',
  styleUrls: ['./review-buttons.component.css'],
  standalone: true,
  imports: [FormsModule]
})
export class ReviewButtonsComponent {
  @Input() postId!: number;
  showRejectForm = false;
  comment = '';

  constructor(private reviewService: ReviewService) {}

  approve() {
    this.reviewService.approvePost(this.postId).subscribe(() => {
      alert('Post approved successfully!');
    });
  }

  toggleReject() {
    this.showRejectForm = !this.showRejectForm;
  }

  reject() {
    this.reviewService.rejectPost(this.postId, this.comment).subscribe(() => {
      alert('Post rejected with comment.');
      this.showRejectForm = false;
      this.comment = '';
    });
  }
}

