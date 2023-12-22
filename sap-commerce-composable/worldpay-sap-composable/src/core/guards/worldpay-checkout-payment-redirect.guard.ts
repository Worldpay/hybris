/* eslint-disable no-prototype-builtins */
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Params, Router, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { GlobalMessageService, GlobalMessageType, SemanticPathService } from '@spartacus/core';
import { catchError, map, timeout } from 'rxjs/operators';
import { Order } from '@spartacus/order/root';
import { WorldpayOrderService } from '../services';
import { WorldpayPlacedOrderStatus } from '../interfaces';

@Injectable({
  providedIn: 'root',
})
export class WorldpayCheckoutPaymentRedirectGuard implements CanActivate {

  constructor(
    protected router: Router,
    protected worldpayOrderService: WorldpayOrderService,
    protected semanticPathService: SemanticPathService,
    protected globalMessageService: GlobalMessageService,
  ) {
  }

  /**
   * Performs the redirect place order API call
   * @since 4.3.6
   * @param route ActivatedRouteSnapshot
   */
  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const params: Params = route.queryParams;
    switch (true) {
    case params == null || typeof params !== 'object' || Object.keys(params).length === 0:
      return this.lookUpOrder();

    case params.hasOwnProperty('pending'): {
      return this.redirectToOrderConfirmation(!(params.paymentStatus in WorldpayPlacedOrderStatus));
    }

    case params.hasOwnProperty('paymentStatus'): {
      return this.redirectToOrderConfirmation(params.paymentStatus === 'AUTHORISED');
    }

    case params.hasOwnProperty('orderId'): {
      return this.placeBankTransferRedirectOrder(params.orderId);
    }

    default:
      this.worldpayOrderService.clearLoading();
      return of(this.router.parseUrl(this.semanticPathService.get('checkoutPaymentDetails')));
    }
  }

  /**
   * Performs the redirect place order API call
   * @since 4.3.6
   */
  protected placeRedirectOrder(): Observable<boolean | UrlTree> {
    return this.worldpayOrderService.placeRedirectOrder()
      .pipe(
        timeout(4000),
        catchError(() => of(this.router.parseUrl(this.semanticPathService.get('orderConfirmation')))),
      );
  }

  /**
   * Performs the redirect place order API call
   * @since 4.3.6
   */
  protected placeBankTransferRedirectOrder(orderId: string): Observable<boolean | UrlTree> {
    return this.worldpayOrderService.placeBankTransferRedirectOrder(orderId)
      .pipe(
        timeout(4000),
        catchError(() => of(this.router.parseUrl(this.semanticPathService.get('checkoutPaymentDetails')))),
      );
  }

  /**
   * Lookup existing order, or redirect to order history page
   * @since 4.3.6
   */
  protected lookUpOrder(): Observable<boolean | UrlTree> {
    return this.worldpayOrderService.getOrderDetails().pipe(
      map((orderDetails: Order): boolean | UrlTree => {
        if (orderDetails && Object.keys(orderDetails).length !== 0) {
          return true;
        } else {
          return this.router.parseUrl(this.semanticPathService.get('orders'));
        }
      })
    );
  }

  /**
   * Redirects to order confirmation page
   * @since 6.4.0
   * @param validator boolean
   */
  protected redirectToOrderConfirmation(validator: boolean): Observable<boolean | UrlTree> {
    if (validator) {
      return this.placeRedirectOrder();
    }

    this.worldpayOrderService.clearLoading();
    this.globalMessageService.add({ key: 'checkoutReview.redirectPaymentRejected' }, GlobalMessageType.MSG_TYPE_ERROR);
    return of(this.router.parseUrl(this.semanticPathService.get('checkoutPaymentDetails')));
  }
}
