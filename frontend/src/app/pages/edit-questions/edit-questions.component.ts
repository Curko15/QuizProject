import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {AxiosService} from '../../axios.service';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';


interface Question {
  quizId: number;
  questionText: string;
  options: string[];
  correctAnswer: string;
}

@Component({
  selector: 'app-edit-questions',
  templateUrl: './edit-questions.component.html',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  styleUrls: ['./edit-questions.component.css']
})
export class EditQuestionsComponent implements OnInit {

  quizId: string = '';
  questions: Question[] = [];
  newQuestionText: string = '';
  newOptions = [
    { id: 1, value: '' },
    { id: 2, value: '' },
    { id: 3, value: '' },
    { id: 4, value: '' }
  ];
  newCorrectAnswer: string = '';
  showValidation: boolean = false;

  constructor(private route: ActivatedRoute, private axiosService: AxiosService, private router: Router) {
    this.quizId = this.route.snapshot.paramMap.get('quizId') || '';
  }

  ngOnInit() {
    this.loadQuestions();
  }

  loadQuestions() {
    this.axiosService.request('GET', `/questions/${this.quizId}`, null)
      .then(response => {
        this.questions = response.data;

      })
      .catch(err => {
        console.error('Error loading questions:', err);
      });
  }

  addQuestion() {
    const options = this.newOptions.map(opt => opt.value);

    if (options.includes('') || !this.newCorrectAnswer || !this.newQuestionText) {
      this.showValidation = true;
      return;
    }

    this.showValidation = false;

    const payload = {
      quizId: this.quizId,
      questionText: this.newQuestionText,
      options: options,
      correctAnswer: this.newCorrectAnswer
    };

    this.axiosService.request('POST', '/questions', payload)
      .then(() => {

        this.loadQuestions();
        this.resetForm();
      })
      .catch(err => {
        console.error('Error adding question:', err);
      });
  }

  deleteQuestion(id: number) {

    this.axiosService.request('DELETE', `/questions/${id}`, null)
      .then(() => {
        alert('Pitanje obrisano.');
        this.loadQuestions();
      })
      .catch(err => {
        console.error('Error deleting question:', err);
      });
  }

  resetForm() {
    this.newQuestionText = '';
    this.newOptions = [
      { id: 1, value: '' },
      { id: 2, value: '' },
      { id: 3, value: '' },
      { id: 4, value: '' }
    ];
    this.newCorrectAnswer = '';
  }

  goBack() {
    this.router.navigate(['/welcome']);
  }
}

