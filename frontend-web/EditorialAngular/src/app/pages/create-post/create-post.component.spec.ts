import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatePostComponent } from './create-post.component';
import { PostService } from '../../services/post.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { PostStatus } from '../../models/post-status.model';
import { of } from 'rxjs';

describe('CreatePostComponent', () => {
  let component: CreatePostComponent;
  let fixture: ComponentFixture<CreatePostComponent>;
  let postService: PostService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreatePostComponent],
      providers: [
        PostService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatePostComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController)
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should save post as concept', () => {
    spyOn(postService, 'createPost').and.returnValue(of(void 0));

    component.postRequest = {
      title: 'Test Title',
      content: 'Test Content',
      author: 'Test Author',
      status: PostStatus.CONCEPT
    };

    component.saveAsConcept();

    expect(component.postRequest.status).toBe(PostStatus.CONCEPT);
    expect(postService.createPost).toHaveBeenCalledWith({
      title: 'Test Title',
      content: 'Test Content',
      author: 'Test Author',
      status: PostStatus.CONCEPT
    });
  });

  it('should clear form after successful submission', () => {
    spyOn(postService, 'createPost').and.returnValue(of(void 0));
    spyOn(window, 'alert');

    component.postRequest = {
      title: 'Test Title',
      content: 'Test Content',
      author: 'Test Author',
      status: PostStatus.REVIEW
    };

    component.sendToReview();

    expect(window.alert).toHaveBeenCalledWith('Post successfully submitted!');
    expect(component.postRequest.title).toBe('');
    expect(component.postRequest.content).toBe('');
    expect(component.postRequest.author).toBe('');
    expect(component.postRequest.status).toBe(PostStatus.CONCEPT);
  });

  it('should not submit post if required fields are missing', () => {
    spyOn(postService, 'createPost');
    spyOn(window, 'alert');

    component.postRequest = {
      title: '',
      content: '',
      author: '',
      status: PostStatus.CONCEPT
    };

    component.sendToReview();

    expect(window.alert).toHaveBeenCalledWith('Please fill in all required fields!');
    expect(postService.createPost).not.toHaveBeenCalled();
  });

  it('should return true if user is authorized', () => {
    spyOn(localStorage, 'getItem').and.callFake((key: string) => {
      return key === 'role' ? 'editor' : null;
    });

    expect(component.isAuthorized()).toBeTrue();
  });

  it('should return false if user is not authorized', () => {
    spyOn(localStorage, 'getItem').and.callFake((key: string) => {
      return key === 'role' ? 'user' : null;
    });

    expect(component.isAuthorized()).toBeFalse();
  });
});
