import { Component, inject } from '@angular/core';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { PostItemComponent } from "../../components/post-item/post-item.component";
import { Router } from '@angular/router';

@Component({
  selector: 'app-all-posts-overview',
  standalone: true,
  imports: [PostItemComponent],
  templateUrl: './all-posts-overview.component.html',
  styleUrl: './all-posts-overview.component.css'
})
export class AllPostsOverviewComponent {
  postService: PostService = inject(PostService);
  router: Router = inject(Router);
  posts: Post[] = [];
  groupedPosts: { [status: string]: Post[] } = {
    CONCEPT: [],
    REVIEW: [],
    PUBLISHED: []
  };

  ngOnInit(): void {
    this.postService.getAllPosts().subscribe(
      (loadedposts) => {
        this.posts = loadedposts;
        this.groupPostsByStatus();
      },
      (error) => {
        console.error('Error fetching posts', error);
      }
    );
  }

  groupPostsByStatus(): void {
    this.groupedPosts['CONCEPT'] = this.posts.filter(post => post.status === 'CONCEPT');
    this.groupedPosts['REVIEW'] = this.posts.filter(post => post.status === 'REVIEW');
    this.groupedPosts['PUBLISHED'] = this.posts.filter(post => post.status === 'PUBLISHED');
  }

  navigateToEdit(post: Post): void {
    this.router.navigate(['/edit', post.id], {state: { title: post.title, content: post.content }});
  }
}
