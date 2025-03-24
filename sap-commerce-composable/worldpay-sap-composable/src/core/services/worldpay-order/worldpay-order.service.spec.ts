import { ComponentRef, ViewContainerRef } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Store, StoreModule } from '@ngrx/store';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CommandService, EventService, GlobalMessageService, GlobalMessageType, PaymentDetails, QueryState, RoutingService, UserIdService, WindowRef } from '@spartacus/core';
import { OrderAdapter, OrderConnector } from '@spartacus/order/core';
import { Order, OrderPlacedEvent } from '@spartacus/order/root';
import { LAUNCH_CALLER, LaunchDialogService } from '@spartacus/storefront';
import { MockActiveCartFacade } from '@worldpay-tests/services/active-cart.service.mock';
import { MockGlobalMessageService } from '@worldpay-tests/services/global-message.service.mock';
import { MockLaunchDialogService } from '@worldpay-tests/services/launch-dialog.service.mock';
import { MockUserIdService } from '@worldpay-tests/services/user-id.service.mock';
import { BehaviorSubject, Observable, ObservedValueOf, of, throwError } from 'rxjs';
import { WorldpayApmAdapter, WorldpayApmConnector } from '../../connectors';
import { WorldpayACHConnector } from '../../connectors/worldpay-ach/worldpay-ach.connector';
import { WorldpayConnector } from '../../connectors/worldpay.connector';
import { DDC3dsJwtSetEvent, InitialPaymentRequestSetEvent } from '../../events/checkout-payment.events';
import { ClearWorldpayACHPaymentFormEvent, ClearWorldpayPaymentDetailsEvent } from '../../events/worldpay.events';
import { ACHPaymentForm, ApmData, APMRedirectResponse, BrowserInfo, PaymentMethod, PlaceOrderResponse } from '../../interfaces';
import { WorldpayApmService } from '../worldpay-apm/worldpay-apm.service';
import { WorldpayCheckoutPaymentService } from '../worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayOrderService } from './worldpay-order.service';
import createSpy = jasmine.createSpy;

const userId = 'userId';
const cartId = 'cartId';
const order: Order = {
  code: '0001',
  guid: '0001',
};
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

class MockWorldpayCheckoutPaymentService implements Partial<WorldpayCheckoutPaymentService> {
  getPaymentDetailsState(): Observable<any> {
    return of({
      loading: false,
      error: false,
      data: mockPaymentDetails
    });
  }

  listenSetThreeDsDDCInfoEvent(): void {
  }
}

