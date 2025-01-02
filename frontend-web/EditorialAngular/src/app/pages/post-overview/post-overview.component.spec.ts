import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostOverviewComponent } from './post-overview.component';
import { PostService } from '../../services/post.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PublishedPost } from '../../models/published-post.model';
import { of } from 'rxjs';

describe('PostOverviewComponent', () => {
  let component: PostOverviewComponent;
  let fixture: ComponentFixture<PostOverviewComponent>;
  let postService: PostService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostOverviewComponent],
      providers: [
              PostService,
              provideHttpClient(),
              provideHttpClientTesting(),
            ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostOverviewComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should fetch published posts on init', () => {
    const mockPosts: PublishedPost[] = [
      { id: 1, title: 'Post 1', content: 'Content 1', author: 'Author 1', dateCreated: new Date('2023-01-01'), comments: [] },
      { id: 2, title: 'Post 2', content: 'Content 2', author: 'Author 2', dateCreated: new Date('2023-01-02'), comments: [] }
    ];

    spyOn(postService, 'getPublishedPosts').and.returnValue(of(mockPosts));

    component.ngOnInit();

    expect(component.posts.length).toBe(2);
    expect(component.posts).toEqual(mockPosts);
    expect(component.filteredPosts).toEqual(mockPosts);
  });

  it('should filter posts based on content, author, and date', () => {
    component.posts = [
      { id: 1, title: 'Post 1', content: 'Content 1', author: 'Author 1', dateCreated: new Date('2023-01-01'), comments: [] },
      { id: 2, title: 'Post 2', content: 'Content 2', author: 'Author 2', dateCreated: new Date('2023-01-02'), comments: [] }
    ];
    component.filteredPosts = component.posts;

    component.applyFilters({ content: 'Content 1', author: 'Author 1', date: '2023-01-01' });

    expect(component.filteredPosts.length).toBe(1);
    expect(component.filteredPosts[0].title).toBe('Post 1');
  });

  it('should show all posts if no filters are applied', () => {
    component.posts = [
      { id: 1, title: 'Post 1', content: 'Content 1', author: 'Author 1', dateCreated: new Date('2023-01-01'), comments: [] },
      { id: 2, title: 'Post 2', content: 'Content 2', author: 'Author 2', dateCreated: new Date('2023-01-02'), comments: [] }
    ];
    component.filteredPosts = component.posts;

    component.applyFilters({});

    expect(component.filteredPosts.length).toBe(2);
  });
});
