import { TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CommandService, EventService, GlobalMessageService, OCC_USER_ID_ANONYMOUS, QueryService, UserIdService, WindowRef } from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { of, throwError } from 'rxjs';
import { WorldpayApplepayAdapter } from '../../connectors/worldpay-applepay/worldpay-applepay.adapter';
import { WorldpayApplepayConnector } from '../../connectors/worldpay-applepay/worldpay-applepay.connector';
import { ApplePayAuthorizePaymentEvent, ApplePayMerchantSessionEvent, RequestApplePayPaymentRequestEvent } from '../../events/applepay.events';
import { ApplePayPaymentRequest } from '../../interfaces';

import { WorldpayApplepayService } from './worldpay-applepay.service';

const mockAuthorization = {
  status: 'SUCCESS',
  orderData: { code: '100' }
};
const mockApplePaySession = {
  STATUS_SUCCESS: 'SUCCESS',
  STATUS_FAILURE: 'FAILURE',
  completePayment: jasmine.createSpy()
};

class MockWorldpayApplepayAdapter implements Partial<WorldpayApplepayAdapter> {

}

class MockOrderFacade implements Partial<OrderFacade> {
  setPlacedOrder() {
  }
}

class MockWorldpayApplepayConnector implements Partial<WorldpayApplepayConnector> {
  requestApplePayPaymentRequest() {
    return of({
      countryCode: 'US',
      currencyCode: 'USD',
    });
  }

  validateApplePayMerchant() {
    return of({ merchantIdentifier: 'testMerchantIdentifier' });
  }

  authorizeApplePayPayment() {
    return of({ order: { code: '0001' } });
  }
}

