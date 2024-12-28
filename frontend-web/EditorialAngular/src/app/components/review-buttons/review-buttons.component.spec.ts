import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewButtonsComponent } from './review-buttons.component';

describe('ReviewButtonsComponent', () => {
  let component: ReviewButtonsComponent;
  let fixture: ComponentFixture<ReviewButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
