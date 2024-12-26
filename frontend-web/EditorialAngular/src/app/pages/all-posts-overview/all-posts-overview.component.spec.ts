import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllPostsOverviewComponent } from './all-posts-overview.component';

describe('AllPostsOverviewComponent', () => {
  let component: AllPostsOverviewComponent;
  let fixture: ComponentFixture<AllPostsOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllPostsOverviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllPostsOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
