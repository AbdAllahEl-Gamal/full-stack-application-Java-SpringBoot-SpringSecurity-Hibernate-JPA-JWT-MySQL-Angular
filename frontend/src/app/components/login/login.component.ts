import { Component, OnInit } from '@angular/core';
import { Router } from  '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from  '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  isSubmitted = false;

  constructor(private authService: AuthService, private router: Router, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  get formControls() { 
    return this.loginForm.controls; 
  }

  loginSubmit() {
    if (this.loginForm.invalid) {
      return;
    }

    const loginPayload = {
      usernameOrEmail: this.loginForm.controls.username.value,
      password: this.loginForm.controls.password.value
    }

    this.authService.login(loginPayload).subscribe(data => {
      if(data.accessToken != null) {
        sessionStorage.setItem('token', data.accessToken);
        this.router.navigateByUrl('/home');
      } else {
        this.isSubmitted = true;
      }
    });
  }

}
