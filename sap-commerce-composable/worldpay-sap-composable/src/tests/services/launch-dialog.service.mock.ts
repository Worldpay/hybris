import { ElementRef, ViewContainerRef } from '@angular/core';
import { LAUNCH_CALLER, LaunchDialogService } from '@spartacus/storefront';
import { EMPTY, Observable, of } from 'rxjs';
import createSpy = jasmine.createSpy;

export class MockLaunchDialogService implements Partial<LaunchDialogService> {
  launch = createSpy();
  clear = createSpy();

  get dialogClose(): Observable<any | undefined> {
    return of(EMPTY);
  }

  openDialog(caller: LAUNCH_CALLER | string, openElement?: ElementRef, vcr?: ViewContainerRef, data?: any): Observable<any> | undefined {
    return of({});
  }

  openDialogAndSubscribe() {
    return EMPTY;
  }

  closeDialog(reason: any) {
  }
}