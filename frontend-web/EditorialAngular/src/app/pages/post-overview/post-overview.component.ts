import { Component, inject, OnInit } from '@angular/core';
import { PostService } from '../../services/post.service';
import { PostItemComponent } from "../../components/post-item/post-item.component";
import { CommonModule } from '@angular/common';
import { PostFilterComponent } from '../../components/post-filter/post-filter.component';
import { PostItemButtonsComponent } from "../../components/post-item-buttons/post-item-buttons.component";
import { PublishedPost } from '../../models/published-post.model';

@Component({
  selector: 'app-post-overview',
  templateUrl: './post-overview.component.html',
  styleUrls: ['./post-overview.component.css'],
  standalone: true,
  imports: [PostItemComponent, PostFilterComponent, CommonModule, PostItemButtonsComponent]
})
export class PostOverviewComponent implements OnInit {
  postService: PostService = inject(PostService);
  posts: PublishedPost[] = [];
  filteredPosts: PublishedPost[] = [];

  ngOnInit(): void {
    this.postService.getPublishedPosts().subscribe(
      (loadedposts) => {
        this.posts = loadedposts;
        this.filteredPosts = loadedposts;
      },
      (error) => {
        console.error('Error fetching posts', error);
      }
    );
  }

  applyFilters(filters: any): void {
    this.filteredPosts = this.posts.filter(post => 
      (filters.content ? post.content.toLowerCase().includes(filters.content.toLowerCase()) : true) &&
      (filters.author ? post.author.toLowerCase().includes(filters.author.toLowerCase()) : true) &&
      (filters.date ? new Date(post.dateCreated).toISOString().split('T')[0] === filters.date : true)
    );
  }
}
