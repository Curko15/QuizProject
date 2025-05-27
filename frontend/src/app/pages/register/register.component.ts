import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AxiosService} from '../../axios.service';
import {FormsModule} from '@angular/forms';


@Component({
  selector: 'app-register',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  firstName = '';
  lastName = '';
  login = '';
  password = '';
  role = 'STUDENT';

  constructor(private axiosService: AxiosService, private router: Router) {}

  onRegister(): void {
    this.axiosService.request("POST", "/register", {
      firstName: this.firstName,
      lastName: this.lastName,
      login: this.login,
      password: this.password,
      role: this.role
    }).then(response => {
      this.axiosService.setAuthToken(response.data.token);
      this.router.navigate(['/welcome']);
    }).catch(error => {

    });
  }
}

