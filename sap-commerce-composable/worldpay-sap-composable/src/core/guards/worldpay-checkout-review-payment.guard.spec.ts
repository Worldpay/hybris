import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { GlobalMessageService, GlobalMessageType, PaymentDetails, QueryState, SemanticPathService } from '@spartacus/core';
import { of } from 'rxjs';
import { take } from 'rxjs/operators';
import { WorldpayCheckoutReviewPaymentGuard } from './worldpay-checkout-review-payment.guard';
import { WorldpayCheckoutPaymentService } from '../services';
import createSpy = jasmine.createSpy;

class MockSemanticPathService implements Partial<SemanticPathService> {
  get() {
    return '';
  };
}

class MockWorldpayCheckoutPaymentService implements Partial<WorldpayCheckoutPaymentService> {
  getCseTokenFromState() {
    return of('');
  }

  getPaymentDetailsState() {
    return of({} as QueryState<PaymentDetails>);
  }
}

class MockGlobalMessageService implements Partial<GlobalMessageService> {
  add = createSpy().and.callThrough();
}

describe('WorldpayCheckoutReviewPaymentGuard', () => {
  let guard: WorldpayCheckoutReviewPaymentGuard;
  let router: Router;
  let semanticPathService: SemanticPathService;
  let checkoutPaymentFacade: WorldpayCheckoutPaymentService;
  let globalMessageService: GlobalMessageService;
  let getPaymentDetailsStateSpy: jasmine.Spy;
  let getCseTokenFromStateSpy: jasmine.Spy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        Router,
        {
          provide: SemanticPathService,
          useClass: MockSemanticPathService
        },
        {
          provide: WorldpayCheckoutPaymentService,
          useClass: MockWorldpayCheckoutPaymentService
        },
        {
          provide: GlobalMessageService,
          useClass: MockGlobalMessageService
        }

      ]
    });
    guard = TestBed.inject(WorldpayCheckoutReviewPaymentGuard);
    router = TestBed.inject(Router);
    checkoutPaymentFacade = TestBed.inject(WorldpayCheckoutPaymentService);
    semanticPathService = TestBed.inject(SemanticPathService);
    globalMessageService = TestBed.inject(GlobalMessageService);
    getPaymentDetailsStateSpy = spyOn(checkoutPaymentFacade, 'getPaymentDetailsState');
    getCseTokenFromStateSpy = spyOn(checkoutPaymentFacade, 'getCseTokenFromState');
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return redirect to Payment Details page if payment method is NOT saved and cseToken is null', () => {
    getCseTokenFromStateSpy.and.returnValue(of(null));
    getPaymentDetailsStateSpy.and.returnValue(of({
      error: false,
      loading: false,
      data: {
        saved: false,
        accountHolderName: 'test',
        cardNumber: '4444333322221111',
        cardType: {
          code: 'visa',
          name: 'Visa'
        },
        expiryMonth: '01',
        expiryYear: '2022',
        cvn: '123',
        billingAddress: {
          firstName: 'test',
          lastName: 'test',
        }
      }
    }));

    ;

    checkoutPaymentFacade.getPaymentDetailsState().subscribe((val) => {
      expect(val.data.saved).toBeFalse();
    });

    checkoutPaymentFacade.getCseTokenFromState().subscribe((val) => {
      expect(val).toBeNull();
    });

    guard.canActivate().pipe(take(1)).subscribe((val) => {
      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'paymentForm.apmChanged' }, GlobalMessageType.MSG_TYPE_ERROR);

      expect(val).toEqual(router.parseUrl(semanticPathService.get('checkoutPaymentDetails')));
    });
  });

  it('should stay on Order Review Page if payment method is saved and cseToken is null', () => {
    getCseTokenFromStateSpy.and.returnValue(of(null));
    getPaymentDetailsStateSpy.and.returnValue(of({
      error: false,
      loading: false,
      data: {
        saved: true,
        accountHolderName: 'test',
        cardNumber: '4444333322221111',
        cardType: {
          code: 'visa',
          name: 'Visa'
        },
        expiryMonth: '01',
        expiryYear: '2022',
        cvn: '123',
        billingAddress: {
          firstName: 'test',
          lastName: 'test',
        }
      }
    }));

    checkoutPaymentFacade.getPaymentDetailsState().subscribe((val) => {
      expect(val.data.saved).toBeTrue();
    });

    checkoutPaymentFacade.getCseTokenFromState().subscribe((val) => {
      expect(val).toBeNull();
    });

    guard.canActivate().pipe(take(1)).subscribe((val) => {
      expect(val).toBeTrue();
      expect(globalMessageService.add).not.toHaveBeenCalled();
    });
  });

  it('should stay on Order Review Page if payment method is NOT saved and cseToken is not null', () => {
    getCseTokenFromStateSpy.and.returnValue(of('cseToken'));
    getPaymentDetailsStateSpy.and.returnValue(of({
      error: false,
      loading: false,
      data: {
        saved: false,
        accountHolderName: 'test',
        cardNumber: '4444333322221111',
        cardType: {
          code: 'visa',
          name: 'Visa'
        },
        expiryMonth: '01',
        expiryYear: '2022',
        cvn: '123',
        billingAddress: {
          firstName: 'test',
          lastName: 'test',
        }
      }
    }));

    checkoutPaymentFacade.getPaymentDetailsState().subscribe((val) => {
      expect(val.data.saved).toBeFalse();
    });

    checkoutPaymentFacade.getCseTokenFromState().subscribe((val) => {
      expect(val).toBe('cseToken');
    });

    guard.canActivate().pipe(take(1)).subscribe((val) => {
      expect(val).toBeTrue();
      expect(globalMessageService.add).not.toHaveBeenCalled();
    });
  });
});
