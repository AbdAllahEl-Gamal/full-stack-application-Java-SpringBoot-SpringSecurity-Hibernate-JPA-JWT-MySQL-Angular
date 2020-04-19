import { Component, OnInit } from '@angular/core';
import { Router } from  '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { AuthService } from  '../../services/auth/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  forgotPasswordForm: FormGroup;
  isSubmitted = false;
  isFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.forgotPasswordForm = this.formBuilder.group({
      email: ['', Validators.required]
    });
  }

  get formControls() { 
    return this.forgotPasswordForm.controls; 
  }

  forgotPasswordSubmit() {
    console.log(this.forgotPasswordForm.value.email);
    this.isSubmitted = true;
    this.authService.forgotPassword(this.forgotPasswordForm.value).subscribe(
      data => {
        console.log(this.forgotPasswordForm.value);
        console.log(data);
        this.isFailed = false;
      },
      err => {
        this.errorMessage = err.error.message;
        this.isFailed = true;
      }
    );
  }
}