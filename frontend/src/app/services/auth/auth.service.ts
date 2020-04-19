import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from "rxjs";

import { ApiResponse } from "../../interfaces/api.response";
import { User } from '../../interfaces/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private httpClient: HttpClient) { }

  baseUrl: string = 'http://localhost:5000/api/auth';

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  login(loginPayload): Observable<any> {
    return this.httpClient.post(`${this.baseUrl}/signin`, {
      usernameOrEmail: loginPayload.username,
      password: loginPayload.password
    }, this.httpOptions);
  }

  registration(user: User): Observable<any> {
    return this.httpClient.post<ApiResponse>(`${this.baseUrl}/signup`, {
      name: user.name,
      username: user.username,
      email: user.email,
      password: user.password
    }, this.httpOptions);
  }

  forgotPassword(user: User): Observable<any> {
    return this.httpClient.post<ApiResponse>(`${this.baseUrl}/forgot-password?email=${user.email}`, this.httpOptions);
  }
}