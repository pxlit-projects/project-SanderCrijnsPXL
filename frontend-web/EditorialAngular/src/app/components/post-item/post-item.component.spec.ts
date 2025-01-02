import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostItemComponent } from './post-item.component';
import { PostItem } from '../../models/post-item.model';
import { By } from '@angular/platform-browser';

describe('PostItemComponent', () => {
  let component: PostItemComponent;
  let fixture: ComponentFixture<PostItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostItemComponent]
    }).compileComponents();
  
    fixture = TestBed.createComponent(PostItemComponent);
    component = fixture.componentInstance;
  
    // Mock de post data
    component.post = {
      title: 'Test Post',
      content: 'This is a test content.',
      author: 'John Doe',
      dateCreated: new Date('2025-01-01')
    };
  
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display post details', () => {
    const post: PostItem = {
      title: 'Test Title',
      content: 'Test Content',
      author: 'Test Author',
      dateCreated: new Date('2023-01-01')
    };

    component.post = post;
    fixture.detectChanges();

    const titleElement = fixture.debugElement.query(By.css('.post-title')).nativeElement;
    const contentElement = fixture.debugElement.query(By.css('.post-content')).nativeElement;
    const authorElement = fixture.debugElement.query(By.css('.post-author')).nativeElement;
    const dateElement = fixture.debugElement.query(By.css('.post-meta p:last-child')).nativeElement;

    expect(titleElement.textContent).toBe('Test Title');
    expect(contentElement.textContent).toBe('Test Content');
    expect(authorElement.textContent).toBe('By: Test Author');
    expect(dateElement.textContent).toBe('01-01-2023');
  });
});
