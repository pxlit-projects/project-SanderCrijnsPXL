import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  router: Router = inject(Router);
  loginData = {
    username: '',
    role: 'user'
  };

  onSubmit() {
    localStorage.setItem('role', this.loginData.role);
    localStorage.setItem('username', this.loginData.username);
    this.router.navigate(['/published-posts']);
  }
}