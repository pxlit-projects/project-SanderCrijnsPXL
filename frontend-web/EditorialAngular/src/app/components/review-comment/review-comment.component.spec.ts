import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewCommentComponent } from './review-comment.component';

describe('ReviewCommentComponent', () => {
  let component: ReviewCommentComponent;
  let fixture: ComponentFixture<ReviewCommentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewCommentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewCommentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
