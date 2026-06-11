import { TestBed } from '@angular/core/testing';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CommandService, EventService, GlobalMessageService, LoggerService, UserIdService } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { mockUserId } from 'worldpay-sap-composable-tests';
import { WorldpayGooglePayConnector } from '../../connectors/worldpay-googlepay/worldpay-googlepay.connector';
import { GooglePayMerchantConfigurationSetEvent } from '../../events/googlepay.events';
import { WorldpayOrderFacade } from '../../facade';
import { PlaceOrderResponse } from '../../interfaces';
import { GooglePayMerchantConfiguration, GooglePayPaymentResponse } from '../../models';
import { WorldpayOrderService } from '../worldpay-order/worldpay-order.service';

import { WorldpayGooglepayService } from './worldpay-googlepay.service';
import createSpy = jasmine.createSpy;

describe('WorldpayGooglepayService', () => {
  let service: WorldpayGooglepayService;
  let worldpayGooglepayConnector: WorldpayGooglePayConnector;
  let eventService: EventService;

  const userId = mockUserId;
  const cartId = 'testCartId';
  const mockGooglePayMerchantConfiguration: GooglePayMerchantConfiguration = {
    merchantId: 'testMerchantId',
    cardType: 'VISA'
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
          provide: WorldpayOrderFacade,
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
        },
        LoggerService,
      ]
    });
    service = TestBed.inject(WorldpayGooglepayService);
    worldpayGooglepayConnector = TestBed.inject(WorldpayGooglePayConnector);
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
    const paymentRequest: GooglePayPaymentResponse = {
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
      mockUserId, 'testCartId', { 'json': 'this is a test' }, { name: 'first last' }, true
    );
  });
});
