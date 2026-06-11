import { inject, Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { GlobalMessageService, GlobalMessageType, QueryState, SemanticPathService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { WorldpayCheckoutPaymentFacade } from '../facade';
import { PaymentMethod, WorldpayApmPaymentInfo } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class WorldpayCheckoutReviewPaymentGuard {
  protected router: Router = inject(Router);
  protected semanticPathService: SemanticPathService = inject(SemanticPathService);
  protected globalMessageService: GlobalMessageService = inject(GlobalMessageService);
  protected worldpayCheckoutPaymentFacade: WorldpayCheckoutPaymentFacade = inject(WorldpayCheckoutPaymentFacade);

  canActivate(): Observable<boolean | UrlTree> {
    return this.worldpayCheckoutPaymentFacade.getPaymentDetailsState().pipe(
      filter((state: QueryState<WorldpayApmPaymentInfo>): boolean => !state.loading && !state.error),
      map((state: QueryState<WorldpayApmPaymentInfo>): WorldpayApmPaymentInfo => state.data),
      switchMap((data: WorldpayApmPaymentInfo): Observable<boolean | UrlTree> => this.worldpayCheckoutPaymentFacade.getCseTokenFromState().pipe(
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
