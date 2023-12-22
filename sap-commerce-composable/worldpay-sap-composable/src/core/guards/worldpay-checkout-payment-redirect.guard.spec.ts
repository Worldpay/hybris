import { TestBed } from '@angular/core/testing';
import { WorldpayOrderService } from '../services';
import { GlobalMessageService, GlobalMessageType, SemanticPathService } from '@spartacus/core';
import { WorldpayCheckoutPaymentRedirectGuard } from './worldpay-checkout-payment-redirect.guard';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { take } from 'rxjs/operators';
import { Order } from '@spartacus/order/root';
import { Observable, of, throwError } from 'rxjs';
import { WorldpayPlacedOrderStatus } from '../interfaces';
import createSpy = jasmine.createSpy;

class MockGlobalMessageService implements Partial<GlobalMessageService> {
  add = createSpy().and.callThrough();
};

class MockWorldpayOrderService implements Partial<WorldpayOrderService> {
  lookUpOrder = createSpy().and.callThrough();

  getOrderDetails(): Observable<Order> {
    return of({} as Order);
  };

  placeBankTransferRedirectOrder(): Observable<boolean | never> {
    return of(true);
  };

  placeRedirectOrder(): Observable<boolean> {
    return of(true);
  };

  clearLoading() {
    
  }
}

class MockSemanticPathService implements Partial<SemanticPathService> {
  get() {
    return '';
  };
}

describe('WorldpayCheckoutPaymentRedirectGuard', () => {
  let guard: WorldpayCheckoutPaymentRedirectGuard;
  let router: Router;
  let worldpayOrderService: WorldpayOrderService;
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
          provide: WorldpayOrderService,
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
    worldpayOrderService = TestBed.inject(WorldpayOrderService);
    semanticPathService = TestBed.inject(SemanticPathService);
    globalMessageService = TestBed.inject(GlobalMessageService);
    orderDetailsSpy = spyOn(worldpayOrderService, 'getOrderDetails');
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
    spyOn(worldpayOrderService, 'placeRedirectOrder').and.callThrough();
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

    expect(worldpayOrderService.placeRedirectOrder).toHaveBeenCalled();
  });

  it('should placed order when pending status is true and redirect to order confirmation page', () => {
    spyOn(worldpayOrderService, 'placeRedirectOrder').and.callThrough();
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

    expect(worldpayOrderService.placeRedirectOrder).toHaveBeenCalled();
  });

  it('should not placed order when pending status and paymentStatus is in WorldpayPlaceOrderStatus enum and redirect to payment method page', () => {
    spyOn(worldpayOrderService, 'placeRedirectOrder').and.callThrough();
    orderDetailsSpy.and.returnValue(of({ code: '001' } as Order));
    const activatedRouteWithParams = {
      queryParams: {
        pending: 'true',
        paymentStatus: WorldpayPlacedOrderStatus.ERROR
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

    expect(worldpayOrderService.placeRedirectOrder).not.toHaveBeenCalled();
  });

  it('should REFUSE placed order and redirect to Checkout Payment Details page and show error message', () => {
    pathSpy.and.returnValue('checkoutPaymentDetails');
    spyOn(worldpayOrderService, 'placeRedirectOrder').and.callThrough();
    orderDetailsSpy.and.returnValue(of({ code: '001' } as Order));
    const activatedRouteWithParams = {
      queryParams: {
        paymentStatus: 'REFUSED'
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

    expect(worldpayOrderService.placeRedirectOrder).not.toHaveBeenCalled();
  });

  describe('should call Place Bank Transfer Redirect Order when orderId queryParam is found', () => {
    let spyRedirectOrder;
    beforeEach(() => {
      spyRedirectOrder = spyOn(worldpayOrderService, 'placeBankTransferRedirectOrder');
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

      expect(worldpayOrderService.placeBankTransferRedirectOrder).toHaveBeenCalledWith('0001');
    });

    it('should show error message and redirect to Checkoput Payment Details page', () => {
      spyRedirectOrder.and.returnValue(throwError('error'));
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

      expect(worldpayOrderService.placeBankTransferRedirectOrder).toHaveBeenCalledWith('0001');
    });
  });
});
