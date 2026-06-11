import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Params, Router } from '@angular/router';
import { GlobalMessageService, GlobalMessageType, SemanticPathService } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, of, throwError } from 'rxjs';
import { take } from 'rxjs/operators';
import { WorldpayOrderFacade } from '../facade';
import { WorldpayPlacedOrderStatus } from '../interfaces';
import { WorldpayOrderService } from '../services';
import { WorldpayCheckoutPaymentRedirectGuard } from './worldpay-checkout-payment-redirect.guard';
import createSpy = jasmine.createSpy;

const mockParams: Params = {
  pending: true,
  status: 'ERROR',
  orderKey: 'E2Y^MERCHANT2ECOM^00000018-1761730288154',
};
class MockGlobalMessageService implements Partial<GlobalMessageService> {
  add = createSpy().and.callThrough();
}

class MockWorldpayOrderService implements Partial<WorldpayOrderService> {
  getOrderDetails(): Observable<Order> {
    return of({} as Order);
  }

  placeBankTransferRedirectOrder(): Observable<boolean | never> {
    return of(true);
  }

  placeRedirectOrder(): Observable<boolean> {
    return of(true);
  }

  clearLoading() {

  }
}

class MockSemanticPathService implements Partial<SemanticPathService> {
  get() {
    return '';
  }
}

