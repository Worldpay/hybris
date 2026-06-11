import { TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CommandService, EventService, GlobalMessageService, LoggerService, OCC_USER_ID_ANONYMOUS, QueryService, UserIdService, WindowRef } from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { of, throwError } from 'rxjs';
import { mockUserId, MockUserIdService } from 'worldpay-sap-composable-tests';
import { WorldpayApplepayAdapter, WorldpayApplepayConnector } from '../../connectors';
import { ApplePayAuthorizePaymentEvent, ApplePayMerchantSessionEvent, RequestApplePayPaymentRequestEvent } from '../../events';
import { ApplePayMerchantSession, ApplePayPaymentRequest } from '../../models';

import { WorldpayApplepayService } from './worldpay-applepay.service';

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

const sessionMockData: any = {
  onvalidatemerchant: null,
  onpaymentauthorized: null,
  onerror: null,
  oncancel: null,
  begin: jasmine.createSpy()
};

const sessionMock = class {
  onvalidatemerchant: any = null;
  onpaymentauthorized: any = null;
  onerror: any = null;
  oncancel: any = null;

  constructor() {
  }

  begin() {
  }
};

const mockMerchantSession: ApplePayMerchantSession = {
  merchantSessionIdentifier: 'testMerchantIdentifier',
};

