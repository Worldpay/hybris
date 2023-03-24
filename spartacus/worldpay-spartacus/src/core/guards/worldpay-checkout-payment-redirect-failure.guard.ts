import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';
import { Observable, of } from 'rxjs';
import { GlobalMessageService, GlobalMessageType } from '@spartacus/core';

export type TypeOfReason =
  | 'cancel'
  | 'error'
  | 'failure';

@Injectable({
  providedIn: 'root'
})
export class WorldpayCheckoutPaymentRedirectFailureGuard implements CanActivate {

  constructor(
    protected globalMessageService: GlobalMessageService,) {
  }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    const params = route.queryParams;

    if (params && typeof params === 'object' && params.hasOwnProperty('reason')) {
      const key = this.getReasonMessageKey(params.reason);
      if (key) {
        this.globalMessageService.add(
          {
            key,
          },
          GlobalMessageType.MSG_TYPE_ERROR);
      }
    }

    return of(true);
  }

  protected getReasonMessageKey(reason: TypeOfReason): string {
    let key;
    switch (reason) {
    case 'cancel':
      key = 'checkoutReview.redirectPaymentCancelled';
      break;

    case 'error':
    case 'failure':
      key = 'checkoutReview.redirectPaymentFailed';
      break;

    default:
      break;
    }
    return key;
  }

}
