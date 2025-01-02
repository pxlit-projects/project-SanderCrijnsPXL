import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule, RouterTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have a valid form when username and role are provided', () => {
    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['role'].setValue('user');
    expect(component.loginForm.valid).toBeTrue();
  });

  it('should have an invalid form when username is not provided', () => {
    component.loginForm.controls['username'].setValue('');
    component.loginForm.controls['role'].setValue('user');
    expect(component.loginForm.invalid).toBeTrue();
  });

  it('should store form data in localStorage and navigate on valid form submission', () => {
    spyOn(localStorage, 'setItem');
    spyOn(router, 'navigate');

    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['role'].setValue('user');
    component.onSubmit();

    expect(localStorage.setItem).toHaveBeenCalledWith('role', 'user');
    expect(localStorage.setItem).toHaveBeenCalledWith('username', 'testuser');
    expect(router.navigate).toHaveBeenCalledWith(['/published-posts']);
  });

  it('should not navigate on invalid form submission', () => {
    spyOn(router, 'navigate');

    component.loginForm.controls['username'].setValue('');
    component.loginForm.controls['role'].setValue('user');
    component.onSubmit();

    expect(router.navigate).not.toHaveBeenCalled();
  });
});