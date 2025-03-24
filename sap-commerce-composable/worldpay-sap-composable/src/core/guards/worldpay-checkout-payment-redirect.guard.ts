/* eslint-disable no-prototype-builtins */
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Params, Router, UrlTree } from '@angular/router';
import { GlobalMessageService, GlobalMessageType, SemanticPathService } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, of } from 'rxjs';
import { catchError, map, timeout } from 'rxjs/operators';
import { WorldpayPlacedOrderStatus } from '../interfaces';
import { WorldpayOrderService } from '../services';

@Injectable({
  providedIn: 'root',
})
export class WorldpayCheckoutPaymentRedirectGuard {

  /**
   * Constructor for WorldpayCheckoutPaymentRedirectGuard.
   * @param {Router} router - The Angular Router service.
   * @param {WorldpayOrderService} worldpayOrderService - The service for handling Worldpay orders.
   * @param {SemanticPathService} semanticPathService - The service for handling semantic paths.
   * @param {GlobalMessageService} globalMessageService - The service for displaying global messages.
   */
  constructor(
    protected router: Router,
    protected worldpayOrderService: WorldpayOrderService,
    protected semanticPathService: SemanticPathService,
    protected globalMessageService: GlobalMessageService,
  ) {
  }

  /**
   * Determines if the route can be activated based on the query parameters.
   * @param {ActivatedRouteSnapshot} route - The snapshot of the current route.
   * @returns {Observable<boolean | UrlTree>} An observable that emits a boolean or UrlTree indicating whether the route can be activated.
   * @since 4.3.6
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
   * Performs the redirect place order API call.
   * @returns {Observable<boolean | UrlTree>} An observable that emits a boolean or UrlTree indicating the result of the redirect place order API call.
   * @protected
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
   * Performs the bank transfer redirect order API call.
   * @param {string} orderId - The ID of the order to be redirected.
   * @returns {Observable<boolean | UrlTree>} An observable that emits a boolean or UrlTree indicating the result of the bank transfer redirect order API call.
   * @protected
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
   * Lookup existing order, or redirect to order history page.
   * @returns {Observable<boolean | UrlTree>} An observable that emits a boolean or UrlTree indicating the result of the lookup order operation.
   * @protected
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
   * Redirects to order confirmation page based on the validation result.
   * @param {boolean} validator - A boolean indicating whether the validation passed.
   * @returns {Observable<boolean | UrlTree>} An observable that emits a boolean or UrlTree indicating the result of the redirection.
   * @protected
   * @since 6.4.0
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
