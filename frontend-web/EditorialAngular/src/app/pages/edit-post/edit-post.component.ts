import { Component, inject } from '@angular/core';
import { PostService } from '../../services/post.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ChangeContentRequest } from '../../models/request/change-content-request.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-post',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './edit-post.component.html',
  styleUrl: './edit-post.component.css'
})
export class EditPostComponent {
  id: number | undefined;
  title: string = '';
  content: string = '';

  postService: PostService = inject(PostService);
  router: Router = inject(Router);
  route: ActivatedRoute = inject(ActivatedRoute);

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.title = window.history.state.title;
    this.content = window.history.state.content;
    
  }

  saveChanges(): void {
    if (this.id){
      const changeContentRequest: ChangeContentRequest = { title: this.title, content: this.content };
      this.postService.changeContent(this.id, changeContentRequest).subscribe((updatedPost) => {
        if (updatedPost) {
          console.log('Post updated successfully', updatedPost);
          this.router.navigate(['/all-posts']);  // Navigate back to the posts list after saving
        } else {
          console.error('Failed to update the post');
        }
      });
    }
  }

  isAuthorized(): boolean {
    console.log('Role:', localStorage.getItem('role'));
    console.log('Is editor:', localStorage.getItem('role') === 'editor');
    return localStorage.getItem('role') === 'editor';
  }
}