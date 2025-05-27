import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-waiting-room',
  templateUrl: './waiting-room.component.html',
  styleUrls: ['./waiting-room.component.css']
})
export class WaitingRoomComponent implements OnInit {

  accessCode: string = '';
  socketClient: any;
  message: string = 'Čekanje da profesor pokrene kviz...';

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService) {
    this.accessCode = this.route.snapshot.paramMap.get('accessCode') || '';
  }

  ngOnInit(): void {
    const ws = new SockJS('http://localhost:8080/ws');
    this.socketClient = Stomp.over(ws);

    this.socketClient.connect(
      { Authorization: `Bearer ${localStorage.getItem('auth_token')}` },
      () => {
        this.socketClient.subscribe(`/topic/session/${this.accessCode}`, (msg: any) => {
          const data = JSON.parse(msg.body);

          if (data.type === 'QUESTION') {
            this.socketClient.disconnect(() => {
              this.router.navigate(['/quiz-session', this.accessCode]);
            });
          }
        });
      },
      (err: any) => {
        console.error('WebSocket error:', err);
      }
    );
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
}
