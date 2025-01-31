import { Component, Input } from '@angular/core';
import { CommentRequest } from '../../models/request/comment-request.model';
import { CommentService } from '../../services/comment.service';
import { FormsModule } from '@angular/forms';
import { Comment } from '../../models/comment.model';

@Component({
  selector: 'app-post-item-buttons',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './post-item-buttons.component.html',
  styleUrl: './post-item-buttons.component.css'
})
export class PostItemButtonsComponent {
  @Input() postId!: number;
  @Input() comments!: Comment[];
  showCommentBox = false;
  showComments = false;
  commentRequest = new CommentRequest('', '');

  constructor(private commentService: CommentService) {}

  toggleCommentBox() {
    this.showCommentBox = !this.showCommentBox;
  }

  toggleComments() {
    this.showComments = !this.showComments;
  }

  submitComment() {
    this.commentService.addCommentToPost(this.postId, this.commentRequest).subscribe(() => {
      alert('Comment added successfully!');
      this.showCommentBox = false;
      this.commentRequest.content = '';
    });
    window.location.reload();
  }

  editComment(comment: any) {
    if(this.isAuthor(comment)) {
      return alert('You can only edit your own comments!');
    }
    else{
      const newContent = prompt('Edit your comment:', comment.content);
      if (newContent) {
        const editRequest = { ...comment, content: newContent };
        this.commentService.editComment(comment.id, editRequest).subscribe(() => {
          comment.content = newContent;
        });
      }
    }
  }

  deleteComment(comment: any) {
    if(this.isAuthor(comment)) {
      return alert('You can only edit your own comments!');
    }
    else{
      this.commentService.deleteComment(comment.id).subscribe(() => {
        this.comments = this.comments.filter(c => c.id !== comment.id);
      });
    }
    
  }

  private isAuthor (comment: any) : boolean {
    return comment.author.toLowerCase() !== localStorage.getItem('username')?.toLowerCase()
  }

}
