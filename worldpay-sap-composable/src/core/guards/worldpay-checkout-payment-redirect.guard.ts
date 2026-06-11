/* eslint-disable no-prototype-builtins */
import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Params, Router, UrlTree } from '@angular/router';
import { GlobalMessageService, GlobalMessageType, SemanticPathService } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, of } from 'rxjs';
import { catchError, map, timeout } from 'rxjs/operators';
import { WorldpayOrderFacade } from '../facade';

@Injectable({
  providedIn: 'root',
})
export class WorldpayCheckoutPaymentRedirectGuard {
  protected router: Router = inject(Router);
  protected worldpayOrderFacade: WorldpayOrderFacade = inject(WorldpayOrderFacade);
  protected semanticPathService: SemanticPathService = inject(SemanticPathService);
  protected globalMessageService: GlobalMessageService = inject(GlobalMessageService);

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

      case params.hasOwnProperty('orderId'): {
        // @ts-ignore: TS4111
        return this.placeBankTransferRedirectOrder(params.orderId);
      }

      default:
        return this.placeRedirectOrder({
          ...params,
          // @ts-ignore: TS4111
          pending: route.queryParams.pending ?? false
        });
    }
  }

  /**
   * Performs the redirect place order API call.
   * @returns {Observable<boolean | UrlTree>} An observable that emits a boolean or UrlTree indicating the result of the redirect place order API call.
   * @protected
   * @since 4.3.6
   */
  protected placeRedirectOrder(params: Params): Observable<boolean | UrlTree> {
    return this.worldpayOrderFacade.placeRedirectOrder(params).pipe(
      catchError((): Observable<UrlTree> => this.redirectToPaymentRejected()),
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
    return this.worldpayOrderFacade.placeBankTransferRedirectOrder(orderId)
      .pipe(
        timeout(4000),
        catchError((): Observable<UrlTree> => of(this.router.parseUrl(this.semanticPathService.get('checkoutPaymentDetails')))),
      );
  }

  /**
   * Lookup existing order, or redirect to order history page.
   * @returns {Observable<boolean | UrlTree>} An observable that emits a boolean or UrlTree indicating the result of the lookup order operation.
   * @protected
   * @since 4.3.6
   */
  protected lookUpOrder(): Observable<boolean | UrlTree> {
    return this.worldpayOrderFacade.getOrderDetails().pipe(
      map((orderDetails: Order): boolean | UrlTree => {
        if (orderDetails && Object.keys(orderDetails).length !== 0) {
          return true;
        } else {
          return this.router.parseUrl(this.semanticPathService.get('orders'));
        }
      })
    );
  }

  protected redirectToPaymentRejected(): Observable<UrlTree> {
    this.worldpayOrderFacade.clearLoading();
    this.globalMessageService.add(
      { key: 'checkoutReview.redirectPaymentRejected' },
      GlobalMessageType.MSG_TYPE_ERROR
    );
    return of(this.router.parseUrl(this.semanticPathService.get('checkoutPaymentDetails')));
  }
}
