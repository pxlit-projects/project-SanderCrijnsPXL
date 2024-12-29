import { Component, inject } from '@angular/core';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
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

}
