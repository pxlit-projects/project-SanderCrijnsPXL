import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommentRequest } from '../models/request/comment-request.model';


@Injectable({
  providedIn: 'root'
})
export class CommentService {

  apiUrl = environment.apiUrl + '/comments';
  http: HttpClient = inject(HttpClient);

  getCommentsForPost(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/${postId}`);
  }

  addCommentToPost(postId: number, comment: CommentRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/add/${postId}`, comment);
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  editComment(id: number, content: String): Observable<Comment> {
    return this.http.patch<Comment>(`${this.apiUrl}/${id}/edit`, content);
  }

}