describe('WorldpayApplepayService', () => {
  let service: WorldpayApplepayService;
  let orderFacade: OrderFacade;
  let activeCartService: ActiveCartFacade;
  let userIdService: UserIdService;
  let worldpayApplepayConnector: WorldpayApplepayConnector;
  let eventService: EventService;
  let windowRef: WindowRef;

  const userId = 'testUserId';
  const cartId = 'testCartId';

  class ActiveCartServiceStub {
    cartId = cartId;

    takeActiveCartId() {
      return of(this.cartId);
    }

    isGuestCart() {
      return of(false);
    }
  }

  class UserIdServiceStub implements Partial<UserIdService> {
    takeUserId() {
      return of(userId);
    };
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
      providers: [
        WorldpayApplepayService,
        GlobalMessageService,
        CommandService,
        QueryService,
        EventService,
        {
          provide: OrderFacade,
          useClass: MockOrderFacade
        },
        {
          provide: ActiveCartFacade,
          useClass: ActiveCartServiceStub
        },
        {
          provide: UserIdService,
          useClass: UserIdServiceStub
        },
        {
          provide: WorldpayApplepayConnector,
          useClass: MockWorldpayApplepayConnector
        },
        {
          provide: WorldpayApplepayAdapter,
          useClass: MockWorldpayApplepayAdapter
        }
      ]
    });
    service = TestBed.inject(WorldpayApplepayService);
    orderFacade = TestBed.inject(OrderFacade);
    activeCartService = TestBed.inject(ActiveCartFacade);
    worldpayApplepayConnector = TestBed.inject(WorldpayApplepayConnector);
    userIdService = TestBed.inject(UserIdService);
    eventService = TestBed.inject(EventService);
    windowRef = TestBed.inject(WindowRef);

    service.nativeWindow.ApplePaySession = {
      canMakePayments: () => true
    };
    spyOn(eventService, 'dispatch').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not enable apple pay', () => {
    service.nativeWindow.ApplePaySession = null;
    expect(service.applePayButtonAvailable()).toBeFalse();
  });

  it('should return null if ApplePaySession is not set', () => {
    service.AppleSession = null;
    const applePaySession = service.getApplePaySessionFromWindow();
    expect(applePaySession).toBeNull();
  });

  describe('apple pay is be enabled', () => {
    it('should requestApplePayPaymentRequest', () => {
      let paymentRequest = null;
      worldpayApplepayConnector.requestApplePayPaymentRequest('userId', 'cartId')
        .subscribe(response => paymentRequest = response).unsubscribe();
      expect(paymentRequest).toEqual({
        countryCode: 'US',
        currencyCode: 'USD',
      });
    });

    it('should dispatch RequestApplePayPaymentRequestEvent event', () => {
      let paymentRequest = null;
      service.enableApplePayButton().subscribe(response => paymentRequest = response).unsubscribe();
      expect(paymentRequest).toEqual({
        countryCode: 'US',
        currencyCode: 'USD',
      });

      expect(eventService.dispatch).toHaveBeenCalledWith(
        {
          applePayPaymentRequest: {
            countryCode: 'US',
            currencyCode: 'USD',
          }
        },
        RequestApplePayPaymentRequestEvent
      );
    });

    it('should dispatch ApplePayMerchantSessionEvent event', () => {
      let merchantSession;
      // @ts-ignore
      service['onValidateMerchant']({ validationURL: 'test.com' });
      expect(eventService.dispatch).toHaveBeenCalledWith({
          merchantSession: {
            merchantIdentifier: 'testMerchantIdentifier'
          }
        },
        ApplePayMerchantSessionEvent
      );
      service.merchantSession$.subscribe(response => merchantSession = response);
      expect(merchantSession).toEqual({ merchantIdentifier: 'testMerchantIdentifier' });
    });

    it('should dispatch ApplePayAuthorizePaymentEvent event', () => {
      spyOn(orderFacade, 'setPlacedOrder').and.callThrough();
      spyOn(worldpayApplepayConnector, 'authorizeApplePayPayment').and.callThrough();
      let paymentAuthorization = null;
      const event = {
        payment: {
          something: 'test'
        }
      };
      // @ts-ignore
      service['onPaymentAuthorized'](event);
      expect(worldpayApplepayConnector.authorizeApplePayPayment).toHaveBeenCalledWith(userId, cartId, event.payment);
      expect(orderFacade.setPlacedOrder).toHaveBeenCalledWith({ code: '0001' });
      expect(eventService.dispatch).toHaveBeenCalledWith({
          authorizePaymentEvent: {
            order: {
              code: '0001'
            }
          }
        },
        ApplePayAuthorizePaymentEvent
      );
      service.paymentAuthorization$.subscribe(response => paymentAuthorization = response);
      expect(paymentAuthorization).toEqual({
        order: {
          code: '0001'
        }
      });
    });

    it('should return ApplePaySession from window', () => {
      const applePaySession = service.getApplePaySessionFromWindow();
      expect(applePaySession).toBe(service.AppleSession);
    });
  });

  describe('enableApplePayButton', () => {
    it('should enable Apple Pay button if available', () => {
      spyOn(service, 'applePayButtonAvailable').and.returnValue(true);
      spyOn(service, 'getPaymentRequestFromState').and.returnValue(of({
        countryCode: 'US',
        currencyCode: 'USD',
      }));

      let paymentRequest = null;
      service.enableApplePayButton().subscribe(response => paymentRequest = response).unsubscribe();

      expect(paymentRequest).toEqual({
        countryCode: 'US',
        currencyCode: 'USD',
      });
    });

    it('should return null if Apple Pay button is not available', () => {
      spyOn(service, 'applePayButtonAvailable').and.returnValue(false);

      let paymentRequest = null;
      service.enableApplePayButton().subscribe(response => paymentRequest = response).unsubscribe();

      expect(paymentRequest).toBeNull();
    });
  });

  describe('createSession', () => {
    it('should create Apple Pay session successfully', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      spyOn(service, 'createSession').and.returnValue({
        onvalidatemerchant: () => {
        },
        onpaymentauthorized: () => {
        },
        onshippingmethodselected: () => {
        },
        onshippingcontactselected: () => {
        },
        onerror: () => {
        },
        oncancel: () => {
        }
      });
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      const session = service.createSession(paymentRequest);

      expect(session).toBeDefined();
      expect(session.onvalidatemerchant).toBeDefined();
      expect(session.onpaymentauthorized).toBeDefined();
      expect(session.onshippingmethodselected).toBeDefined();
      expect(session.onshippingcontactselected).toBeDefined();
      expect(session.onerror).toBeDefined();
      expect(session.oncancel).toBeDefined();
    });

    it('should not create Apple Pay session if not in browser', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(false);
      spyOn(service, 'createSession').and.returnValue(undefined);
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      const session = service.createSession(paymentRequest);

      expect(session).toBeUndefined();
    });

    it('should handle session error', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      // @ts-ignore
      spyOn(service, 'onPaymentError').and.callThrough();
      spyOn(service, 'createSession').and.returnValue({
        onvalidatemerchant: () => {
        },
        onpaymentauthorized: () => {
        },
        onshippingmethodselected: () => {
        },
        onshippingcontactselected: () => {
        },
        onerror: service['onPaymentError'].bind(service),
        oncancel: () => {
        }
      });
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      const session = service.createSession(paymentRequest);
      session.onerror();
      expect(service['onPaymentError']).toHaveBeenCalled();
    });

    it('should handle session cancel', () => {
      spyOn(windowRef, 'isBrowser').and.returnValue(true);
      // @ts-ignore
      spyOn(service, 'onPaymentError').and.callThrough();
      spyOn(service, 'createSession').and.returnValue({
        onvalidatemerchant: () => {
        },
        onpaymentauthorized: () => {
        },
        onshippingmethodselected: () => {
        },
        onshippingcontactselected: () => {
        },
        onerror: () => {
        },
        oncancel: service['onPaymentError'].bind(service)
      });
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      const session = service.createSession(paymentRequest);

      session.oncancel();
      expect(service['onPaymentError']).toHaveBeenCalled();
    });
  });

  describe('checkoutPreconditions', () => {
    it('should return userId and cartId when checkout conditions are met', () => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of('testUserId'));
      spyOn(activeCartService, 'takeActiveCartId').and.returnValue(of('testCartId'));
      spyOn(activeCartService, 'isGuestCart').and.returnValue(of(false));

      let result;
      service.checkoutPreconditions().subscribe(response => result = response);

      expect(result).toEqual(['testUserId', 'testCartId']);
    });

    it('should throw error when userId is not set', (doneFn) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(null));
      spyOn(activeCartService, 'takeActiveCartId').and.returnValue(of('testCartId'));
      spyOn(activeCartService, 'isGuestCart').and.returnValue(of(false));
      service.checkoutPreconditions().subscribe({
        next: () => fail('Expected an error to be thrown'),
        error: (err) => {
          expect(err).toEqual(new Error('Checkout conditions not met'));
          doneFn();
        },
      });
    });

    it('should throw error when cartId is not set', (doneFn) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of('testUserId'));
      spyOn(activeCartService, 'takeActiveCartId').and.returnValue(of(null));
      spyOn(activeCartService, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe({
        next: () => fail('Expected an error to be thrown'),
        error: (err) => {
          expect(err).toEqual(new Error('Checkout conditions not met'));
          doneFn();
        },
      });
    });

    it('should throw error when userId is anonymous and cart is not guest', (doneFn) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(OCC_USER_ID_ANONYMOUS));
      spyOn(activeCartService, 'takeActiveCartId').and.returnValue(of('testCartId'));
      spyOn(activeCartService, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe({
        next: () => fail('Expected an error to be thrown'),
        error: (err) => {
          expect(err).toEqual(new Error('Checkout conditions not met'));
          doneFn();
        },
      });
    });
  });

  describe('getMerchantSessionFromState', () => {
    it('should return the merchant session from state', () => {
      const mockMerchantSession = { merchantIdentifier: 'testMerchantIdentifier' };
      service.merchantSession$.next(mockMerchantSession);

      let merchantSession;
      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);

      expect(merchantSession).toEqual(mockMerchantSession);
    });

    it('should return null if merchant session is not set', () => {
      service.merchantSession$.next(null);

      let merchantSession;
      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);

      expect(merchantSession).toBeNull();
    });
  });

  describe('getPaymentAuthorizationFromState', () => {
    it('should return the payment authorization from state', () => {
      const mockPaymentAuthorization = { order: { code: '0001' } };
      service.paymentAuthorization$.next(mockPaymentAuthorization);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toEqual(mockPaymentAuthorization);
    });

    it('should return null if payment authorization is not set', () => {
      service.paymentAuthorization$.next(null);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toBeNull();
    });
  });

  describe('onPaymentAuthorized', () => {
    it('should set placed order when payment is authorized', () => {
      const mockResponse = { order: { code: '0001' } };
      spyOn(service['authorizeApplepayPaymentCommand'], 'execute').and.returnValue(of(mockResponse));
      spyOn(orderFacade, 'setPlacedOrder').and.callThrough();

      service['onPaymentAuthorized']({ payment: {} });

      expect(orderFacade.setPlacedOrder).toHaveBeenCalledWith(mockResponse.order);
    });

    it('should handle error when payment authorization fails', () => {
      spyOn(console, 'error');
      const mockError = new Error('Authorization failed');
      spyOn(service['authorizeApplepayPaymentCommand'], 'execute').and.returnValue(throwError(() => mockError));
      // @ts-ignore
      spyOn(service, 'onPaymentError').and.callThrough();

      service['onPaymentAuthorized']({ payment: {} });

      expect(service['onPaymentError']).toHaveBeenCalled();
      expect(console.error).toHaveBeenCalledWith('Applepay payment Error', mockError);
    });
  });
});
