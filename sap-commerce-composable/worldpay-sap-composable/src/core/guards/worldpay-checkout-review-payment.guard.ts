import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { GlobalMessageService, GlobalMessageType, QueryState, SemanticPathService } from '@spartacus/core';
import { WorldpayCheckoutPaymentService } from '../services';
import { PaymentMethod, WorldpayApmPaymentInfo } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class WorldpayCheckoutReviewPaymentGuard  {

  constructor(
    protected router: Router,
    protected semanticPathService: SemanticPathService,
    protected globalMessageService: GlobalMessageService,
    protected worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService,
  ) {
  }

  canActivate(): Observable<boolean | UrlTree> {
    return this.worldpayCheckoutPaymentService.getPaymentDetailsState().pipe(
      filter((state: QueryState<WorldpayApmPaymentInfo>) => !state.loading && !state.error),
      map((state: QueryState<WorldpayApmPaymentInfo>) => state.data),
      switchMap((data: WorldpayApmPaymentInfo) => this.worldpayCheckoutPaymentService.getCseTokenFromState().pipe(
        map((cseToken: string): boolean | UrlTree => {
          if (
            (
              // eslint-disable-next-line no-prototype-builtins
              data.hasOwnProperty('cardType') &&
              !data.saved &&
              (!cseToken || cseToken?.length === 0)
            ) ||
            (data.apmCode === PaymentMethod.ACH && data?.achPaymentForm === null)
          ) {
            this.globalMessageService.add({ key: 'paymentForm.apmChanged' }, GlobalMessageType.MSG_TYPE_ERROR);
            return this.router.parseUrl(this.semanticPathService.get('checkoutPaymentDetails'));
          }
          return true;
        })
      ))
    );
  }
}
