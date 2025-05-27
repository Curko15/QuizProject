import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { Router } from '@angular/router';
import {AuthService} from '../services/auth.service';


export const roleGuard = (expectedRole: string): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    const role = auth.getUserRole();
    if (auth.isLoggedIn() && role === expectedRole) {
      return true;
    } else {
      router.navigate(['/login']);
      return false;
    }
  };
};