class MockWorldpayApmAdapter implements Partial<MockWorldpayApmAdapter> {
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

class MockRoutingService implements Partial<RoutingService> {
  go = createSpy().and.returnValue(of(true).toPromise());
}

const achPaymentForm: ACHPaymentForm = {
  accountType: 'checking',
  routingNumber: '123456789',
  accountNumber: '123456789',
  companyName: 'user',
};

describe('WorldpayOrderService', () => {
  let service: WorldpayOrderService;
  let activeCartFacade: ActiveCartFacade;
  let userIdService: UserIdService;
  let commandService: CommandService;
  let eventService: EventService;
  let store: Store;
  let globalMessageService: GlobalMessageService;
  let winRef: WindowRef;
  let worldpayApmService: WorldpayApmService;
  let worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService;
  let worldpayConnector: jasmine.SpyObj<WorldpayConnector>;
  let worldpayACHConnector: WorldpayACHConnector;
  let worldpayApmConnector: jasmine.SpyObj<WorldpayApmConnector>;
  let launchDialogService: LaunchDialogService;
  let routingService: RoutingService;

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

    getWorldpayAPMRedirectUrl(apm, save): Observable<APMRedirectResponse> {
      return of({
        mappingLabels: {},
        parameters: {
          entry: [],
        },
        postUrl: 'https://test.com'
      });
    }

    setWorldpayAPMRedirectUrlEvent(apmRedirect): void {

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

    showErrorMessage(error): void {
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
    const MockWorldpayConnector = jasmine.createSpyObj('WorldpayConnector', [
      'initialPaymentRequest',
      'getOrder',
      'getDDC3dsJwt',
    ]);

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
          useValue: MockWorldpayConnector
        },
        {
          provide: WorldpayApmService,
          useClass: MockWorldpayApmService
        },
        {
          provide: WorldpayCheckoutPaymentService,
          useClass: MockWorldpayCheckoutPaymentService
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
        }
      ]
    });
    service = TestBed.inject(WorldpayOrderService);
    activeCartFacade = TestBed.inject(ActiveCartFacade);
    userIdService = TestBed.inject(UserIdService);
    commandService = TestBed.inject(CommandService);
    eventService = TestBed.inject(EventService);
    store = TestBed.inject(Store);
    globalMessageService = TestBed.inject(GlobalMessageService);
    launchDialogService = TestBed.inject(LaunchDialogService);
    winRef = TestBed.inject(WindowRef);
    worldpayApmService = TestBed.inject(WorldpayApmService);
    worldpayCheckoutPaymentService = TestBed.inject(WorldpayCheckoutPaymentService);
    worldpayConnector = TestBed.inject(WorldpayConnector) as jasmine.SpyObj<WorldpayConnector>;
    worldpayACHConnector = TestBed.inject(WorldpayACHConnector);
    worldpayApmConnector = TestBed.inject(WorldpayApmConnector) as jasmine.SpyObj<WorldpayApmConnector>;
    routingService = TestBed.inject(RoutingService);

