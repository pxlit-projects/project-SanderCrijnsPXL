import { Component, inject, OnInit } from '@angular/core';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { PostItemComponent } from "../../components/post-item/post-item.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-overview',
  templateUrl: './post-overview.component.html',
  styleUrls: ['./post-overview.component.css'],
  standalone: true,
  imports: [PostItemComponent, CommonModule]
})
export class PostOverviewComponent implements OnInit {
  postService: PostService = inject(PostService);
  posts: Post[] = [];

  constructor() { }

  ngOnInit(): void {
    this.postService.getPosts().subscribe(
      (loadedposts) => {
        this.posts = loadedposts;
      },
      (error) => {
        console.error('Error fetching posts', error);
      }
    );
  }
}