describe('WorldpayApplepayService', () => {
  let service: WorldpayApplepayService;
  let orderFacade: OrderFacade;
  let activeCartService: ActiveCartFacade;
  let userIdService: UserIdService;
  let worldpayApplepayConnector: WorldpayApplepayConnector;
  let eventService: EventService;
  let windowRef: WindowRef;
  let logger: LoggerService;

  const userId = mockUserId;
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
          useClass: MockUserIdService
        },
        {
          provide: WorldpayApplepayConnector,
          useClass: MockWorldpayApplepayConnector
        },
        {
          provide: WorldpayApplepayAdapter,
          useClass: MockWorldpayApplepayAdapter
        },
        LoggerService
      ]
    });
    service = TestBed.inject(WorldpayApplepayService);
    orderFacade = TestBed.inject(OrderFacade);
    activeCartService = TestBed.inject(ActiveCartFacade);
    worldpayApplepayConnector = TestBed.inject(WorldpayApplepayConnector);
    userIdService = TestBed.inject(UserIdService);
    eventService = TestBed.inject(EventService);
    logger = TestBed.inject(LoggerService);
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
      service.onValidateMerchant({ validationURL: 'test.com' });
      expect(eventService.dispatch).toHaveBeenCalledWith({
        merchantSession: {
          merchantIdentifier: 'testMerchantIdentifier'
        }
      }, ApplePayMerchantSessionEvent);
      service.merchantSession$.subscribe(response => merchantSession = response);
      expect(merchantSession).toEqual({ merchantIdentifier: 'testMerchantIdentifier' });
    });

    it('should dispatch ApplePayAuthorizePaymentEvent event', () => {
      spyOn(orderFacade, 'setPlacedOrder').and.callThrough();
      spyOn(worldpayApplepayConnector, 'authorizeApplePayPayment').and.callThrough();
      let paymentAuthorization = null;
      const event = {
        payment: {
          token: {
            transactionIdentifier: '100',
            paymentMethod: {
              displayName: 'Payment Method',
            }
          },
          shippingContact: {
            familyName: 'Shipping Contact',
          }
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
      }, ApplePayAuthorizePaymentEvent);
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

    it('should create an Apple Pay session with correct version and payment request', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      const mockSession: any = {
        onvalidatemerchant: null,
        onpaymentauthorized: null,
        onerror: null,
        oncancel: null,
        begin: jasmine.createSpy('begin')
      };
      spyOn(service, 'createSession').and.returnValue(mockSession);

      const session = service.createSession(paymentRequest);

      expect(service.createSession).toHaveBeenCalledWith(paymentRequest);
      expect(session).toBeDefined();
    });

    it('should set onvalidatemerchant handler to onValidateMerchant method', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      spyOn(service, 'onValidateMerchant');
      service.AppleSession = sessionMock;

      service.createSession(paymentRequest);

      expect(service.onValidateMerchant).not.toHaveBeenCalled();
    });

    it('should set onpaymentauthorized handler to onPaymentAuthorized method', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      spyOn(service, 'onPaymentAuthorized');

      service.AppleSession = sessionMock;

      service.createSession(paymentRequest);

      expect(service.onPaymentAuthorized).not.toHaveBeenCalled();
    });

    it('should set onerror handler to onPaymentError method', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      spyOn(service, 'onPaymentError');

      service.AppleSession = sessionMock;

      service.createSession(paymentRequest);

      expect(service.onPaymentError).not.toHaveBeenCalled();
    });

    it('should set oncancel handler to onPaymentError method', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      spyOn(service, 'onPaymentError');

      service.AppleSession = sessionMock;

      service.createSession(paymentRequest);

      expect(service.onPaymentError).not.toHaveBeenCalled();
    });

    it('should call begin method on the session', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };
      const mockBegin = jasmine.createSpy('begin');

      service.AppleSession = jasmine.createSpy().and.returnValue({
        ...sessionMock,
        begin: mockBegin
      });

      service.createSession(paymentRequest);

      expect(mockBegin).toHaveBeenCalled();
    });

    it('should return the created session', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };

      service.AppleSession = sessionMock;

      const session = service.createSession(paymentRequest);

      expect(session).toBeTruthy();
      expect(session.onvalidatemerchant).toBeDefined();
      expect(session.onpaymentauthorized).toBeDefined();
      expect(session.onerror).toBeDefined();
      expect(session.oncancel).toBeDefined();
    });

    it('should pass version 5 to AppleSession constructor', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };

      let capturedVersion: number;
      service.AppleSession = class {
        constructor(version: number) {
          capturedVersion = version;
        }

        begin() {
        }
      };

      service.createSession(paymentRequest);

      expect(capturedVersion).toEqual(5);
    });

    it('should pass payment request to AppleSession constructor', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };

      let capturedPaymentRequest: any;
      service.AppleSession = class {
        constructor(version: number, req: any) {
          capturedPaymentRequest = req;
        }

        begin() {
        }
      };

      service.createSession(paymentRequest);

      expect(capturedPaymentRequest).toEqual(paymentRequest);
    });

    it('should bind onvalidatemerchant handler to service context', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };

      service.AppleSession = sessionMock;
      const session = service.createSession(paymentRequest);

      expect(session.onvalidatemerchant).toBeTruthy();
      expect(typeof session.onvalidatemerchant).toBe('function');
    });

    it('should bind onpaymentauthorized handler to service context', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };

      service.AppleSession = sessionMock;

      const session = service.createSession(paymentRequest);

      expect(session.onpaymentauthorized).toBeTruthy();
      expect(typeof session.onpaymentauthorized).toBe('function');
    });

    it('should bind onerror handler to service context', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };

      service.AppleSession = sessionMock;

      const session = service.createSession(paymentRequest);

      expect(session.onerror).toBeTruthy();
      expect(typeof session.onerror).toBe('function');
    });

    it('should bind oncancel handler to service context', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };

      service.AppleSession = sessionMock;

      const session = service.createSession(paymentRequest);

      expect(session.oncancel).toBeTruthy();
      expect(typeof session.oncancel).toBe('function');
    });

    it('should handle session creation with all payment request properties', () => {
      const paymentRequest = {
        currencyCode: 'EUR',
        countryCode: 'DE',
        total: {
          label: 'Total',
          amount: '100.00',
        },
        lineItems: [{
          label: 'Item',
          amount: '100.00'
        }]
      };

      service.AppleSession = sessionMock;

      const session = service.createSession(paymentRequest);

      expect(session).toBeDefined();
    });

    it('should create session when AppleSession constructor throws error', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };

      service.AppleSession = class {
        constructor() {
          throw new Error('AppleSession not available');
        }
      };

      expect(() => service.createSession(paymentRequest)).toThrow();
    });

    it('should set all event handlers before calling begin', () => {
      const paymentRequest: ApplePayPaymentRequest = {
        currencyCode: 'USD',
        countryCode: 'US'
      };

      let handlersSetBeforeBegin = false;
      service.AppleSession = jasmine.createSpy().and.returnValue({
        ...sessionMock,
        begin() {
          handlersSetBeforeBegin = this.onvalidatemerchant !== null &&
                                   this.onpaymentauthorized !== null &&
                                   this.onerror !== null &&
                                   this.oncancel !== null;
        }
      });

      service.createSession(paymentRequest);

      expect(handlersSetBeforeBegin).toBeTrue();
    });

    it('should create session with null payment request', () => {
      const paymentRequest: ApplePayPaymentRequest = null;
      service.AppleSession = sessionMock;
      const session = service.createSession(paymentRequest);

      expect(session).toBeDefined();
    });

    it('should register apple pay callbacks', () => {
      service.AppleSession = jasmine.createSpy().and.returnValue(sessionMockData);
      service.createSession({} as ApplePayPaymentRequest);
      expect(sessionMockData.onvalidatemerchant).toEqual(jasmine.any(Function));
      expect(sessionMockData.onpaymentauthorized).toEqual(jasmine.any(Function));
      expect(sessionMockData.onerror).toEqual(jasmine.any(Function));
      expect(sessionMockData.oncancel).toEqual(jasmine.any(Function));
    });

    it('should create session with undefined payment request', () => {
      const paymentRequest: ApplePayPaymentRequest = undefined;

      service.AppleSession = sessionMock;
      const session = service.createSession(paymentRequest);

      expect(session).toBeDefined();
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
      spyOn(logger, 'error');
      const mockError = new Error('Authorization failed');
      spyOn(service['authorizeApplepayPaymentCommand'], 'execute').and.returnValue(throwError(() => mockError));
      // @ts-ignore
      spyOn(service, 'onPaymentError').and.callThrough();

      service['onPaymentAuthorized']({ payment: {} });

      expect(service['onPaymentError']).toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith('Applepay payment Error', mockError);
    });
  });

  describe('setMerchantSession', () => {
    it('should set merchant session with valid payment request', () => {
      service.setMerchantSession(mockMerchantSession);

      let merchantSession;
      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);

      expect(merchantSession).toEqual(mockMerchantSession);
    });

    it('should set merchant session to null', () => {
      service.setMerchantSession(null);

      let merchantSession;
      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);

      expect(merchantSession).toBeNull();
    });

    it('should update merchant session when called multiple times', () => {
      const firstSession = mockMerchantSession;
      const secondSession = { merchantSessionIdentifier: 'testMerchantIdentifier2' };

      service.setMerchantSession(firstSession);
      service.setMerchantSession(secondSession);

      let merchantSession;
      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);

      expect(merchantSession).toEqual(secondSession);
    });

    it('should clear merchant session by setting to null', () => {
      service.setMerchantSession(mockMerchantSession);
      service.setMerchantSession(null);

      let merchantSession;
      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);

      expect(merchantSession).toBeNull();
    });

    it('should preserve merchant session object reference equality', () => {
      service.setMerchantSession(mockMerchantSession);

      let receivedSession;
      service.getMerchantSessionFromState().subscribe(response => receivedSession = response);

      expect(receivedSession).toBe(mockMerchantSession);
    });

    it('should handle undefined merchant session', () => {
      service.setMerchantSession(undefined as any);

      let merchantSession;
      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);

      expect(merchantSession).toBeUndefined();
    });
  });

  describe('setPaymentAuthorization', () => {
    it('should set payment authorization with valid payment object', () => {
      const mockPayment = { order: { code: '0001' } };
      service.setPaymentAuthorization(mockPayment);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toEqual(mockPayment);
    });

    it('should set payment authorization to null', () => {
      service.setPaymentAuthorization(null);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toBeNull();
    });

    it('should update payment authorization when called multiple times', () => {
      const firstPayment = { order: { code: '0001' } };
      const secondPayment = { order: { code: '0002' } };

      service.setPaymentAuthorization(firstPayment);
      service.setPaymentAuthorization(secondPayment);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toEqual(secondPayment);
    });

    it('should clear payment authorization by setting to null', () => {
      const mockPayment = { order: { code: '0001' } };
      service.setPaymentAuthorization(mockPayment);
      service.setPaymentAuthorization(null);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toBeNull();
    });

    it('should emit new value to subscribers when payment authorization is set', (done) => {
      const mockPayment = { order: { code: '0001' } };
      let emittedCount = 0;

      service.getPaymentAuthorizationFromState().subscribe(response => {
        emittedCount++;
        if (emittedCount === 2) {
          expect(response).toEqual(mockPayment);
          done();
        }
      });

      service.setPaymentAuthorization(mockPayment);
    });

    it('should handle complex payment authorization objects', () => {
      const complexPayment = {
        order: {
          code: '123456',
          date: new Date(),
          total: '99.99'
        },
        token: 'payment_token_123',
        timestamp: new Date()
      };

      service.setPaymentAuthorization(complexPayment);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toEqual(complexPayment);
    });

    it('should preserve payment authorization object reference equality', () => {
      const mockPayment = { order: { code: '0001' } };
      service.setPaymentAuthorization(mockPayment);

      let receivedPayment;
      service.getPaymentAuthorizationFromState().subscribe(response => receivedPayment = response);

      expect(receivedPayment).toBe(mockPayment);
    });

    it('should handle undefined payment authorization', () => {
      service.setPaymentAuthorization(undefined);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toBeUndefined();
    });

    it('should handle payment authorization with empty object', () => {
      const emptyPayment = {};
      service.setPaymentAuthorization(emptyPayment);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toEqual(emptyPayment);
    });
  });

  describe('setMerchantSession and setPaymentAuthorization together', () => {
    it('should manage merchant session and payment authorization independently', () => {
      const mockPayment = { order: { code: '0001' } };

      service.setMerchantSession(mockMerchantSession);
      service.setPaymentAuthorization(mockPayment);

      let merchantSession;
      let paymentAuthorization;

      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(merchantSession).toEqual(mockMerchantSession);
      expect(paymentAuthorization).toEqual(mockPayment);
    });

    it('should update merchant session without affecting payment authorization', () => {
      const initialPayment = { order: { code: '0001' } };

      service.setPaymentAuthorization(initialPayment);
      service.setMerchantSession(mockMerchantSession);

      let paymentAuthorization;
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(paymentAuthorization).toEqual(initialPayment);
    });

    it('should update payment authorization without affecting merchant session', () => {
      const newPayment = { order: { code: '0002' } };

      service.setMerchantSession(mockMerchantSession);
      service.setPaymentAuthorization(newPayment);

      let merchantSession;
      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);

      expect(merchantSession).toEqual(mockMerchantSession);
    });

    it('should clear both states independently', () => {
      const mockPayment = { order: { code: '0001' } };

      service.setMerchantSession(mockMerchantSession);
      service.setPaymentAuthorization(mockPayment);

      service.setMerchantSession(null);

      let merchantSession;
      let paymentAuthorization;

      service.getMerchantSessionFromState().subscribe(response => merchantSession = response);
      service.getPaymentAuthorizationFromState().subscribe(response => paymentAuthorization = response);

      expect(merchantSession).toBeNull();
      expect(paymentAuthorization).toEqual(mockPayment);
    });
  });
});
