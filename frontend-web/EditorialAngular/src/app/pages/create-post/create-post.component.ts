import { Component, inject } from '@angular/core';
import { PostService } from '../../services/post.service';
import { PostRequest } from '../../models/request/post-request.model';
import { PostStatus } from '../../models/post-status.model';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.css'],
  standalone: true,
  imports: [FormsModule]
})
export class CreatePostComponent {
  private isFormSubmitted: boolean = false;

  postRequest: PostRequest = {
    title: '',
    content: '',
    author: '',
    status: PostStatus.CONCEPT
  };
  postService: PostService = inject(PostService);

  // Save as Concept
  saveAsConcept() {
    this.postRequest.status = PostStatus.CONCEPT;
    this.submitPost();
  }

  // Send to Review
  sendToReview() {
    this.postRequest.status = PostStatus.REVIEW;
    this.submitPost();
  }

  // Submit Post to Backend
  private submitPost() {
    if (this.postRequest.title && this.postRequest.content && this.postRequest.author) {
      this.postService.createPost(this.postRequest).subscribe({
        next: () => {
          alert('Post successfully submitted!');
          this.clearForm();
        }, error: (err) => console.error('Error:', err)
      });
    } else {
      alert('Please fill in all required fields!');
    }
  }

  // Clear form fields
  private clearForm() {
    // Reset the postRequest object to default values
    this.postRequest = {
      title: '',
      content: '',
      author: '',
      status: PostStatus.CONCEPT
    };
  }

  isAuthorized(): boolean {
    console.log('Role:', localStorage.getItem('role'));
    console.log('Is editor:', localStorage.getItem('role') === 'editor');
    return localStorage.getItem('role') === 'editor';
  }

  canDeactivate(): boolean | Observable<boolean> {
    if (this.isFormSubmitted) {
      return true;
    }
    return confirm('You have an unsaved post. Are you sure you want to leave?');
  }
  
}