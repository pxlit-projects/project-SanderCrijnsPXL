import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewCommentComponent } from './review-comment.component';
import { By } from '@angular/platform-browser';

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

  it('should display the comment', () => {
    component.comment = 'Test Comment';
    fixture.detectChanges();

    const commentElement = fixture.debugElement.query(By.css('p')).nativeElement;
    expect(commentElement.textContent).toBe('Test Comment');
  });
});
