import { TestBed } from '@angular/core/testing';

import { WorldpayGooglepayService } from './worldpay-googlepay.service';
import { Address, CommandService, EventService, GlobalMessageService, UserIdService } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { GooglePayMerchantConfiguration, GooglePayPaymentRequest, PlaceOrderResponse } from '../../interfaces';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { WorldpayOrderService } from '../worldpay-order/worldpay-order.service';
import { WorldpayGooglePayConnector } from '../../connectors/worldpay-googlepay/worldpay-googlepay.connector';
import { GooglePayMerchantConfigurationSetEvent } from '../../events/googlepay.events';
import createSpy = jasmine.createSpy;

describe('WorldpayGooglepayService', () => {
  let service: WorldpayGooglepayService;
  let activeCartService: ActiveCartFacade;
  let userIdService: UserIdServiceStub;
  let worldpayGooglepayConnector: WorldpayGooglePayConnector;
  let worldpayOrderService: WorldpayOrderService;
  let globalMessageService: GlobalMessageService;
  let eventService: EventService;

  const userId = 'testUserId';
  const cartId = 'testCartId';
  const mockGooglePayMerchantConfiguration: GooglePayMerchantConfiguration = {
    merchantId: 'testMerchantId',
    cardType: 'VISA'
  };
  const mockBillingAddress: Address = {
    firstName: 'John',
    lastName: 'Smith',
    line1: 'Buckingham Street 5',
    line2: '1A',
    phone: '(+11) 111 111 111',
    postalCode: 'MA8902',
    town: 'London',
    country: {
      name: 'test-country-name',
      isocode: 'UK',
    },
    formattedAddress: 'test-formattedAddress',
  };

  class ActiveCartServiceStub {
    cartId = cartId;

    public takeActiveCartId() {
      return of(this.cartId);
    }

    public isGuestCart() {
      return of(false);
    }
  }

  class UserIdServiceStub {
    takeUserId(): Observable<string> {
      return of(userId);
    }
  }

  class MockWorldpayOrderService implements Partial<WorldpayOrderService> {
    setPlacedOrder() {

    }
  }

  class MockGlobalMessageService implements Partial<GlobalMessageService> {
    add = createSpy();
  }

  class MockWorldpayGooglePayConnector implements Partial<WorldpayGooglePayConnector> {
    getGooglePayMerchantConfiguration(): Observable<GooglePayMerchantConfiguration> {
      return of(mockGooglePayMerchantConfiguration);
    }

    authoriseGooglePayPayment(): Observable<PlaceOrderResponse> {
      return of({ order: { code: '0001' } });
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        WorldpayGooglepayService,
        CommandService,
        EventService,
        {
          provide: WorldpayOrderService,
          useClass: MockWorldpayOrderService
        },
        {
          provide: GlobalMessageService,
          useClass: MockGlobalMessageService
        },
        {
          provide: WorldpayGooglePayConnector,
          useClass: MockWorldpayGooglePayConnector
        },
        {
          provide: ActiveCartFacade,
          useClass: ActiveCartServiceStub
        },
        {
          provide: UserIdService,
          useClass: UserIdServiceStub
        }
      ]
    });
    service = TestBed.inject(WorldpayGooglepayService);
    activeCartService = TestBed.inject(ActiveCartFacade);
    userIdService = TestBed.inject(UserIdService);
    worldpayGooglepayConnector = TestBed.inject(WorldpayGooglePayConnector);
    worldpayOrderService = TestBed.inject(WorldpayOrderService);
    globalMessageService = TestBed.inject(GlobalMessageService);
    eventService = TestBed.inject(EventService);

    spyOn(eventService, 'dispatch').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should request merchant configuration', () => {
    spyOn(worldpayGooglepayConnector, 'getGooglePayMerchantConfiguration').and.callThrough();
    service.requestMerchantConfiguration();

    expect(worldpayGooglepayConnector.getGooglePayMerchantConfiguration).toHaveBeenCalledWith(userId, cartId);
    expect(eventService.dispatch).toHaveBeenCalledWith(
      {
        googlePayMerchantConfiguration: mockGooglePayMerchantConfiguration
      }, GooglePayMerchantConfigurationSetEvent
    );
  });

  it('should request authorise order', () => {
    spyOn(worldpayGooglepayConnector, 'authoriseGooglePayPayment').and.callThrough();
    const paymentRequest: GooglePayPaymentRequest = {
      apiVersion: 2,
      apiVersionMinor: 0,
      paymentMethodData: {
        info: { billingAddress: { name: 'first last' } },
        tokenizationData: {
          token: '{"json": "this is a test"}'
        }
      }
    };

    service.authoriseOrder(paymentRequest, true);

    expect(worldpayGooglepayConnector.authoriseGooglePayPayment).toHaveBeenCalledWith(
      'testUserId', 'testCartId', { 'json': 'this is a test' }, { name: 'first last' }, true
    );
  });
});
