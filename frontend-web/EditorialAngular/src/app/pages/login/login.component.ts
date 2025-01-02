import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  router: Router = inject(Router);

  loginForm: FormGroup;

  constructor() {
    this.loginForm = new FormGroup({
      username: new FormControl('', [Validators.required]),
      role: new FormControl('user', [Validators.required]) 
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      // Store form data in localStorage
      const loginData = this.loginForm.value;
      localStorage.setItem('role', loginData.role);
      localStorage.setItem('username', loginData.username);

      // Navigate to the next page
      this.router.navigate(['/published-posts']);
    }
  }
}
