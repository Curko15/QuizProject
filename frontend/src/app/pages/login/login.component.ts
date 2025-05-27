import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AxiosService} from '../../axios.service';
import {FormsModule} from '@angular/forms';


@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  login: string = '';
  password: string = '';

  constructor(private axiosService: AxiosService, private router: Router) {}

  onLogin(): void {
    this.axiosService.request("POST", "login", {
      login: this.login,
      password: this.password
    }).then((response: { data: { token: any; }; }) => {
      this.axiosService.setAuthToken(response.data.token);
      this.router.navigate(['/welcome']);
    });
  }
}
