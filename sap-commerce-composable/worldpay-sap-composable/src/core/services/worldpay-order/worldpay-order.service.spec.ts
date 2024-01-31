import { TestBed } from '@angular/core/testing';

import { WorldpayOrderService } from './worldpay-order.service';
import { ActiveCartFacade, PaymentDetails } from '@spartacus/cart/base/root';
import { CommandService, EventService, GlobalMessageService, GlobalMessageType, QueryState, RoutingService, UserIdService, WindowRef } from '@spartacus/core';
import { OrderAdapter, OrderConnector } from '@spartacus/order/core';
import { Store, StoreModule } from '@ngrx/store';
import { WorldpayApmService } from '../worldpay-apm/worldpay-apm.service';
import { WorldpayCheckoutPaymentService } from '../worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayConnector } from '../../connectors/worldpay.connector';
import { BehaviorSubject, NEVER, Observable, ObservedValueOf, of } from 'rxjs';
import { ApmData, APMRedirectResponse, PaymentMethod, ThreeDsDDCInfo } from '../../interfaces';
import { ClearWorldpayPaymentDetailsEvent } from '../../events/worldpay.events';
import { Order, OrderPlacedEvent } from '@spartacus/order/root';
import { DDC3dsJwtSetEvent } from '../../events/checkout-payment.events';
import { LaunchDialogService } from '@spartacus/storefront';
import { WorldpayApmAdapter } from '../../connectors';
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

class MockUserIdService implements Partial<UserIdService> {
  takeUserId = createSpy().and.returnValue(of(userId));
}

class MockActiveCartFacade implements Partial<ActiveCartFacade> {
  takeActiveCartId = createSpy('takeActiveCartId').and.returnValue(of(cartId));
  isGuestCart = createSpy('isGuestCart').and.returnValue(of(false));
}

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

class MockWorldpayConnector implements Partial<WorldpayConnector> {
  initialPaymentRequest = createSpy('initialPaymentRequest').and.returnValue(of({
    threeDSecureNeeded: false,
    threeDSecureInfo: 'info',
    transactionStatus: 'AUTHORISED',
    order: {
      code: '0001'
    }
  }));

  getDDC3dsJwt(): Observable<ThreeDsDDCInfo> {
    return of({});
  }

  getOrder = createSpy('WorldpayAdapter.getOrder').and.callFake(() => of(order));

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

class MockGlobalMessageService {
  add = createSpy('add').and.callThrough();
}

class MockLaunchDialogService implements Partial<LaunchDialogService> {
  dialogClose: Observable<any> = NEVER;

  openDialogAndSubscribe(): void {
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
  let worldpayConnector: WorldpayConnector;
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

  beforeEach(() => {
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
    worldpayConnector = TestBed.inject(WorldpayConnector);
    routingService = TestBed.inject(RoutingService);

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
      null
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
      null
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
});
