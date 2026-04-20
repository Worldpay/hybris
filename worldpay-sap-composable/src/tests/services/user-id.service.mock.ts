import { UserIdService } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { mockUserId } from '../fake-data';

export class MockUserIdService implements Partial<UserIdService> {
  takeUserId(): Observable<string> {
    return of(mockUserId);
  }

  getUserId(): Observable<string> {
    return of(mockUserId);
  }
}