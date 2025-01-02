import { TestBed } from '@angular/core/testing';

import { CommentService } from './comment.service';
import { ReviewService } from './review.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { environment } from '../../environments/environment.development';
import { CommentRequest } from '../models/request/comment-request.model';

describe('CommentService', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
              ReviewService,
              provideHttpClient(),
              provideHttpClientTesting(),
            ],
    });
    service = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add a comment to a post', () => {
    const postId = 1;
    const commentRequest: CommentRequest = { content: 'New Comment', author: 'Author' };

    service.addCommentToPost(postId, commentRequest).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/comments/add/${postId}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(commentRequest);
    req.flush({});
  });

  it('should delete a comment', () => {
    const commentId = 1;

    service.deleteComment(commentId).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/comments/${commentId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should edit a comment', () => {
    const commentId = 1;
    const updatedContent = 'Updated Comment';

    service.editComment(commentId, updatedContent).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/comments/${commentId}/edit`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toBe(updatedContent);
    req.flush({});
  });
});
