import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { WorldpayApmService } from '../services/worldpay-apm/worldpay-apm.service';
import { ActiveCartService, GlobalMessageService, GlobalMessageType, SemanticPathService, UserIdService } from '@spartacus/core';
import { catchError, map, switchMap, timeout } from 'rxjs/operators';
import { getUserIdCartId } from '../utils/get-user-cart-id';
import { CheckoutService } from '@spartacus/checkout/core';

@Injectable({
  providedIn: 'root',
})
export class WorldpayCheckoutPaymentRedirectGuard implements CanActivate {

  constructor(
    protected router: Router,
    protected worldpayApmService: WorldpayApmService,
    protected checkoutService: CheckoutService,
    protected semanticPathService: SemanticPathService,
    protected globalMessageService: GlobalMessageService,
    protected activeCartService: ActiveCartService,
    protected userIdService: UserIdService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const params = route.queryParams;

    if (params == null || typeof params !== 'object' || Object.keys(params).length === 0) {
      return this.lookUpOrder();
    } else if (params.hasOwnProperty('paymentStatus')) {
      const { paymentStatus } = params;

      if (paymentStatus === 'AUTHORISED') {
        return this.placeRedirectOrder();
      } else {
        this.globalMessageService.add(
          {
            key: 'checkoutReview.redirectPaymentRejected',
          },
          GlobalMessageType.MSG_TYPE_ERROR);
      }
    }

    return of(this.router.parseUrl(this.semanticPathService.get('checkoutPaymentDetails')));
  }

  /**
   * Performs the redirect place order API call
   */
  protected placeRedirectOrder(): Observable<boolean | UrlTree> {
    return getUserIdCartId(this.userIdService, this.activeCartService)
      .pipe(
        switchMap(({
          cartId,
          userId
        }) => this.worldpayApmService.placeRedirectOrder(cartId, userId)),
        timeout(4000),
        catchError(() => of(this.router.parseUrl(this.semanticPathService.get('orderConfirmation')))),
      );
  }

  /**
   * Lookup existing order, or redirect to to order history page
   */
  protected lookUpOrder(): Observable<boolean | UrlTree> {
    return this.checkoutService.getOrderDetails().pipe(
      map((orderDetails) => {
        if (orderDetails && Object.keys(orderDetails).length !== 0) {
          return true;
        } else {
          return this.router.parseUrl(this.semanticPathService.get('orders'));
        }
      })
    );
  }
}
