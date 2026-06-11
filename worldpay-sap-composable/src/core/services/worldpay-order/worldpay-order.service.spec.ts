import { ComponentRef, ViewContainerRef } from '@angular/core';
import { fakeAsync, TestBed } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { Params } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import {
  CommandService,
  EventService,
  GlobalMessageService,
  GlobalMessageType, HttpErrorModel,
  LoggerService,
  PaymentDetails,
  QueryState,
  RoutingService,
  UserIdService,
  WindowRef
} from '@spartacus/core';
import { OrderAdapter, OrderConnector } from '@spartacus/order/core';
import { Order, OrderPlacedEvent } from '@spartacus/order/root';
import { LAUNCH_CALLER, LaunchDialogService } from '@spartacus/storefront';
import { BehaviorSubject, Observable, ObservedValueOf, of, throwError } from 'rxjs';
import {
  MockActiveCartFacade,
  MockGlobalMessageService,
  MockLaunchDialogService,
  mockOrder,
  MockRoutingService,
  mockUserId,
  MockUserIdService,
  MockWorldpayCheckoutPaymentService,
  MockWorldpayConnector
} from 'worldpay-sap-composable-tests';
import { WorldpayACHConnector, WorldpayApmAdapter, WorldpayApmConnector, WorldpayConnector } from '../../connectors';
import { ClearWorldpayACHPaymentFormEvent, ClearWorldpayPaymentDetailsEvent, DDC3dsJwtSetEvent, InitialPaymentRequestSetEvent } from '../../events';
import { ACHPaymentForm, ApmData, APMRedirectResponse, BrowserInfo, PaymentMethod, PlaceOrderResponse } from '../../interfaces';
import { WorldpayApmService } from '../worldpay-apm/worldpay-apm.service';
import { WorldpayCheckoutPaymentService } from '../worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayOrderService } from './worldpay-order.service';
import createSpy = jasmine.createSpy;

const userId = mockUserId;
const cartId = 'cartId';
const order: Order = mockOrder;
const apm = {
  code: PaymentMethod.Card,
  name: 'credit'
};
const apmSubject = new BehaviorSubject<ApmData>(apm);
const apms = [
  { code: PaymentMethod.iDeal },
  { code: PaymentMethod.PayPal },
  { code: PaymentMethod.ApplePay },
  { code: PaymentMethod.GooglePay },
];

const mockParamsError: Params = {
  pending: 'true',
  status: 'ERROR',
  orderKey: 'E2Y^MERCHANT2ECOM^00000018-1761730288154',
};
const mockParams: Params = {
  pending: 'true',
  paymentStatus: 'AUTHORISED',
  orderKey: 'E2Y^MERCHANT2ECOM^00000018-1761730288154',
};
const mockPaymentDetails: PaymentDetails = {
  accountHolderName: 'user',
  billingAddress: {
    formattedAddress: 'address',
    id: '0001',
  },
  cardNumber: '1111222233334444',
  cardType: {
    code: 'visa',
    name: 'Visa'
  },
  cvn: '123',
  defaultPayment: false,
  expiryMonth: '12',
  expiryYear: '24',
  id: '0001',
  subscriptionId: '000000000',
};

const browserInfo: BrowserInfo = {
  javaEnabled: false,
  language: 'en-GB',
  colorDepth: 24,
  screenHeight: 1080,
  screenWidth: 1920,
  timeZone: (-60).toString(),
  userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36',
  javascriptEnabled: true
};

class MockWorldpayApmAdapter implements Partial<WorldpayApmAdapter> {
  authoriseApmRedirect(): Observable<APMRedirectResponse> {
    return of({
      postUrl: 'https://test.com',
      mappingLabels: {},
      parameters: {
        entry: []
      }
    });
  }
}

const achPaymentForm: ACHPaymentForm = {
  accountType: 'checking',
  routingNumber: '123456789',
  accountNumber: '123456789',
  companyName: 'user',
};

