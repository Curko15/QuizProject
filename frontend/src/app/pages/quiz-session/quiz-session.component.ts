import { Component, OnInit, OnDestroy } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { AuthService } from '../../services/auth.service';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import {NgClass, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-quiz-session',
  templateUrl: './quiz-session.component.html',
  styleUrls: ['./quiz-session.component.css'],
  imports: [NgForOf, NgIf, NgClass]
})
export class QuizSessionComponent implements OnInit, OnDestroy {

  accessCode: string = '';
  students: string[] = [];
  leaderboard: { student: string, score: number }[] = [];
  currentQuestion: any = null;
  showQuestion: boolean = false;
  userRole: string | null = null;
  waiting: boolean = true;
  quizEnded: boolean = false;
  hasAnswered: boolean = false;
  quizAborted: boolean = false;
  answeredStudents: string[] = [];

  socketClient: any = null;

  constructor(private route: ActivatedRoute, private authService: AuthService, private router: Router) {
    this.accessCode = this.route.snapshot.paramMap.get('accessCode') || '';
    this.userRole = this.authService.getUserRole();
  }

  ngOnInit(): void {
    const ws = new SockJS('http://localhost:8080/ws');
    this.socketClient = Stomp.over(ws);

    this.socketClient.connect(
        { Authorization: `Bearer ${localStorage.getItem('auth_token')}` },
        (frame: any) => {


          this.socketClient.subscribe(`/topic/session/${this.accessCode}`, (msg: any) => {
            const data = JSON.parse(msg.body);

            if (data.type === 'JOIN') {
              this.students.push(data.student);
            } else if (data.type === 'LEAVE') {
              this.students = this.students.filter(s => s !== data.student);
            } else if (data.type === 'QUESTION') {

              this.currentQuestion = {
                text: data.questionText,
                options: data.options
              };
              this.answeredStudents = [];
              this.waiting = false;
              this.showQuestion = true;
              this.hasAnswered = false;
            } else if (data.type === 'LEADERBOARD') {
              this.leaderboard = data.entries.sort((a: any, b: any) => b.score - a.score);
            } else if (data.type === 'QUIZ_ENDED') {
              this.quizEnded = true;
              this.showQuestion = false;
              this.waiting = false;
            } else if (data.type === 'ABORTED') {
              this.quizAborted = true;
            } else if (data.type === 'ANSWERED') {
              if (!this.answeredStudents.includes(data.student)) {
                this.answeredStudents.push(data.student);
              }
            }
          });
        },
        (error: any) => {
          console.error('WebSocket connection error:', error);
        }
    );
  }

  goBack(): void {
    this.router.navigate(['/welcome']);
  }

  endQuiz() {
    if (!this.socketClient || !this.socketClient.connected) return;
    const endMessage = {
      accessCode: this.accessCode
    };
    this.socketClient.send('/app/end', {}, JSON.stringify(endMessage));
    this.router.navigate(['/welcome']);
  }

  startQuiz() {
    if (!this.socketClient || !this.socketClient.connected) return;
    const startMessage = {
      type: 'START',
      accessCode: this.accessCode
    };

    this.socketClient.send(`/app/start`, {}, JSON.stringify(startMessage));
  }

  submitAnswer(option: string) {

    if (this.hasAnswered) return;
    const answerMessage = {
      type: 'ANSWER',
      accessCode: this.accessCode,
      student: this.authService.getLogin(),
      answer: option
    };
    this.hasAnswered = true;
    this.socketClient.send(`/app/answer`, {}, JSON.stringify(answerMessage));
  }

  leaveSession() {
    const message = {
      type: 'LEAVE',
      student: this.authService.getLogin(),
      accessCode: this.accessCode
    };

    this.socketClient.send(`/app/leave`, {}, JSON.stringify(message));
    this.socketClient.disconnect(() => {
      this.router.navigate(['/welcome']);
    });
  }
  nextQuestion() {
    const msg = {
      type: 'NEXT',
      accessCode: this.accessCode
    };
    this.answeredStudents = [];
    this.socketClient.send('/app/next', {}, JSON.stringify(msg));
  }

  ngOnDestroy(): void {
    if (this.socketClient && this.socketClient.connected) {
      this.socketClient.disconnect(() => {

      });
    }
  }
}


