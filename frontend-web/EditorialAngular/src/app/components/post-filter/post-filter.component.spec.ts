import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostFilterComponent } from './post-filter.component';
import { By } from '@angular/platform-browser';

describe('PostFilterComponent', () => {
  let component: PostFilterComponent;
  let fixture: ComponentFixture<PostFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostFilterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit filterChanged event when filters are applied', () => {
    spyOn(component.filterChanged, 'emit');

    component.filters.content = 'Test Content';
    component.applyFilters();

    expect(component.filterChanged.emit).toHaveBeenCalledWith({
      content: 'Test Content',
      author: '',
      date: ''
    });
  });

  it('should clear filters and emit filterChanged event', () => {
    spyOn(component.filterChanged, 'emit');

    component.filters = { content: 'Test Content', author: 'Test Author', date: '2023-01-01' };
    component.clearFilters();

    expect(component.filters).toEqual({ content: '', author: '', date: '' });
    expect(component.filterChanged.emit).toHaveBeenCalledWith({
      content: '',
      author: '',
      date: ''
    });
  });

  it('should update filters and call applyFilters on input change', () => {
    spyOn(component, 'applyFilters');

    const contentInput = fixture.debugElement.query(By.css('input[placeholder="Filter by content"]')).nativeElement;
    contentInput.value = 'New Content';
    contentInput.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    expect(component.filters.content).toBe('New Content');
    expect(component.applyFilters).toHaveBeenCalled();
  });
});
