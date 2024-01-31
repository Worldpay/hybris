import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { GlobalMessageService, GlobalMessageType, QueryState, SemanticPathService } from '@spartacus/core';
import { WorldpayCheckoutPaymentService } from '../services';
import { PaymentDetails } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root'
})
export class WorldpayCheckoutReviewPaymentGuard implements CanActivate {

  constructor(
    protected router: Router,
    protected semanticPathService: SemanticPathService,
    protected globalMessageService: GlobalMessageService,
    protected checkoutPaymentFacade: WorldpayCheckoutPaymentService,
  ) {
  }

  canActivate(): Observable<boolean | UrlTree> {
    return this.checkoutPaymentFacade.getPaymentDetailsState().pipe(
      filter((state: QueryState<PaymentDetails>) => !state.loading && !state.error),
      map((state: QueryState<PaymentDetails>) => state.data),
      switchMap((data: PaymentDetails) => this.checkoutPaymentFacade.getCseTokenFromState().pipe(
        map((cseToken: string): boolean | UrlTree => {
          // eslint-disable-next-line no-prototype-builtins
          if (data.hasOwnProperty('cardType') && !data.saved && (!cseToken || cseToken?.length === 0)) {
            this.globalMessageService.add({ key: 'paymentForm.apmChanged' }, GlobalMessageType.MSG_TYPE_ERROR);
            return this.router.parseUrl(this.semanticPathService.get('checkoutPaymentDetails'));
          }
          return true;
        })
      ))
    );
  }
}
