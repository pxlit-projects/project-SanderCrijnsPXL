import { TestBed } from '@angular/core/testing';

import { PostService } from './post.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { environment } from '../../environments/environment.development';
import { PostRequest } from '../models/request/post-request.model';
import { ChangeContentRequest } from '../models/request/change-content-request.model';
import { Post } from '../models/post.model';
import { PublishedPost } from '../models/published-post.model';
import { PostStatus } from '../models/post-status.model';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PostService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all posts', () => {
    const mockPosts: Post[] = [
      { id: 1, title: 'Post 1', content: 'Content 1', author: 'Author 1', dateCreated: new Date('2023-01-01'), status: 'published' },
      { id: 2, title: 'Post 2', content: 'Content 2', author: 'Author 2', dateCreated: new Date('2023-01-02'), status: 'published' }
    ];

    service.getAllPosts().subscribe(posts => {
      expect(posts.length).toBe(2);
      expect(posts).toEqual(mockPosts);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/posts/all`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('user');
    req.flush(mockPosts);
  });

  it('should fetch published posts', () => {
    const mockPublishedPosts: PublishedPost[] = [
      { id: 1, title: 'Published Post 1', content: 'Content 1', author: 'Author 1', dateCreated: new Date('2023-01-01'), comments: [] },
      { id: 2, title: 'Published Post 2', content: 'Content 2', author: 'Author 2', dateCreated: new Date('2023-01-02'), comments: [] }
    ];

    service.getPublishedPosts().subscribe(posts => {
      expect(posts.length).toBe(2);
      expect(posts).toEqual(mockPublishedPosts);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/posts/published`);
    expect(req.request.method).toBe('GET');
    req.flush(mockPublishedPosts);
  });

  it('should fetch posts to review', () => {
    const mockPosts: Post[] = [
      { id: 1, title: 'Post 1', content: 'Content 1', author: 'Author 1', dateCreated: new Date('2023-01-01'), status: 'review' },
      { id: 2, title: 'Post 2', content: 'Content 2', author: 'Author 2', dateCreated: new Date('2023-01-02'), status: 'review' }
    ];

    service.getPostsToReview().subscribe(posts => {
      expect(posts.length).toBe(2);
      expect(posts).toEqual(mockPosts);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/posts/openForReview`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('user');
    req.flush(mockPosts);
  });

  it('should create a post', () => {
    const postRequest: PostRequest = { title: 'New Post', content: 'New Content', author: 'New Author', status: PostStatus.REVIEW };

    service.createPost(postRequest).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/posts`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('user');
    req.flush({});
  });

  it('should change post content', () => {
    const postId = 1;
    const changeContentRequest: ChangeContentRequest = { title: 'Updated Title', content: 'Updated Content' };

    service.changeContent(postId, changeContentRequest).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/posts/${postId}/changeContent`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.headers.get('Authorization')).toBe('user');
    req.flush({});
  });
});
