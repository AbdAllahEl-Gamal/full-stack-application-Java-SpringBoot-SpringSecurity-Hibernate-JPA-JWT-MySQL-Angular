import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() { }

  TOKEN_KEY: string = 'auth-token';
  USER_KEY: string = 'auth-user';

  saveToken(token: string) {
    window.sessionStorage.removeItem(this.TOKEN_KEY);
    window.sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  saveUser(user) {
    window.sessionStorage.removeItem(this.USER_KEY);
    window.sessionStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  getUser() {
    return JSON.parse(sessionStorage.getItem(this.USER_KEY));
  }

  logout() {
    window.sessionStorage.clear();
  }

}