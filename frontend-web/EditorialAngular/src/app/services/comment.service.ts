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

  public getCommentsForPost(postId: number): Observable<Comment[]> {
    console.log('getCommentsForPost');
    return this.http.get<Comment[]>(`${this.apiUrl}/${postId}`);
  }

  public addCommentToPost(postId: number, comment: CommentRequest): Observable<void> {
    console.log('addCommentToPost');
    console.log(postId);
    console.log(comment);
    return this.http.post<void>(`${this.apiUrl}/add/${postId}`, comment);
  }

  public deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  public editComment(id: number, content: String): Observable<Comment> {
    return this.http.patch<Comment>(`${this.apiUrl}/${id}/edit`, content);
  }

}
