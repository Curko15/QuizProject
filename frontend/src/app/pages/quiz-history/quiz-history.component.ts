import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {AxiosService} from '../../axios.service';
import {DatePipe, NgClass, NgForOf, NgIf} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-quiz-history',
  templateUrl: './quiz-history.component.html',
  styleUrls: ['./quiz-history.component.css'],
  standalone: true,
  imports: [NgForOf, DatePipe, NgIf, NgClass]
})
export class QuizHistoryComponent implements OnInit {
  results: any[] = [];
  login: string | null = '';
  userRole: string | null = '';

  constructor(private auth: AuthService, private axios: AxiosService, private router: Router) {}

  ngOnInit(): void {
    this.login = this.auth.getLogin();
    this.userRole = this.auth.getUserRole();

    if (this.login && this.userRole) {
      const path = this.userRole === 'TEACHER' ? 'teacher' : 'student';
      this.axios.request('GET', `/results/${path}/${this.login}`, null)
        .then(res => {
          this.results = res.data

          }
        )
        .catch(err => console.error('Greška kod dohvaćanja rezultata:', err));
    }
  }


  goBack(): void {
    this.router.navigate(['/welcome']);
  }
}
