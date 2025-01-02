import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostReview } from '../models/post-review.model';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  apiUrl = environment.apiUrl + '/review';
  http: HttpClient = inject(HttpClient);

  private getAuthHeaders(): HttpHeaders {
    const role = localStorage.getItem('role') || 'user';
    return new HttpHeaders({
      'Authorization': `${role}`
    });
  }

  public approvePost(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/approve/${id}`, {}, {headers: this.getAuthHeaders()});
  }

  public rejectPost(id: number, comment: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reject/${id}`, comment, {headers: this.getAuthHeaders()});
  }

  public getPostsToReview(): Observable<PostReview[]> {
    const getPostsUrl = this.apiUrl + '/posts-to-review';

    console.log('Fetching posts from:', getPostsUrl);

    return this.http.get<PostReview[]>(getPostsUrl, {headers: this.getAuthHeaders()});
  }
}