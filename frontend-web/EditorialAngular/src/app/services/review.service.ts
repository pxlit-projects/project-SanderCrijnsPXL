import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostReview } from '../models/post-review.model';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  apiUrl = environment.apiUrl + '/review';
  http: HttpClient = inject(HttpClient);

  public approvePost(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/approve/${id}`, {});
  }

  public rejectPost(id: number, comment: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reject/${id}`, comment);
  }

  public getPostsToReview(): Observable<PostReview[]> {
    const getPostsUrl = this.apiUrl + '/posts-to-review';

    console.log('Fetching posts from:', getPostsUrl);

    return this.http.get<PostReview[]>(getPostsUrl);
  }
}