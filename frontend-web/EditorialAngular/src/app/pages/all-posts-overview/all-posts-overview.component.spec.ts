import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllPostsOverviewComponent } from './all-posts-overview.component';
import { PostService } from '../../services/post.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { Post } from '../../models/post.model';

describe('AllPostsOverviewComponent', () => {
  let component: AllPostsOverviewComponent;
  let fixture: ComponentFixture<AllPostsOverviewComponent>;
  let postService: PostService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllPostsOverviewComponent],
      providers: [
              PostService,
              provideHttpClient(),
              provideHttpClientTesting(),
            ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllPostsOverviewComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch all posts on init and group them by status', () => {
    const mockPosts: Post[] = [
      { id: 1, title: 'Post 1', content: 'Content 1', author: 'Author 1', dateCreated: new Date('2023-01-01'), status: 'CONCEPT' },
      { id: 2, title: 'Post 2', content: 'Content 2', author: 'Author 2', dateCreated: new Date('2023-01-02'), status: 'REVIEW' },
      { id: 3, title: 'Post 3', content: 'Content 3', author: 'Author 3', dateCreated: new Date('2023-01-03'), status: 'PUBLISHED' }
    ];

    spyOn(postService, 'getAllPosts').and.returnValue(of(mockPosts));

    component.ngOnInit();

    expect(component.posts.length).toBe(3);
    expect(component.groupedPosts['CONCEPT'].length).toBe(1);
    expect(component.groupedPosts['REVIEW'].length).toBe(1);
    expect(component.groupedPosts['PUBLISHED'].length).toBe(1);
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
