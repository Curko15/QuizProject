import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import {authGuard} from './guards/auth.guard';
import {CreateQuizComponent} from './pages/create-quiz/create-quiz.component';
import {AddQuestionComponent} from './pages/add-question/add-question.component';
import {QuizSessionComponent} from './pages/quiz-session/quiz-session.component';
import {WaitingRoomComponent} from './pages/waiting-room/waiting-room.component';
import {EditQuestionsComponent} from './pages/edit-questions/edit-questions.component';
import {QuizHistoryComponent} from './pages/quiz-history/quiz-history.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'create-quiz', component: CreateQuizComponent },
  { path: 'add-question/:quizId', component: AddQuestionComponent },
  { path: 'quiz-session/:accessCode', component: QuizSessionComponent },
  { path: 'waiting-room/:accessCode', component: WaitingRoomComponent },
  { path: 'edit-questions/:quizId', component: EditQuestionsComponent },
  { path: 'quiz-history', component: QuizHistoryComponent, canActivate: [authGuard] },

  {
    path: 'welcome',
    component: WelcomeComponent,
    canActivate: [authGuard]
  },
];
