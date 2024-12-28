import { Component, inject } from '@angular/core';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { PostItemComponent } from "../../components/post-item/post-item.component";

@Component({
  selector: 'app-posts-review-overview',
  standalone: true,
  imports: [PostItemComponent],
  templateUrl: './posts-review-overview.component.html',
  styleUrl: './posts-review-overview.component.css'
})
export class PostsReviewOverviewComponent {
  postService: PostService = inject(PostService);
    posts: Post[] = [];
  
    ngOnInit(): void {
      this.postService.getPostsToReview().subscribe(
        (loadedposts) => {
          this.posts = loadedposts;
        },
        (error) => {
          console.error('Error fetching posts', error);
        }
      );
    }

}
