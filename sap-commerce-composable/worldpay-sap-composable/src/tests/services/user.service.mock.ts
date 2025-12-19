import { Title } from '@spartacus/core';
import { EMPTY, Observable } from 'rxjs';

export class MockUserService {
  getTitles(): Observable<Title[]> {
    return EMPTY;
  }

  loadTitles(): void {
  }
}