describe('WorldpayOrderService', () => {
  let service: WorldpayOrderService;
  let eventService: EventService;
  let globalMessageService: GlobalMessageService;
  let worldpayApmService: WorldpayApmService;
  let worldpayConnector: WorldpayConnector;
  let worldpayACHConnector: WorldpayACHConnector;
  let worldpayApmConnector: jasmine.SpyObj<WorldpayApmConnector>;
  let launchDialogService: LaunchDialogService;
  let routingService: RoutingService;
  let logger: LoggerService;

  class MockWorldpayApmService implements Partial<WorldpayApmService> {
    getSelectedAPMFromState(): Observable<ApmData> {
      return apmSubject;
    }

    getWorldpayAvailableApmsFromState(): Observable<ApmData[]> {
      return of(apms);
    }

    getApmComponentById(): Observable<ApmData> {
      return of(apm);
    }

    requestAvailableApmsState(): Observable<QueryState<ApmData[]>> {
      return of({
        loading: false,
        error: false,
        data: apms
      });
    }

    getWorldpayAvailableApmsLoading(): Observable<boolean> {
      return of(false);
    }

    getWorldpayAPMRedirectUrl(): Observable<APMRedirectResponse> {
      return of({
        mappingLabels: {},
        parameters: {
          entry: [],
        },
        postUrl: 'https://test.com'
      });
    }

    setWorldpayAPMRedirectUrlEvent(): void {
    }

    getAPMRedirectUrl(): Observable<APMRedirectResponse> {
      return of({
        mappingLabels: {},
        parameters: {
          entry: [],
        },
        postUrl: 'https://test.com'
      });
    }

    getLoading(): Observable<boolean> {
      return of(false);
    }

    placeWorldpayOrder(): Observable<[ObservedValueOf<Observable<ApmData>>, ObservedValueOf<Observable<QueryState<PaymentDetails | undefined>>>]> {
      return of();
    }

    showErrorMessage(error: HttpErrorModel): void {
      const errorMessage: string = error?.details?.[0]?.message || ' ';
      globalMessageService.add({ key: errorMessage }, GlobalMessageType.MSG_TYPE_ERROR);
    }
  }

  class MockWorldpayACHConnector implements Partial<WorldpayACHConnector> {
    placeACHOrder(): Observable<any> {
      return of({});
    }
  }

  beforeEach(() => {
    const MockWorldpayApmConnector = jasmine.createSpyObj('WorldpayApmConnector', [
      'placeOrderRedirect',
      'placeBankTransferOrderRedirect'
    ]);

    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
      ],
      providers: [
        WorldpayOrderService,
        {
          provide: ActiveCartFacade,
          useClass: MockActiveCartFacade
        },
        {
          provide: UserIdService,
          useClass: MockUserIdService
        },
        {
          provide: LaunchDialogService,
          useClass: MockLaunchDialogService
        },
        CommandService,
        EventService,
        {
          provide: GlobalMessageService,
          useClass: MockGlobalMessageService
        },
        WindowRef,
        OrderConnector,
        OrderAdapter,
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector
        },
        {
          provide: WorldpayApmService,
          useClass: MockWorldpayApmService
        },
        {
          provide: WorldpayCheckoutPaymentService,
          useFactory: (sanitizer: DomSanitizer) => new MockWorldpayCheckoutPaymentService(sanitizer),
          deps: [DomSanitizer]
        },
        {
          provide: WorldpayApmAdapter,
          useClass: MockWorldpayApmAdapter
        },
        {
          provide: RoutingService,
          useClass: MockRoutingService
        },
        {
          provide: WorldpayACHConnector,
          useClass: MockWorldpayACHConnector
        },
        {
          provide: WorldpayApmConnector,
          useValue: MockWorldpayApmConnector
        },
        LoggerService
      ]
    });
    service = TestBed.inject(WorldpayOrderService);
    eventService = TestBed.inject(EventService);
    globalMessageService = TestBed.inject(GlobalMessageService);
    launchDialogService = TestBed.inject(LaunchDialogService);
    worldpayApmService = TestBed.inject(WorldpayApmService);
    worldpayConnector = TestBed.inject(WorldpayConnector);
    worldpayACHConnector = TestBed.inject(WorldpayACHConnector);
    worldpayApmConnector = TestBed.inject(WorldpayApmConnector) as jasmine.SpyObj<WorldpayApmConnector>;
    routingService = TestBed.inject(RoutingService);
    logger = TestBed.inject(LoggerService);

    spyOn(eventService, 'dispatch').and.callThrough();
    spyOn(service, 'setPlacedOrder').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call executeDDC3dsJwtCommand', () => {
    spyOn(worldpayConnector, 'getDDC3dsJwt').and.returnValue(of({
      ddcUrl: 'https://test.com',
      jwt: 'jwt'
    }));
    service.executeDDC3dsJwtCommand().subscribe().unsubscribe();
    expect(worldpayConnector.getDDC3dsJwt).toHaveBeenCalledWith();
    expect(eventService.dispatch).toHaveBeenCalledWith({
      ddcInfo: {
        ddcUrl: 'https://test.com',
        jwt: 'jwt'
      },
    }, DDC3dsJwtSetEvent);
  });

  it('should show loading spinner', () => {
    let loading = false;
    spyOn(worldpayApmService, 'getLoading').and.returnValue(of(true));
    worldpayApmService.getLoading().subscribe(response => loading = response);
    expect(loading).toBeTrue();
  });

  it('should call getWorldpayAPMRedirectUrl', () => {
    spyOn(worldpayApmService, 'getWorldpayAPMRedirectUrl').and.callThrough();
    let apmRedirect = {};
    worldpayApmService.getWorldpayAPMRedirectUrl(apm, false).subscribe(response => apmRedirect = response);
    expect(worldpayApmService.getWorldpayAPMRedirectUrl).toHaveBeenCalledWith(apm, false);
    expect(apmRedirect).toEqual({
      mappingLabels: {},
      parameters: { entry: [] },
      postUrl: 'https://test.com'
    });
  });

  it('should be able to call initial payment request', () => {
    const dfReferenceId = '123-123213185-1231231';
    const cseToken = '123';
    const acceptedTermsAndConditions = true;

    service.initialPaymentRequest(
      mockPaymentDetails,
      dfReferenceId,
      cseToken,
      acceptedTermsAndConditions,
      null,
      browserInfo,
      undefined
    ).subscribe().unsubscribe();

    const paymentDetailsWithoutCardNumber = { ...mockPaymentDetails };
    delete paymentDetailsWithoutCardNumber.cardNumber;
    expect(worldpayConnector.initialPaymentRequest).toHaveBeenCalledWith(
      userId,
      cartId,
      paymentDetailsWithoutCardNumber,
      dfReferenceId,
      '600x400',
      cseToken,
      acceptedTermsAndConditions,
      null,
      browserInfo,
      undefined
    );
  });

  it('should call Challenge Accepted for registered user', fakeAsync(() => {
    service.challengeAccepted({
      accepted: true,
      orderCode: '100'
    });

    expect(eventService.dispatch).toHaveBeenCalledWith({
      userId,
      cartId,
      order,
      cartCode: cartId
    }, OrderPlacedEvent);

    expect(worldpayConnector.getOrder).toHaveBeenCalledWith('userId', '100', undefined);
    expect(service.setPlacedOrder).toHaveBeenCalledWith(order);
    expect(routingService.go).toHaveBeenCalledWith({
      cxRoute: 'orderConfirmation',
    });
  }));

  it('should call Challenge Accepted for anonymous user', () => {
    service.challengeAccepted({
      accepted: true,
      orderCode: '100',
      guestCustomer: true,
      customerID: 'test@email.com'
    });

    expect(worldpayConnector.getOrder).toHaveBeenCalledWith('test@email.com', '100', true);
    expect(eventService.dispatch).toHaveBeenCalledWith({
      userId,
      cartId,
      order,
      cartCode: cartId
    }, OrderPlacedEvent);

    expect(service.setPlacedOrder).toHaveBeenCalledWith(order);
    expect(routingService.go).toHaveBeenCalledWith({
      cxRoute: 'orderConfirmation',
    });
  });

  it('should show error messages', () => {
    service.challengeFailed('checkoutReview.challengeFailed');
    expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.challengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
  });

  it('should showErrorMessage', () => {
    let error = {
      details: [
        { message: 'error' }
      ]
    };
    worldpayApmService.showErrorMessage(error);
    expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'error' }, GlobalMessageType.MSG_TYPE_ERROR);
  });

  it('should checkoutPreconditions', () => {
    let checkoutPreconditions: ArrayLike<any> = [];
    // @ts-ignore
    service.checkoutPreconditions().subscribe(response => checkoutPreconditions = response);
    expect(checkoutPreconditions).toEqual([userId, 'cartId']);
  });

  it('should handle error when placing ACH order', (done) => {
    const error = new Error('Failed to place order');
    spyOn(worldpayACHConnector, 'placeACHOrder').and.returnValue(throwError(() => error));
    service.placeACHOrder(achPaymentForm).subscribe({
      error: (err) => {
        expect(err).toEqual(error);
        done();
      }
    });
  });

  describe('initialPaymentRequestCommand', () => {
    it('should dispatch InitialPaymentRequestSetEvent and clear loading when 3DSecure is needed', () => {
      const response = {
        threeDSecureNeeded: true,
        threeDSecureInfo: {
          threeDSFlexData: {
            entry: [{
              key: 'key1',
              value: 'value1'
            }]
          }
        },
        transactionStatus: '',
        order: {}
      } as PlaceOrderResponse;

      worldpayConnector.initialPaymentRequest = createSpy('WorldpayAdapter.initialPaymentRequest').and.returnValue(of(response));
      spyOn(service, 'clearLoading');

      service['initialPaymentRequestCommand'].execute({
        paymentDetails: mockPaymentDetails,
        dfReferenceId: 'dfReferenceId',
        challengeWindowSize: '600x400',
        cseToken: 'cseToken',
        acceptedTermsAndConditions: true,
        deviceSession: 'deviceSession',
        browserInfo
      }).subscribe();

      expect(eventService.dispatch).toHaveBeenCalledWith(response, InitialPaymentRequestSetEvent);
      expect(service.clearLoading).toHaveBeenCalled();
    });

    it('should dispatch InitialPaymentRequestSetEvent and call placedOrderSuccess when transaction is authorised', () => {
      const response = {
        threeDSecureNeeded: false,
        threeDSecureInfo: {},
        transactionStatus: 'AUTHORISED',
        order
      } as PlaceOrderResponse;

      worldpayConnector.initialPaymentRequest = createSpy('WorldpayAdapter.initialPaymentRequest').and.returnValue(of(response));
      spyOn(service, 'placedOrderSuccess');

      service['initialPaymentRequestCommand'].execute({
        paymentDetails: mockPaymentDetails,
        dfReferenceId: 'dfReferenceId',
        challengeWindowSize: '600x400',
        cseToken: 'cseToken',
        acceptedTermsAndConditions: true,
        deviceSession: 'deviceSession',
        browserInfo
      }).subscribe();

      expect(eventService.dispatch).toHaveBeenCalledWith(response, InitialPaymentRequestSetEvent);
      expect(service.placedOrderSuccess).toHaveBeenCalledWith(userId, cartId, response.order);
    });

    it('should add global message and clear loading when transaction fails and show generic error message when no return message is included', () => {
      const response = {
        threeDSecureNeeded: false,
        threeDSecureInfo: {},
        transactionStatus: 'FAILED',
        order: {},
      } as PlaceOrderResponse;

      worldpayConnector.initialPaymentRequest = createSpy('WorldpayAdapter.initialPaymentRequest').and.returnValue(of(response));
      spyOn(service, 'clearLoading');

      service['initialPaymentRequestCommand'].execute({
        paymentDetails: mockPaymentDetails,
        dfReferenceId: 'dfReferenceId',
        challengeWindowSize: '600x400',
        cseToken: 'cseToken',
        acceptedTermsAndConditions: true,
        deviceSession: 'deviceSession',
        browserInfo
      }).subscribe();

      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.initialPaymentRequestFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(service.clearLoading).toHaveBeenCalled();
    });

    it('should add global message and clear loading when transaction fails and return error message', () => {
      const response = {
        threeDSecureNeeded: false,
        threeDSecureInfo: {},
        transactionStatus: 'FAILED',
        order: {},
        returnMessage: 'Specific error message'
      } as PlaceOrderResponse;

      worldpayConnector.initialPaymentRequest = createSpy('WorldpayAdapter.initialPaymentRequest').and.returnValue(of(response));
      spyOn(service, 'clearLoading');

      service['initialPaymentRequestCommand'].execute({
        paymentDetails: mockPaymentDetails,
        dfReferenceId: 'dfReferenceId',
        challengeWindowSize: '600x400',
        cseToken: 'cseToken',
        acceptedTermsAndConditions: true,
        deviceSession: 'deviceSession',
        browserInfo
      }).subscribe();

      expect(globalMessageService.add).toHaveBeenCalledWith({ raw: 'Specific error message' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(service.clearLoading).toHaveBeenCalled();
    });
  });

  describe('getDDC3dsJwt', () => {
    it('should dispatch DDC3dsJwtSetEvent with ddcInfo when getDDC3dsJwt is successful', () => {
      const ddcInfo = {
        ddcUrl: 'https://test.com',
        jwt: 'jwt'
      };
      spyOn(worldpayConnector, 'getDDC3dsJwt').and.returnValue(of(ddcInfo));

      service['getDDC3dsJwtCommand'].execute(undefined).subscribe();

      expect(eventService.dispatch).toHaveBeenCalledWith({ ddcInfo }, DDC3dsJwtSetEvent);
    });

    it('should handle error when getDDC3dsJwt fails', () => {
      const error = new Error('Failed to get DDC3dsJwt');
      spyOn(worldpayConnector, 'getDDC3dsJwt').and.returnValue(throwError(() => error));

      service['getDDC3dsJwtCommand'].execute(undefined).subscribe({
        error: (err) => {
          expect(err).toEqual(error);
        }
      });

      expect(eventService.dispatch).not.toHaveBeenCalled();
    });
  });

  describe('challengeAcceptedCommand', () => {
    it('should dispatch OrderPlacedEvent and ClearWorldpayPaymentDetailsEvent when challenge is accepted for registered user', () => {
      const response = {
        customerID: 'userId',
        orderCode: 'orderCode',
        guestCustomer: false
      };

      worldpayConnector.getOrder = createSpy('WorldpayConnector.getOrder ').and.returnValue(of(order));

      service['challengeAcceptedCommand'].execute(response).subscribe();

      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        order,
        cartCode: cartId
      }, OrderPlacedEvent);
    });

    it('should dispatch OrderPlacedEvent and ClearWorldpayPaymentDetailsEvent when challenge is accepted for guest user', () => {
      const response = {
        customerID: 'guestId',
        orderCode: 'orderCode',
        guestCustomer: true
      };

      worldpayConnector.getOrder = createSpy('WorldpayConnector.getOrder ').and.returnValue(of(order));

      service['challengeAcceptedCommand'].execute(response).subscribe();

      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        order,
        cartCode: cartId
      }, OrderPlacedEvent);
    });

    it('should handle error when getOrder fails', () => {
      const response = {
        customerID: 'userId',
        orderCode: 'orderCode',
        guestCustomer: false
      };

      const error = new Error('Failed to get order');
      worldpayConnector.getOrder = createSpy('WorldpayAdapter.getOrder').and.returnValue(throwError(() => error));

      service['challengeAcceptedCommand'].execute(response).subscribe({
        error: (err) => {
          expect(err).toEqual(error);
        }
      });

      expect(eventService.dispatch).not.toHaveBeenCalledWith({}, OrderPlacedEvent);
    });
  });

  describe('placeRedirectOrderCommand', () => {
    it('should place redirect order successfully', () => {
      worldpayApmConnector.placeOrderRedirect.and.returnValue(of(order));

      service['placeRedirectOrderCommand'].execute(mockParams).subscribe();

      expect(worldpayApmConnector.placeOrderRedirect).toHaveBeenCalledWith(userId, cartId, mockParams);
      expect(service.setPlacedOrder).toHaveBeenCalledWith(order);
      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        cartCode: cartId,
        order,
      }, OrderPlacedEvent);
    });

    it('should handle error when placing redirect order fails', () => {
      const error = new Error('Failed to place redirect order');
      worldpayApmConnector.placeOrderRedirect.and.returnValue(throwError(() => error));

      service['placeRedirectOrderCommand'].execute(mockParamsError).subscribe({
        error: (err) => {
          expect(err).toEqual(error);
        }
      });

      expect(service.setPlacedOrder).not.toHaveBeenCalled();
      expect(eventService.dispatch).not.toHaveBeenCalledWith({}, OrderPlacedEvent);
    });
  });

  describe('placeBankTransferRedirectOrderCommand', () => {
    it('should place bank transfer redirect order successfully', () => {
      const orderId = 'orderId';
      worldpayApmConnector.placeBankTransferOrderRedirect.and.returnValue(of(order));

      service['placeBankTransferRedirectOrderCommand'].execute(orderId).subscribe();

      expect(worldpayApmConnector.placeBankTransferOrderRedirect).toHaveBeenCalledWith(userId, cartId, orderId);
      expect(service.setPlacedOrder).toHaveBeenCalledWith(order);
      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        cartCode: cartId,
        order,
      }, OrderPlacedEvent);
    });

    it('should handle error when placing bank transfer redirect order fails', () => {
      const orderId = 'orderId';
      const error = new Error('Failed to place bank transfer redirect order');
      worldpayApmConnector.placeBankTransferOrderRedirect.and.returnValue(throwError(() => error));

      service['placeBankTransferRedirectOrderCommand'].execute(orderId).subscribe({
        error: (err) => {
          expect(err).toEqual(error);
        }
      });

      expect(service.setPlacedOrder).not.toHaveBeenCalled();
      expect(eventService.dispatch).not.toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
    });

    it('should not place bank transfer redirect order if preconditions fail', () => {
      const orderId = 'orderId';
      // @ts-ignore
      spyOn(service, 'checkoutPreconditions').and.returnValue(throwError(() => new Error('Preconditions failed')));

      service['placeBankTransferRedirectOrderCommand'].execute(orderId).subscribe({
        error: (err) => {
          expect(err.message).toBe('Preconditions failed');
        }
      });

      expect(worldpayApmConnector.placeBankTransferOrderRedirect).not.toHaveBeenCalled();
      expect(service.setPlacedOrder).not.toHaveBeenCalled();
    });
  });

  describe('placeACHOrderCommand$', () => {
    it('should place ACH order successfully', () => {
      const achPaymentForm: ACHPaymentForm = {
        accountType: 'checking',
        routingNumber: '123456789',
        accountNumber: '123456789',
        companyName: 'user',
      };
      spyOn(worldpayACHConnector, 'placeACHOrder').and.returnValue(of(order));

      service['placeACHOrderCommand$'].execute(achPaymentForm).subscribe();

      expect(worldpayACHConnector.placeACHOrder).toHaveBeenCalledWith(userId, cartId, achPaymentForm);
      expect(service.setPlacedOrder).toHaveBeenCalledWith(order);
      expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayACHPaymentFormEvent);
    });

    it('should handle error when placing ACH order fails', () => {
      const achPaymentForm: ACHPaymentForm = {
        accountType: 'checking',
        routingNumber: '123456789',
        accountNumber: '123456789',
        companyName: 'user',
      };
      const error = new Error('Failed to place ACH order');
      spyOn(worldpayACHConnector, 'placeACHOrder').and.returnValue(throwError(() => error));

      service['placeACHOrderCommand$'].execute(achPaymentForm).subscribe({
        error: (err) => {
          expect(err).toEqual(error);
        }
      });

      expect(service.setPlacedOrder).not.toHaveBeenCalled();
      expect(eventService.dispatch).not.toHaveBeenCalledWith({}, ClearWorldpayACHPaymentFormEvent);
    });

    it('should not place ACH order if preconditions fail', () => {
      const achPaymentForm: ACHPaymentForm = {
        accountType: 'checking',
        routingNumber: '123456789',
        accountNumber: '123456789',
        companyName: 'user',
      };
      // @ts-ignore
      spyOn(service, 'checkoutPreconditions').and.returnValue(throwError(() => new Error('Preconditions failed')));
      spyOn(worldpayACHConnector, 'placeACHOrder');
      service['placeACHOrderCommand$'].execute(achPaymentForm).subscribe({
        error: (err) => {
          expect(err.message).toBe('Preconditions failed');
        }
      });

      expect(worldpayACHConnector.placeACHOrder).not.toHaveBeenCalled();
      expect(service.setPlacedOrder).not.toHaveBeenCalled();
      expect(eventService.dispatch).not.toHaveBeenCalledWith({}, ClearWorldpayACHPaymentFormEvent);
    });
  });

  describe('challengeAccepted', () => {
    it('should handle challenge accepted and navigate to order confirmation', (done) => {
      const worldpayChallengeResponse = {
        customerID: 'userId',
        orderCode: 'orderCode',
        guestCustomer: false
      };
      const order: Order = { code: 'orderCode' };

      worldpayConnector.getOrder = createSpy('WorldpayConnector.getOrder ').and.returnValue(of(order));

      service.challengeAccepted(worldpayChallengeResponse);

      expect(worldpayConnector.getOrder).toHaveBeenCalledWith('userId', 'orderCode', false);
      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        order,
        cartCode: cartId
      }, OrderPlacedEvent);
      expect(service.setPlacedOrder).toHaveBeenCalledWith(order);
      expect(routingService.go).toHaveBeenCalledWith({ cxRoute: 'orderConfirmation' });
      done();
    });

    it('should handle challenge accepted for guest user and navigate to order confirmation', (done) => {
      const worldpayChallengeResponse = {
        customerID: 'guestId',
        orderCode: 'orderCode',
        guestCustomer: true
      };
      const order: Order = { code: 'orderCode' };

      worldpayConnector.getOrder = createSpy('WorldpayConnector.getOrder ').and.returnValue(of(order));

      service.challengeAccepted(worldpayChallengeResponse);

      expect(worldpayConnector.getOrder).toHaveBeenCalledWith('guestId', 'orderCode', true);
      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        order,
        cartCode: cartId
      }, OrderPlacedEvent);
      expect(service.setPlacedOrder).toHaveBeenCalledWith(order);
      expect(routingService.go).toHaveBeenCalledWith({ cxRoute: 'orderConfirmation' });
      done();
    });

    it('should handle error when challenge accepted fails', (done) => {
      const worldpayChallengeResponse = {
        customerID: 'userId',
        orderCode: 'orderCode',
        guestCustomer: false
      };
      const error = new Error('Failed to get order');
      spyOn(logger, 'error');
      worldpayConnector.getOrder = createSpy('WorldpayConnector.getOrder ').and.returnValue(throwError(() => error));

      service.challengeAccepted(worldpayChallengeResponse);

      expect(worldpayConnector.getOrder).toHaveBeenCalledWith('userId', 'orderCode', false);
      expect(service.setPlacedOrder).not.toHaveBeenCalled();
      expect(routingService.go).not.toHaveBeenCalled();
      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.challengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(logger.error).toHaveBeenCalledWith('Challenge Failed:', error);
      done();
    });
  });

  describe('placeOrderRedirect', () => {
    it('should place redirect order successfully and return true', (done) => {
      const orderDetails: Order = { code: 'orderCode' };
      worldpayApmConnector.placeOrderRedirect.and.returnValue(of(order));
      spyOn(service, 'getOrderDetails').and.returnValue(of(orderDetails));

      service.placeRedirectOrder(mockParams).subscribe((result) => {
        expect(result).toBeTrue();
        done();
      });
    });

    it('should return false when order details are empty', (done) => {
      worldpayApmConnector.placeOrderRedirect.and.returnValue(of(order));
      spyOn(service, 'getOrderDetails').and.returnValue(of({}));

      service.placeRedirectOrder(mockParams).subscribe((result) => {
        expect(result).toBeFalse();
        done();
      });
    });

    it('should return false when order details are null when redirect', (done) => {
      worldpayApmConnector.placeOrderRedirect.and.returnValue(of(order));
      spyOn(service, 'getOrderDetails').and.returnValue(of(null));

      service.placeRedirectOrder(mockParams).subscribe((result) => {
        expect(result).toBeFalse();
        done();
      });
    });

    it('should handle error when placeRedirectOrderCommand fails', (done) => {
      const error = new Error('Failed to place redirect order');
      spyOn(service['placeRedirectOrderCommand'], 'execute').and.returnValue(throwError(() => error));
      spyOn(service, 'getOrderDetails').and.returnValue(of({}));

      service.placeRedirectOrder(mockParams).subscribe({
        error: (err) => {
          expect(err).toEqual(error);
          done();
        }
      });
    });
  });

  describe('placeBankTransferRedirectOrder', () => {
    it('should place bank transfer redirect order successfully and return true', (done) => {
      const orderId = 'orderId';
      const orderDetails: Order = { code: 'orderCode' };
      spyOn(service, 'getOrderDetails').and.returnValue(of(orderDetails));
      spyOn(service['placeBankTransferRedirectOrderCommand'], 'execute').and.returnValue(of(void 0));

      service.placeBankTransferRedirectOrder(orderId).subscribe((result) => {
        expect(result).toBeTrue();
        done();
      });
    });

    it('should return false when place bank transfer order details are empty', (done) => {
      const orderId = 'orderId';
      spyOn(service, 'getOrderDetails').and.returnValue(of({}));
      spyOn(service['placeBankTransferRedirectOrderCommand'], 'execute').and.returnValue(of(void 0));

      service.placeBankTransferRedirectOrder(orderId).subscribe((result) => {
        expect(result).toBeFalse();
        done();
      });
    });

    it('should return false when order details are null for bank transfer redirect', (done) => {
      const orderId = 'orderId';
      spyOn(service, 'getOrderDetails').and.returnValue(of(null));
      spyOn(service['placeBankTransferRedirectOrderCommand'], 'execute').and.returnValue(of(void 0));

      service.placeBankTransferRedirectOrder(orderId).subscribe((result) => {
        expect(result).toBeFalse();
        done();
      });
    });

    it('should handle error when placeBankTransferRedirectOrderCommand fails', (done) => {
      const orderId = 'orderId';
      const error = new Error('Failed to place bank transfer redirect order');
      spyOn(service['placeBankTransferRedirectOrderCommand'], 'execute').and.returnValue(throwError(() => error));
      spyOn(service, 'getOrderDetails').and.returnValue(of({}));

      service.placeBankTransferRedirectOrder(orderId).subscribe({
        error: (err) => {
          expect(err).toEqual(error);
          done();
        }
      });
    });
  });

  describe('startLoading', () => {
    it('should start loading spinner', () => {
      const vcr = {} as ViewContainerRef;
      launchDialogService.launch = createSpy('LaunchDialogService.launch').and.returnValue(of({} as ComponentRef<any>));

      service.startLoading(vcr);

      expect(launchDialogService.launch).toHaveBeenCalledWith(LAUNCH_CALLER.PLACE_ORDER_SPINNER, vcr);
      expect(service.placedOrder).toBeTruthy();
    });

    it('should handle null ViewContainerRef', () => {
      launchDialogService.launch = createSpy('LaunchDialogService.launch').and.returnValue(of({} as ComponentRef<any>));

      service.startLoading(null as unknown as ViewContainerRef);

      expect(launchDialogService.launch).toHaveBeenCalledWith(LAUNCH_CALLER.PLACE_ORDER_SPINNER, null);
      expect(service.placedOrder).toBeTruthy();
    });
  });

  describe('clearLoading', () => {
    it('should clear loading spinner when placedOrder is defined', () => {
      const componentRef = { destroy: jasmine.createSpy('destroy') } as unknown as ComponentRef<any>;
      service.placedOrder = of(componentRef);

      launchDialogService.clear = createSpy('LaunchDialogService.clear').and.callThrough();

      service.clearLoading();

      expect(launchDialogService.clear).toHaveBeenCalledWith(LAUNCH_CALLER.PLACE_ORDER_SPINNER);
      expect(componentRef.destroy).toHaveBeenCalled();
    });

    it('should not clear loading spinner when placedOrder is not defined', () => {
      service.placedOrder = undefined;

      launchDialogService.clear = createSpy('LaunchDialogService.clear').and.callThrough();

      service.clearLoading();

      expect(launchDialogService.clear).not.toHaveBeenCalled();
    });
  });
});
