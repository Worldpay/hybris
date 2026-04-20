import { GlobalMessageEntities, GlobalMessageService } from '@spartacus/core';
import { Observable, of } from 'rxjs';

export class MockGlobalMessageService implements Partial<GlobalMessageService> {
  add = jasmine.createSpy('add');
  remove = jasmine.createSpy('remove');

  get(): Observable<GlobalMessageEntities> {
    return of({});
  }
}

export const globalMessageServiceSpy: jasmine.SpyObj<GlobalMessageService> = jasmine.createSpyObj('GlobalMessageService', ['add']);