    worldpayConnector.initialPaymentRequest.and.returnValue(of({
      threeDSecureNeeded: false,
      threeDSecureInfo: 'info',
      transactionStatus: 'AUTHORISED',
      order: {
        code: '0001'
      }
    } as PlaceOrderResponse));
    worldpayConnector.getOrder.and.callFake(() => of(order));
    worldpayConnector.getDDC3dsJwt.and.returnValue(of({}));
    spyOn(eventService, 'dispatch').and.callThrough();
    spyOn(service, 'setPlacedOrder').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call executeDDC3dsJwtCommand', () => {
    worldpayConnector.getDDC3dsJwt.and.returnValue(of({
      ddcUrl: 'https://test.com',
      jwt: 'jwt'
    }));
    service.executeDDC3dsJwtCommand();
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
      browserInfo
    );

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
      browserInfo
    );
  });

  it('should call Challenge Accepted for registered user', () => {
    service.challengeAccepted({
      accepted: true,
      orderCode: '100'
    });
    expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
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
  });

  it('should call Challenge Accepted for anonymous user', () => {
    service.challengeAccepted({
      accepted: true,
      orderCode: '100',
      guestCustomer: true,
      customerID: 'test@email.com'
    });

    expect(worldpayConnector.getOrder).toHaveBeenCalledWith('test@email.com', '100', true);
    expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
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
    service.challengeFailed();
    expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.challengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
    expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
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
    let checkoutPreconditions = [];
    // @ts-ignore
    service.checkoutPreconditions().subscribe(response => checkoutPreconditions = response);
    expect(checkoutPreconditions).toEqual(['userId', 'cartId']);
  });

  it('should place ACH order successfully', (done) => {
    spyOn(worldpayACHConnector, 'placeACHOrder').and.returnValue(of(order));
    service.placeACHOrder(achPaymentForm).subscribe((response) => {
      expect(response).toEqual(order);
      done();
    });
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

      worldpayConnector.initialPaymentRequest.and.returnValue(of(response));
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

      worldpayConnector.initialPaymentRequest.and.returnValue(of(response));
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

    it('should add global message and clear loading when transaction fails', () => {
      const response = {
        threeDSecureNeeded: false,
        threeDSecureInfo: {},
        transactionStatus: 'FAILED',
        order: {}
      } as PlaceOrderResponse;

      worldpayConnector.initialPaymentRequest.and.returnValue(of(response));
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
  });

  describe('getDDC3dsJwt', () => {
    it('should dispatch DDC3dsJwtSetEvent with ddcInfo when getDDC3dsJwt is successful', () => {
      const ddcInfo = {
        ddcUrl: 'https://test.com',
        jwt: 'jwt'
      };
      worldpayConnector.getDDC3dsJwt.and.returnValue(of(ddcInfo));

      service['getDDC3dsJwtCommand'].execute(undefined).subscribe();

      expect(eventService.dispatch).toHaveBeenCalledWith({ ddcInfo }, DDC3dsJwtSetEvent);
    });

    it('should handle error when getDDC3dsJwt fails', () => {
      const error = new Error('Failed to get DDC3dsJwt');
      worldpayConnector.getDDC3dsJwt.and.returnValue(throwError(() => error));

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

      worldpayConnector.getOrder.and.returnValue(of(order));

      service['challengeAcceptedCommand'].execute(response).subscribe();

      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        order,
        cartCode: cartId
      }, OrderPlacedEvent);
      expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
    });

    it('should dispatch OrderPlacedEvent and ClearWorldpayPaymentDetailsEvent when challenge is accepted for guest user', () => {
      const response = {
        customerID: 'guestId',
        orderCode: 'orderCode',
        guestCustomer: true
      };

      worldpayConnector.getOrder.and.returnValue(of(order));

      service['challengeAcceptedCommand'].execute(response).subscribe();

      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        order,
        cartCode: cartId
      }, OrderPlacedEvent);
      expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
    });

    it('should handle error when getOrder fails', () => {
      const response = {
        customerID: 'userId',
        orderCode: 'orderCode',
        guestCustomer: false
      };

      const error = new Error('Failed to get order');
      worldpayConnector.getOrder.and.returnValue(throwError(() => error));

      service['challengeAcceptedCommand'].execute(response).subscribe({
        error: (err) => {
          expect(err).toEqual(error);
        }
      });

      expect(eventService.dispatch).not.toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
    });
  });

  describe('placeRedirectOrderCommand', () => {
    it('should place redirect order successfully', () => {
      worldpayApmConnector.placeOrderRedirect.and.returnValue(of(order));

      service['placeRedirectOrderCommand'].execute().subscribe();

      expect(worldpayApmConnector.placeOrderRedirect).toHaveBeenCalledWith(userId, cartId);
      expect(service.setPlacedOrder).toHaveBeenCalledWith(order);
      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        cartCode: cartId,
        order,
      }, OrderPlacedEvent);
      expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
    });

    it('should handle error when placing redirect order fails', () => {
      const error = new Error('Failed to place redirect order');
      worldpayApmConnector.placeOrderRedirect.and.returnValue(throwError(() => error));

      service['placeRedirectOrderCommand'].execute().subscribe({
        error: (err) => {
          expect(err).toEqual(error);
        }
      });

      expect(service.setPlacedOrder).not.toHaveBeenCalled();
      expect(eventService.dispatch).not.toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
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
      expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
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
      expect(eventService.dispatch).not.toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
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

      worldpayConnector.getOrder.and.returnValue(of(order));

      service.challengeAccepted(worldpayChallengeResponse);

      expect(worldpayConnector.getOrder).toHaveBeenCalledWith('userId', 'orderCode', false);
      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        order,
        cartCode: cartId
      }, OrderPlacedEvent);
      expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
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

      worldpayConnector.getOrder.and.returnValue(of(order));

      service.challengeAccepted(worldpayChallengeResponse);

      expect(worldpayConnector.getOrder).toHaveBeenCalledWith('guestId', 'orderCode', true);
      expect(eventService.dispatch).toHaveBeenCalledWith({
        userId,
        cartId,
        order,
        cartCode: cartId
      }, OrderPlacedEvent);
      expect(eventService.dispatch).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
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
      spyOn(console, 'error');
      worldpayConnector.getOrder.and.returnValue(throwError(() => error));

      service.challengeAccepted(worldpayChallengeResponse);

      expect(worldpayConnector.getOrder).toHaveBeenCalledWith('userId', 'orderCode', false);
      expect(service.setPlacedOrder).not.toHaveBeenCalled();
      expect(routingService.go).not.toHaveBeenCalled();
      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.challengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(console.error).toHaveBeenCalledWith('Challenge Failed:', error);
      done();
    });
  });

  describe('placeOrderRedirect', () => {
    it('should place redirect order successfully and return true', (done) => {
      const orderDetails: Order = { code: 'orderCode' };
      worldpayApmConnector.placeOrderRedirect.and.returnValue(of(order));
      spyOn(service, 'getOrderDetails').and.returnValue(of(orderDetails));

      service.placeRedirectOrder().subscribe((result) => {
        expect(result).toBeTrue();
        done();
      });
    });

    it('should return false when order details are empty', (done) => {
      worldpayApmConnector.placeOrderRedirect.and.returnValue(of(order));
      spyOn(service, 'getOrderDetails').and.returnValue(of({}));

      service.placeRedirectOrder().subscribe((result) => {
        expect(result).toBeFalse();
        done();
      });
    });

    it('should return false when order details are null', (done) => {
      worldpayApmConnector.placeOrderRedirect.and.returnValue(of(order));
      spyOn(service, 'getOrderDetails').and.returnValue(of(null));

      service.placeRedirectOrder().subscribe((result) => {
        expect(result).toBeFalse();
        done();
      });
    });

    it('should handle error when placeRedirectOrderCommand fails', (done) => {
      const error = new Error('Failed to place redirect order');
      spyOn(service['placeRedirectOrderCommand'], 'execute').and.returnValue(throwError(() => error));
      spyOn(service, 'getOrderDetails').and.returnValue(of({}));

      service.placeRedirectOrder().subscribe({
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

    it('should return false when order details are empty', (done) => {
      const orderId = 'orderId';
      spyOn(service, 'getOrderDetails').and.returnValue(of({}));
      spyOn(service['placeBankTransferRedirectOrderCommand'], 'execute').and.returnValue(of(void 0));

      service.placeBankTransferRedirectOrder(orderId).subscribe((result) => {
        expect(result).toBeFalse();
        done();
      });
    });

    it('should return false when order details are null', (done) => {
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
      spyOn(launchDialogService, 'launch').and.returnValue(of({} as ComponentRef<any>));

      service.startLoading(vcr);

      expect(launchDialogService.launch).toHaveBeenCalledWith(LAUNCH_CALLER.PLACE_ORDER_SPINNER, vcr);
      expect(service.placedOrder).toBeTruthy();
    });

    it('should handle null ViewContainerRef', () => {
      spyOn(launchDialogService, 'launch').and.returnValue(of({} as ComponentRef<any>));

      service.startLoading(null as unknown as ViewContainerRef);

      expect(launchDialogService.launch).toHaveBeenCalledWith(LAUNCH_CALLER.PLACE_ORDER_SPINNER, null);
      expect(service.placedOrder).toBeTruthy();
    });
  });

  describe('clearLoading', () => {
    it('should clear loading spinner when placedOrder is defined', () => {
      const componentRef = { destroy: jasmine.createSpy('destroy') } as unknown as ComponentRef<any>;
      service.placedOrder = of(componentRef);

      spyOn(launchDialogService, 'clear').and.callThrough();

      service.clearLoading();

      expect(launchDialogService.clear).toHaveBeenCalledWith(LAUNCH_CALLER.PLACE_ORDER_SPINNER);
      expect(componentRef.destroy).toHaveBeenCalled();
    });

    it('should not clear loading spinner when placedOrder is not defined', () => {
      service.placedOrder = undefined;

      spyOn(launchDialogService, 'clear').and.callThrough();

      service.clearLoading();

      expect(launchDialogService.clear).not.toHaveBeenCalled();
    });
  });
});
