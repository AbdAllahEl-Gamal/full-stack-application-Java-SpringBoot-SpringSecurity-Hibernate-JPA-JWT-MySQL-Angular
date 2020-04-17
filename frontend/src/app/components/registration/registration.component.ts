import { Component, OnInit } from '@angular/core';
import { Router } from  '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { User } from '../../interfaces/user';
import { AuthService } from  '../../services/auth/auth.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

  registrationForm: FormGroup;
  isSubmitted = false;
  isFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.registrationForm = this.formBuilder.group({
      name: ['', Validators.required],
      username: ['', Validators.required],
      email: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  get formControls() { 
    return this.registrationForm.controls; 
  }

  registrationSubmit() {
    this.authService.registration(this.registrationForm.value).subscribe(
      data => {
        console.log(data);
        this.isSubmitted = true;
        this.isFailed = false;
      },
      err => {
        this.errorMessage = err.error.message;
        this.isFailed = true;
      }
    );
  }
}