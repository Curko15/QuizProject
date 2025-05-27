import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import {AxiosService} from '../../axios.service';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';


interface Question {
  id?: number;
  questionText: string;
  options: string[];
  correctAnswer: string;
}

@Component({
  selector: 'app-add-question',
  templateUrl: './add-question.component.html',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  styleUrls: ['./add-question.component.css']
})
export class AddQuestionComponent {

  quizId: string = '';
  questionText: string = '';
  options = [
    { id: 1, value: '' },
    { id: 2, value: '' },
    { id: 3, value: '' },
    { id: 4, value: '' }
  ];
  correctAnswer: string = '';
  questions: Question[] = [];
  showValidation: boolean = false;

  constructor(private router: Router, private route: ActivatedRoute, private axiosService: AxiosService) {
    this.quizId = this.route.snapshot.paramMap.get('quizId') || '';
  }



  addQuestion() {
    const parsedOptions = this.options.map(option => option.value.trim());

    if (!this.questionText.trim() || parsedOptions.includes('') || !this.correctAnswer.trim()) {
      this.showValidation = true;
      return;
    }

    this.showValidation = false;

    const newQuestion: Question = {
      questionText: this.questionText.trim(),
      options: parsedOptions,
      correctAnswer: this.correctAnswer.trim()
    };

    this.questions.push(newQuestion);

    // Resetiraj formu
    this.questionText = '';
    this.options = [
      { id: 1, value: '' },
      { id: 2, value: '' },
      { id: 3, value: '' },
      { id: 4, value: '' }
    ];
    this.correctAnswer = '';
  }

  removeQuestion(index: number) {
    this.questions.splice(index, 1);
  }

  saveQuestions() {
    const payload = this.questions.map(q => ({
      quizId: this.quizId,
      questionText: q.questionText,
      options: q.options,
      correctAnswer: q.correctAnswer
    }));

    payload.forEach(question => {
      this.axiosService.request('POST', '/questions', question)
        .then(() => {

        })
        .catch(err => {
          console.error('Greška prilikom dodavanja pitanja:', err);
        });
    });


    this.router.navigate(['/welcome']);
  }

  goBack() {
    this.router.navigate(['/welcome']);
  }
}

