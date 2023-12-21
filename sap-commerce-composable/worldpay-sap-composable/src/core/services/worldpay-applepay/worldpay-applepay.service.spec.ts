import { TestBed } from '@angular/core/testing';

import { WorldpayApplepayService } from './worldpay-applepay.service';
import { StoreModule } from '@ngrx/store';
import { CommandService, EventService, GlobalMessageService, QueryService, UserIdService } from '@spartacus/core';
import { of } from 'rxjs';
import { WorldpayApplepayAdapter } from '../../connectors/worldpay-applepay/worldpay-applepay.adapter';
import { OrderFacade } from '@spartacus/order/root';
import { WorldpayApplepayConnector } from '../../connectors/worldpay-applepay/worldpay-applepay.connector';
import { ApplePayAuthorizePaymentEvent, ApplePayMerchantSessionEvent, RequestApplePayPaymentRequestEvent } from '../../events/applepay.events';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import createSpy = jasmine.createSpy;

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
    takeUserId = createSpy('takeUserId').and.returnValue(of(userId));
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
      service.onPaymentAuthorized(event);
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
  });
});
