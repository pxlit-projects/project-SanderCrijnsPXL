import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, of, throwError, timeout } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { Post } from '../models/post.model';
import { PostRequest } from '../models/request/post-request.model';
import { ChangeContentRequest } from '../models/request/change-content-request.model';
import { PublishedPost } from '../models/published-post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  apiUrl = environment.apiUrl + '/posts';
  http: HttpClient = inject(HttpClient);

  private getAuthHeaders(): HttpHeaders {
      const role = localStorage.getItem('role') || 'user';
      return new HttpHeaders({
        'Authorization': `${role}`
      });
    }

  public getAllPosts(): Observable<Post[]> {
    const getPostsUrl = this.apiUrl + '/all'; 
    
    console.log('Fetching posts from:', getPostsUrl);
    
    return this.http.get<Post[]>(getPostsUrl, { headers: this.getAuthHeaders() });
  }

  public getPublishedPosts(): Observable<PublishedPost[]> {
    const getPostsUrl = this.apiUrl + '/published'; 
    
    console.log('Fetching posts from:', getPostsUrl);
    
    return this.http.get<PublishedPost[]>(getPostsUrl);
  }

  public getPostsToReview(): Observable<Post[]> {
    const getPostsUrl = this.apiUrl + '/openForReview'; 
    
    console.log('Fetching posts from:', getPostsUrl);
    
    return this.http.get<Post[]>(getPostsUrl, { headers: this.getAuthHeaders() });
  }

  public createPost(postRequest: PostRequest): Observable<void> {
    console.log('Creating post at:', this.apiUrl);
    
    return this.http.post<void>(this.apiUrl, postRequest, { headers: this.getAuthHeaders() }).pipe(
      catchError((error) => {
        console.error('Error creating post:', error);
        return of();
      })
    );
  }

  public changeContent(id: number, changeContentRequest: ChangeContentRequest): Observable<Post> {
    const changeContentUrl = `${this.apiUrl}/${id}/changeContent`;
    
    console.log('Changing content of post at:', changeContentUrl);
    
    return this.http.patch<Post>(changeContentUrl, changeContentRequest, { headers: this.getAuthHeaders() } ).pipe(
      catchError((error) => {
        console.error('Error changing post content:', error);
        return of();
      })
    );
  }

  public addToReview(id: number): Observable<void> {
    const addToReviewUrl = `${this.apiUrl}/${id}/add-to-review`;
    console.log('Adding post to review at:', addToReviewUrl);
    return this.http.patch<void>(addToReviewUrl, null, { headers: this.getAuthHeaders() }).pipe(
      catchError((error) => {
        console.error('Error adding post to review:', error);
        return throwError(() => error);
      })
    );
  }
}