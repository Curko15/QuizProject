import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {FormsModule} from '@angular/forms';
import axios from 'axios';
import {AxiosService} from '../../axios.service';
import {NgIf} from '@angular/common';  // prilagodi putanju prema tvom projektu

@Component({
  selector: 'app-create-quiz',
  templateUrl: './create-quiz.component.html',
  imports: [
    FormsModule,
    NgIf
  ],
  styleUrls: ['./create-quiz.component.css']
})
export class CreateQuizComponent {

  title: string = '';
  description: string = '';
  showValidation: boolean = false;

  constructor(private router: Router, private axiosService: AxiosService) {}

  createQuiz() {
    if (!this.title.trim() || !this.description.trim()) {
      this.showValidation = true;
      return;
    }
    this.showValidation = false;
    const payload = {
      title: this.title,
      description: this.description
    };

    this.axiosService.request('POST', '/quiz', payload)
      .then((response) => {
        const quizId = response.data.quizId;
        this.router.navigate(['/add-question', quizId]);
      })
      .catch(err => {
        console.error('Greška prilikom kreiranja kviza:', err);
      });
  }

  goBack() {
    this.router.navigate(['/welcome']);
  }
}



