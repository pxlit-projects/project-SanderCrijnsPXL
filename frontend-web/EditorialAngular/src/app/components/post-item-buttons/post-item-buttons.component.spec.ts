import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostItemButtonsComponent } from './post-item-buttons.component';

describe('PostItemButtonsComponent', () => {
  let component: PostItemButtonsComponent;
  let fixture: ComponentFixture<PostItemButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostItemButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostItemButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
