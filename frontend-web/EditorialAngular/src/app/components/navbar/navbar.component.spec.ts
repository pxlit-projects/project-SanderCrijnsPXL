import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavbarComponent } from './navbar.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NavbarComponent,
        RouterTestingModule  // Zorgt voor een mock router omgeving
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have router links', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const links = compiled.querySelectorAll('a');
    expect(links.length).toBe(5);
    expect(links[0].getAttribute('routerLink')).toBe('/');
    expect(links[1].getAttribute('routerLink')).toBe('/published-posts');
    expect(links[2].getAttribute('routerLink')).toBe('/create');
    expect(links[3].getAttribute('routerLink')).toBe('/all-posts');
    expect(links[4].getAttribute('routerLink')).toBe('/review');
  });
});
