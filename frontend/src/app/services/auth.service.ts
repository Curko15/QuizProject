import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  private getPayload(): any | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }

  getFullName(): string | null {
    const payload = this.getPayload();
    if (payload?.firstName && payload?.lastName) {
      return `${payload.firstName} ${payload.lastName}`;
    }
    return null;
  }

  getLogin(): string | null {
    const payload = this.getPayload();
    if (payload?.login) {
      return `${payload.login}`;
    }
    return null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    window.location.href = '/login'; // hard reload
  }

  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.Role || null;
    } catch (e) {
      return null;
    }
  }
}