describe('WorldpayCheckoutPaymentRedirectGuard', () => {
  let guard: WorldpayCheckoutPaymentRedirectGuard;
  let router: Router;
  let worldpayOrderFacade: WorldpayOrderFacade;
  let semanticPathService: SemanticPathService;
  let globalMessageService: GlobalMessageService;
  let orderDetailsSpy: jasmine.Spy;
  let pathSpy: jasmine.Spy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        Router,
        {
          provide: SemanticPathService,
          useClass: MockSemanticPathService
        },
        {
          provide: WorldpayOrderFacade,
          useClass: MockWorldpayOrderService
        },
        {
          provide: GlobalMessageService,
          useClass: MockGlobalMessageService
        }
      ]
    });
    guard = TestBed.inject(WorldpayCheckoutPaymentRedirectGuard);
    router = TestBed.inject(Router);
    worldpayOrderFacade = TestBed.inject(WorldpayOrderFacade);
    semanticPathService = TestBed.inject(SemanticPathService);
    globalMessageService = TestBed.inject(GlobalMessageService);
    orderDetailsSpy = spyOn(worldpayOrderFacade, 'getOrderDetails');
    pathSpy = spyOn(semanticPathService, 'get');
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return redirect to order page if order is empty', () => {
    pathSpy.and.returnValue('orders');
    orderDetailsSpy.and.returnValue(of({} as Order));
    const activatedRouteWithoutParams = {
      queryParams: {},
    } as unknown as ActivatedRouteSnapshot;

    guard.canActivate(activatedRouteWithoutParams).pipe(take(1))
      .subscribe((val) => {
        expect(val).toEqual(router.parseUrl(semanticPathService.get('orders')));
      });
  });

  it('should return to order details page when order is placed', () => {
    orderDetailsSpy.and.returnValue(of({ code: '001' } as Order));
    const activatedRouteWithoutParams = {
      queryParams: {},
    } as unknown as ActivatedRouteSnapshot;

    guard.canActivate(activatedRouteWithoutParams).pipe(take(1))
      .subscribe((val) => {
        expect(val).toBeTrue();
      });
  });

  it('should AUTHORISED placed order and redirect to order confirmation page', () => {
    spyOn(worldpayOrderFacade, 'placeRedirectOrder').and.callThrough();
    orderDetailsSpy.and.returnValue(of({ code: '001' } as Order));
    const activatedRouteWithParams = {
      queryParams: {
        paymentStatus: 'AUTHORISED'
      },
    } as unknown as ActivatedRouteSnapshot;

    guard.canActivate(activatedRouteWithParams).pipe(take(1))
      .subscribe((val) => {
        expect(val).toBeTrue();
      });

    expect(worldpayOrderFacade.placeRedirectOrder).toHaveBeenCalled();
  });

  it('should placed order when pending status is true and redirect to order confirmation page', () => {
    spyOn(worldpayOrderFacade, 'placeRedirectOrder').and.callThrough();
    orderDetailsSpy.and.returnValue(of({ code: '001' } as Order));
    const activatedRouteWithParams = {
      queryParams: {
        pending: 'true'
      },
    } as unknown as ActivatedRouteSnapshot;

    guard.canActivate(activatedRouteWithParams).pipe(take(1))
      .subscribe((val) => {
        expect(val).toBeTrue();
      });

    expect(worldpayOrderFacade.placeRedirectOrder).toHaveBeenCalled();
  });

  it('should not placed order when pending status and paymentStatus is in WorldpayPlaceOrderStatus enum and redirect to payment method page', () => {
    pathSpy.and.returnValue('checkoutPaymentDetails');
    spyOn(worldpayOrderFacade, 'placeRedirectOrder').and.returnValue(throwError(() => 'error'));
    orderDetailsSpy.and.returnValue(of({ code: '001' } as Order));
    const activatedRouteWithParams = {
      queryParams: {
        pending: 'true',
        status: WorldpayPlacedOrderStatus.ERROR
      },
    } as unknown as ActivatedRouteSnapshot;

    guard.canActivate(activatedRouteWithParams).pipe(take(1))
      .subscribe((val) => {
        expect(val).toEqual(router.parseUrl(semanticPathService.get('checkoutPaymentDetails')));
      });

    expect(globalMessageService.add).toHaveBeenCalledWith(
      { key: 'checkoutReview.redirectPaymentRejected' },
      GlobalMessageType.MSG_TYPE_ERROR
    );

    expect(worldpayOrderFacade.placeRedirectOrder)
      .toHaveBeenCalledWith({ pending: 'true', status: WorldpayPlacedOrderStatus.ERROR });
  });

  it('should REFUSE placed order and redirect to Checkout Payment Details page and show error message', () => {
    pathSpy.and.returnValue('checkoutPaymentDetails');
    spyOn(worldpayOrderFacade, 'placeRedirectOrder')
      .and.returnValue(throwError(() => 'REFUSED'));
    orderDetailsSpy.and.returnValue(of({ code: '001' } as Order));
    const activatedRouteWithParams = {
      queryParams: {
        status: 'REFUSED'
      },
    } as unknown as ActivatedRouteSnapshot;

    guard.canActivate(activatedRouteWithParams).pipe(take(1))
      .subscribe((val) => {
        expect(val).toEqual(router.parseUrl(semanticPathService.get('checkoutPaymentDetails')));
      });

    expect(globalMessageService.add).toHaveBeenCalledWith(
      { key: 'checkoutReview.redirectPaymentRejected' },
      GlobalMessageType.MSG_TYPE_ERROR
    );

    expect(worldpayOrderFacade.placeRedirectOrder)
      .toHaveBeenCalledWith({ status: 'REFUSED', pending: false }); 
  });

  it('should redirect to Checkout Payment Details page and show error message if paymentStatus is REFUSED', () => {
    pathSpy.and.returnValue('checkoutPaymentDetails');
    spyOn(worldpayOrderFacade, 'placeRedirectOrder').and.returnValue(throwError(() => 'REFUSED'));
    const activatedRouteWithParams = {
      queryParams: {
        status: 'REFUSED'
      },
    } as unknown as ActivatedRouteSnapshot;

    guard.canActivate(activatedRouteWithParams).pipe(take(1))
      .subscribe((val) => {
        expect(val).toEqual(router.parseUrl(semanticPathService.get('checkoutPaymentDetails')));
      });

    expect(globalMessageService.add).toHaveBeenCalledWith(
      { key: 'checkoutReview.redirectPaymentRejected' },
      GlobalMessageType.MSG_TYPE_ERROR
    );

    expect(worldpayOrderFacade.placeRedirectOrder)
      .toHaveBeenCalledWith({ status: 'REFUSED', pending: false });  
  });

  describe('should call Place Bank Transfer Redirect Order when orderId queryParam is found', () => {
    let spyRedirectOrder:  jasmine.Spy<() => Observable<boolean>>;
    beforeEach(() => {
      spyRedirectOrder = spyOn(worldpayOrderFacade, 'placeBankTransferRedirectOrder');
    });

    it('should place bank transfer redirect order', () => {
      spyRedirectOrder.and.returnValue(of(true));
      orderDetailsSpy.and.returnValue(of({ code: '001' } as Order));
      const activatedRouteWithParams = {
        queryParams: {
          orderId: '0001'
        },
      } as unknown as ActivatedRouteSnapshot;

      guard.canActivate(activatedRouteWithParams).pipe(take(1))
        .subscribe((val) => {
          expect(val).toBeTrue();
        });

      expect(worldpayOrderFacade.placeBankTransferRedirectOrder).toHaveBeenCalledWith('0001');
    });

    it('should show error message and redirect to Checkout Payment Details page', () => {
      pathSpy.and.returnValue('checkoutPaymentDetails');
      spyRedirectOrder.and.returnValue(throwError(() =>'error'));
      orderDetailsSpy.and.returnValue(of({ code: '001' } as Order));
      const activatedRouteWithParams = {
        queryParams: {
          orderId: '0001'
        },
      } as unknown as ActivatedRouteSnapshot;

      guard.canActivate(activatedRouteWithParams).pipe(take(1))
        .subscribe((val) => {
          expect(val).toEqual(router.parseUrl(semanticPathService.get('checkoutPaymentDetails')));
        });

      expect(worldpayOrderFacade.placeBankTransferRedirectOrder).toHaveBeenCalledWith('0001');
    });

    it('should redirect to order confirmation page on timeout', () => {
      pathSpy.and.returnValue('orderConfirmation');
      spyOn(worldpayOrderFacade, 'placeRedirectOrder').and.returnValue(throwError(() => 'timeout'));
      const result$ = guard['placeRedirectOrder'](mockParams);
      result$.pipe(take(1)).subscribe((result) => {
        expect(result).toEqual(router.parseUrl(semanticPathService.get('orderConfirmation')));
      });
    });
  });

  it('should clear loading and redirect to checkout payment details page when error case is hit', () => {
    spyOn(worldpayOrderFacade, 'clearLoading');
    spyOn(worldpayOrderFacade, 'placeRedirectOrder').and.returnValue(throwError(() => 'error'));
    pathSpy.and.returnValue('checkoutPaymentDetails');
    const activatedRouteWithParams = {
      queryParams: {
        unknownParam: 'unknown'
      },
    } as unknown as ActivatedRouteSnapshot;

    guard.canActivate(activatedRouteWithParams).pipe(take(1))
      .subscribe((val) => {
        expect(val).toEqual(router.parseUrl(semanticPathService.get('checkoutPaymentDetails')));
      });

    expect(worldpayOrderFacade.placeRedirectOrder)
      .toHaveBeenCalledWith({ unknownParam: 'unknown', pending: false });
  });
});
