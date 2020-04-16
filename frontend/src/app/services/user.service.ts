import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient) { }

  baseUrl: string = 'http://localhost:5000/api/authorization';

  getPublicContent(): Observable<any> {
    return this.httpClient.get(`${this.baseUrl}/all`, { responseType: 'text' });
  }

  getUserBoard(): Observable<any> {
    return this.httpClient.get(`${this.baseUrl}/user`, { responseType: 'text' });
  }

  getAdminBoard(): Observable<any> {
    return this.httpClient.get(`${this.baseUrl}/admin`, { responseType: 'text' });
  }
}