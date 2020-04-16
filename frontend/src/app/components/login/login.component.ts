import { Component, OnInit } from '@angular/core';
import { Router } from  '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { AuthService } from  '../../services/auth/auth.service';
import { TokenStorageService } from  '../../services/auth/token-storage.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  isSubmitted = false;
  isFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(private authService: AuthService, private tokenStorageService: TokenStorageService, private router: Router, private formBuilder: FormBuilder) { }

  ngOnInit() {
    if (this.tokenStorageService.getToken()) {
      this.isSubmitted = true;
      this.roles = this.tokenStorageService.getUser().roles;
    }

    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  get formControls() { 
    return this.loginForm.controls; 
  }

  loginSubmit() {
    this.authService.login(this.loginForm.value).subscribe(
      data => {
        this.tokenStorageService.saveToken(data.accessToken);
        this.tokenStorageService.saveUser(data);

        this.isFailed = false;
        this.isSubmitted = true;
        this.roles = this.tokenStorageService.getUser().roles;
        this.router.navigateByUrl('/home');
      },
      err => {
        this.errorMessage = err.error.message;
        this.isFailed = true;
      }
    );
  }
}