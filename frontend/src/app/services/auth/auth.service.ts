import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from "rxjs/index";
import { ApiResponse } from "../../interfaces/api.response";
import { JwtAuthenticationResponse } from "../../interfaces/jwt.authentication.response";
import { User } from '../../interfaces/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private httpClient: HttpClient) { }

  baseUrl: string = 'http://localhost:5000/api/auth';

  login(loginPayload): Observable<JwtAuthenticationResponse> {
    return this.httpClient.post<JwtAuthenticationResponse>(`${this.baseUrl}/signin`, loginPayload);
  }

  registration(user: User): Observable<ApiResponse> {
    return this.httpClient.post<ApiResponse>(`${this.baseUrl}/signup`, user);
  } 

  public isLoggedIn() {
    return sessionStorage.getItem('token') !== null;
  }

  public logout() {
    sessionStorage.removeItem('token');
  }
}