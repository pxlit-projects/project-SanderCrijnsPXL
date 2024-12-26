import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, of, timeout } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { Post } from '../models/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  apiUrl = environment.apiUrl + '/posts/publishedPosts';
  http: HttpClient = inject(HttpClient);

  public getPosts(): Observable<Post[]> {
    console.log('Fetching posts from:', this.apiUrl);
    return this.http.get<Post[]>(this.apiUrl);
  }
}
