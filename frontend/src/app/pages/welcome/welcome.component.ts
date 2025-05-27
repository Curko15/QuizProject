import { Component, OnInit } from '@angular/core';

import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {AxiosService} from '../../axios.service';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

interface Quiz {
  quizId: number;
  title: string;
  description: string;
}

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  imports: [
    RouterLink,
    FormsModule,
    NgIf,
    NgForOf
  ],
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  quizzes: Quiz[] = [];
  userRole: string | null = null;
  joinCode: string = '';
  fullName: string | null = '';

  constructor(private authService: AuthService, private router: Router, private axiosService: AxiosService) {}

  ngOnInit() {
    this.userRole = this.authService.getUserRole();
    this.fullName = this.authService.getFullName()


    if (this.userRole === 'TEACHER') {
      this.loadTeacherQuizzes();
    }
  }

  loadTeacherQuizzes() {
    this.axiosService.request('GET', '/quizzes', null)
      .then(response => {
        this.quizzes = response.data;
      })
      .catch(err => {
        console.error('Error fetching teacher quizzes:', err);
      });
  }

  joinQuiz() {
    if (!this.joinCode.trim()) {

      return;
    }

    this.axiosService.request('POST', `join`, {
      accessCode: this.joinCode
    })
      .then(() => {
        this.router.navigate(['/quiz-session', this.joinCode]);
      })
      .catch(err => {
        console.error('Greška prilikom pridruživanja kvizu:', err);

      });
  }

  deleteQuiz(quizId: number) {
    if (confirm('Jeste li sigurni da želite obrisati ovaj kviz?')) {
      this.axiosService.request('DELETE', `/quizzes/${quizId}`, null)
        .then(() => {

          this.loadTeacherQuizzes();  // Ponovno učitaj kvizove
        })
        .catch(err => {
          console.error('Error deleting quiz:', err);
        });
    }
  }
  startQuiz(quizId: number) {
    this.axiosService.request('POST', `/start/${quizId}`, null)
      .then(response => {
        const accessCode = response.data;
        this.router.navigate(['/quiz-session', accessCode]);
      })
      .catch(err => {
        console.error('Greška prilikom pokretanja kviza:', err);

      });
  }



}

