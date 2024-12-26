import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostsReviewOverviewComponent } from './posts-review-overview.component';

describe('PostsReviewOverviewComponent', () => {
  let component: PostsReviewOverviewComponent;
  let fixture: ComponentFixture<PostsReviewOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostsReviewOverviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostsReviewOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
