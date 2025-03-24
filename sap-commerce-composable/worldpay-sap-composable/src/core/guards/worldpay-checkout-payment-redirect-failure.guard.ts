import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Params } from '@angular/router';
import { GlobalMessageService, GlobalMessageType } from '@spartacus/core';
import { Observable, of } from 'rxjs';

export type TypeOfReason =
  | 'cancel'
  | 'error'
  | 'failure';

@Injectable({
  providedIn: 'root'
})
export class WorldpayCheckoutPaymentRedirectFailureGuard {

  /**
   * Constructor
   * @since 4.3.6
   * @param globalMessageService GlobalMessageService
   */
  constructor(
    protected globalMessageService: GlobalMessageService,) {
  }

  /**
   * Method used to activate guard
   * @since 4.3.6
   * @param route ActivatedRouteSnapshot
   */
  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    const params: Params = route.queryParams;

    // eslint-disable-next-line no-prototype-builtins
    if (params && typeof params === 'object' && params.hasOwnProperty('reason')) {
      const key: string = this.getReasonMessageKey(params.reason);
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
    switch (reason) {
    case 'cancel':
      return 'checkoutReview.redirectPaymentCancelled';

    case 'error':
    case 'failure':
      return 'checkoutReview.redirectPaymentFailed';

    default:
      return null;
    }
  }

}
