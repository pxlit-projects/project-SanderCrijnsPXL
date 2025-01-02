import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewButtonsComponent } from './review-buttons.component';
import { ReviewService } from '../../services/review.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('ReviewButtonsComponent', () => {
  let component: ReviewButtonsComponent;
  let fixture: ComponentFixture<ReviewButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewButtonsComponent],
      providers: [
              ReviewService,
              provideHttpClient(),
              provideHttpClientTesting(),
            ],
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
