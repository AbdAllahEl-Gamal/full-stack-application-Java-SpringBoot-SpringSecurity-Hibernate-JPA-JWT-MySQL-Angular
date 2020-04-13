import { Component, OnInit } from '@angular/core';
import { AuthService } from  '../../services/auth/auth.service';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss']
})
export class NavComponent implements OnInit {

  userName = '';

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.userName = sessionStorage.getItem('loggedUser');
  }

  logout() {
    return this.authService.logout();
  }

}
