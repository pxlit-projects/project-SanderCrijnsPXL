import { Component, Input } from '@angular/core';
import { CommentRequest } from '../../models/request/comment-request.model';
import { CommentService } from '../../services/comment.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-post-item-buttons',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './post-item-buttons.component.html',
  styleUrl: './post-item-buttons.component.css'
})
export class PostItemButtonsComponent {
  @Input() postId!: number;
  showCommentBox = false;
  commentRequest = new CommentRequest('', '');
  comments: any[] = [];

  constructor(private commentService: CommentService) {}

  toggleCommentBox() {
    this.showCommentBox = !this.showCommentBox;
  }

  submitComment() {
    this.commentService.addCommentToPost(this.postId, this.commentRequest).subscribe(() => {
      alert('Comment added successfully!');
      this.showCommentBox = false;
      this.commentRequest.content = '';
    });
  }

  getComments() {
    this.commentService.getCommentsForPost(this.postId).subscribe((response) => {
      this.comments = response;
    });
  }

  editComment(comment: any) {
    const newContent = prompt('Edit your comment:', comment.content);
    if (newContent) {
      const editRequest = { ...comment, content: newContent };
      this.commentService.editComment(comment.id, editRequest).subscribe(() => {
        comment.content = newContent;
      });
    }
  }

  deleteComment(commentId: number) {
    this.commentService.deleteComment(commentId).subscribe(() => {
      this.comments = this.comments.filter(c => c.id !== commentId);
    });
  }
}