import { TestBed } from '@angular/core/testing';

import { ReviewService } from './review.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment.development';
import { PostReview } from '../models/post-review.model';

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ReviewService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should approve a post', () => {
    const postId = 1;
    service.approvePost(postId).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/review/approve/${postId}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('user');
    req.flush({});
  });

  it('should reject a post', () => {
    const postId = 1;
    const comment = 'Inappropriate content';
    service.rejectPost(postId, comment).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/review/reject/${postId}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBe(comment);
    expect(req.request.headers.get('Authorization')).toBe('user');
    req.flush({});
  });

  it('should get posts to review', () => {
    const mockPosts: PostReview[] = [
      { id: 1, title: 'Post 1', content: 'Content 1', author: 'Author 1', dateCreated: new Date('2023-01-01'), comment: '' },
      { id: 2, title: 'Post 2', content: 'Content 2', author: 'Author 2', dateCreated: new Date('2023-01-02'), comment: '' }
    ];

    service.getPostsToReview().subscribe(posts => {
      expect(posts.length).toBe(2);
      expect(posts).toEqual(mockPosts);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/review/posts-to-review`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('user');
    req.flush(mockPosts);
  });

});
