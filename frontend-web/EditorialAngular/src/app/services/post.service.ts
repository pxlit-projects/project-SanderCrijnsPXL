import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, of, timeout } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { Post } from '../models/post.model';
import { PostRequest } from '../models/post-request.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  apiUrl = environment.apiUrl + '/posts';
  http: HttpClient = inject(HttpClient);

  public getPosts(): Observable<Post[]> {
    const getPostsUrl = this.apiUrl + '/publishedPosts'; 
    
    console.log('Fetching posts from:', getPostsUrl);
    
    return this.http.get<Post[]>(getPostsUrl);
  }

  public createPost(postRequest: PostRequest): Observable<void> {
    console.log('Creating post at:', this.apiUrl);
    
    return this.http.post<void>(this.apiUrl, postRequest).pipe(
      catchError((error) => {
        console.error('Error creating post:', error);
        return of();
      })
    );
  }
}