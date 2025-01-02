import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostItemButtonsComponent } from './post-item-buttons.component';
import { CommentService } from '../../services/comment.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { CommentRequest } from '../../models/request/comment-request.model';
import { Comment } from '../../models/comment.model';
import { By } from '@angular/platform-browser';

describe('PostItemButtonsComponent', () => {
  let component: PostItemButtonsComponent;
  let fixture: ComponentFixture<PostItemButtonsComponent>;
  let commentService: CommentService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostItemButtonsComponent],
      providers: [
        CommentService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostItemButtonsComponent);
    component = fixture.componentInstance;
    commentService = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle comment box visibility', () => {
    expect(component.showCommentBox).toBeFalse();

    component.toggleCommentBox();
    expect(component.showCommentBox).toBeTrue();

    component.toggleCommentBox();
    expect(component.showCommentBox).toBeFalse();
  });

  it('should toggle comments visibility', () => {
    expect(component.showComments).toBeFalse();

    component.toggleComments();
    expect(component.showComments).toBeTrue();

    component.toggleComments();
    expect(component.showComments).toBeFalse();
  });

  it('should edit a comment', () => {
    spyOn(window, 'prompt').and.returnValue('Updated Comment');
    const comment: Comment = {
      id: 1, content: 'Original Comment', author: 'Test Author',
      postId: '1'
    };
    component.comments = [comment];
    spyOn(localStorage, 'getItem').and.returnValue('Test Author');

    component.editComment(comment);

    const req = httpMock.expectOne(`${commentService.apiUrl}/1/edit`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ ...comment, content: 'Updated Comment' });
    req.flush({});

    expect(comment.content).toBe('Updated Comment');
  });

  it('should delete a comment', () => {
    spyOn(window, 'alert');
    const comment: Comment = { id: 1, content: 'Test Comment', author: 'Test Author', postId: '1' };
    component.comments = [comment];
    spyOn(localStorage, 'getItem').and.returnValue('Test Author');

    component.deleteComment(comment);

    const req = httpMock.expectOne(`${commentService.apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});

    expect(component.comments.length).toBe(0);
  });

  it('should not edit a comment if user is not the author', () => {
    spyOn(window, 'alert');
    const comment: Comment = { id: 1, content: 'Test Comment', author: 'Other Author', postId: '1' };
    component.comments = [comment];
    spyOn(localStorage, 'getItem').and.returnValue('Test Author');

    component.editComment(comment);

    expect(window.alert).toHaveBeenCalledWith('You can only edit your own comments!');
  });

  it('should not delete a comment if user is not the author', () => {
    spyOn(window, 'alert');
    const comment: Comment = { id: 1, content: 'Test Comment', author: 'Other Author', postId: '1' };
    component.comments = [comment];
    spyOn(localStorage, 'getItem').and.returnValue('Test Author');

    component.deleteComment(comment);

    expect(window.alert).toHaveBeenCalledWith('You can only edit your own comments!');
  });

  it('should display comment box when toggleCommentBox is called', () => {
    component.toggleCommentBox();
    fixture.detectChanges();

    const commentBox: DebugElement = fixture.debugElement.query(By.css('.comment-box'));
    expect(commentBox).toBeTruthy();
  });
});
