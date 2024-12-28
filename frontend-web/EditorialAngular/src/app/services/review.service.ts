import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  apiUrl = environment.apiUrl + '/review';
  http: HttpClient = inject(HttpClient);

  public approvePost(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/approve/${id}`, {});
  }

  public rejectPost(id: number, comment: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reject/${id}`, comment);
  }
}