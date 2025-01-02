import { Component, inject } from '@angular/core';
import { PostItemComponent } from "../../components/post-item/post-item.component";
import { ReviewButtonsComponent } from "../../components/review-buttons/review-buttons.component";
import { ReviewService } from '../../services/review.service';
import { PostReview } from '../../models/post-review.model';
import { ReviewCommentComponent } from "../../components/review-comment/review-comment.component";

@Component({
  selector: 'app-posts-review-overview',
  standalone: true,
  imports: [PostItemComponent, ReviewButtonsComponent, ReviewCommentComponent],
  templateUrl: './posts-review-overview.component.html',
  styleUrl: './posts-review-overview.component.css'
})
export class PostsReviewOverviewComponent {
  reviewService: ReviewService = inject(ReviewService);
    posts: PostReview[] = [];
  
    ngOnInit(): void {
      this.reviewService.getPostsToReview().subscribe(
        (loadedposts) => {
          this.posts = loadedposts;
        },
        (error) => {
          console.error('Error fetching posts', error);
        }
      );
    }

    isAuthorized(): boolean {
      console.log('Role:', localStorage.getItem('role'));
      console.log('Is editor:', localStorage.getItem('role') === 'editor');
      return localStorage.getItem('role') === 'editor';
    }

}
