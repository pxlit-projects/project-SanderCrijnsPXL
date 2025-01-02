import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostsReviewOverviewComponent } from './posts-review-overview.component';
import { ReviewService } from '../../services/review.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PostReview } from '../../models/post-review.model';
import { of } from 'rxjs';

describe('PostsReviewOverviewComponent', () => {
  let component: PostsReviewOverviewComponent;
  let fixture: ComponentFixture<PostsReviewOverviewComponent>;
  let reviewService: ReviewService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostsReviewOverviewComponent],
      providers: [
        ReviewService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostsReviewOverviewComponent);
    component = fixture.componentInstance;
    reviewService = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController)
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch posts to review on init', () => {
    const mockPosts: PostReview[] = [
      { id: 1, title: 'Post 1', content: 'Content 1', author: 'Author 1', dateCreated: new Date('2023-01-01'), comment: '' },
      { id: 2, title: 'Post 2', content: 'Content 2', author: 'Author 2', dateCreated: new Date('2023-01-02'), comment: '' }
    ];

    spyOn(reviewService, 'getPostsToReview').and.returnValue(of(mockPosts));

    component.ngOnInit();

    expect(component.posts.length).toBe(2);
    expect(component.posts).toEqual(mockPosts);
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
