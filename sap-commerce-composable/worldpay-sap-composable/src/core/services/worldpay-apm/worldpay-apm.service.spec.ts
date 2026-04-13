import { APP_BASE_HREF } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { StoreModule } from '@ngrx/store';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CheckoutPaymentDetailsSetEvent } from '@spartacus/checkout/base/root';
import { CmsService, ConverterService, EventService, GlobalMessageService, GlobalMessageType, LoggerService, OCC_USER_ID_ANONYMOUS, UserIdService } from '@spartacus/core';
import { OrderDetailsService } from '@spartacus/order/components';
import { OrderPlacedEvent } from '@spartacus/order/root';
import { LaunchDialogService } from '@spartacus/storefront';
import { NEVER, Observable, of, throwError } from 'rxjs';
import { mockUserId, MockWorldpayCheckoutPaymentService } from 'worldpay-sap-composable-tests';
import { WorldpayApmConnector, WorldpayConnector } from '../../connectors';
import {
  ClearWorldpayPaymentDetailsEvent,
  SelectWorldpayAPMEvent,
  SetWorldpayAPMRedirectResponseEvent,
  SetWorldpaySaveAsDefaultCreditCardEvent,
  SetWorldpaySavedCreditCardEvent
} from '../../events';
import { ApmPaymentDetails, APMRedirectResponse, PaymentMethod } from '../../interfaces';
import { WorldpayCheckoutPaymentService } from '../worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayOrderService } from '../worldpay-order/worldpay-order.service';

import { WorldpayApmService } from './worldpay-apm.service';
import createSpy = jasmine.createSpy;

const apm = {
  code: PaymentMethod.Card,
  name: 'credit'
};

const userId = mockUserId;
const cartId = '0000000';

class MockCmsService {
  getComponentData = createSpy('getComponentData').and.returnValue(of(apm));
}

class MockUserIdService {
  takeUserId() {
    return of(userId);
  }
}

class MockActiveCartFacade {
  takeActiveCartId() {
    return of(cartId);
  }

  isGuestCart() {
    return of(false);
  }
}

class CmsServiceStub {
  getComponentData(uid, code) {
    return of({
      name,
      code
    });
  }

}

class MockOrderDetailsService {
  getOrderDetails() {
    return of(null);
  }
}

class MockWorldpayApmConnector implements Partial<WorldpayApmConnector> {
  getAvailableApms() {
    return of([
      { code: PaymentMethod.Card },
      { code: PaymentMethod.GooglePay }
    ]);
  }

  authoriseApmRedirect(): Observable<APMRedirectResponse> {
    return of({
      postUrl: 'https://test.com',
      mappingLabels: {},
      parameters: {
        entry: []
      }
    });
  }

  setAPMPaymentInfo(): Observable<any> {
    return of({});
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

class MockWorldpayOrderService implements Partial<WorldpayOrderService> {
  clearLoading(): void {
  }
}

class MockWorldpayConnector implements Partial<WorldpayConnector> {

}

describe('WorldpayApmService', () => {
  let service: WorldpayApmService;
  let converterService: ConverterService;
  let eventService: EventService;
  let worldpayApmConnector: WorldpayApmConnector;
  let worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService;
  let worldpayOrderService: WorldpayOrderService;
  let globalMessageService: GlobalMessageService;
  let userIdService: UserIdService;
  let activeCartFacade: ActiveCartFacade;
  let logger: LoggerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
      providers: [
        EventService,
        LoggerService,
        {
          provide: CmsService,
          useClass: MockCmsService,
        },
        {
          provide: UserIdService,
          useClass: MockUserIdService,
        },
        {
          provide: ActiveCartFacade,
          useClass: MockActiveCartFacade,
        },
        {
          provide: CmsService,
          useClass: CmsServiceStub
        },
        {
          provide: OrderDetailsService,
          useClass: MockOrderDetailsService
        },
        {
          provide: WorldpayApmConnector,
          useClass: MockWorldpayApmConnector
        },
        {
          provide: GlobalMessageService,
          useClass: MockGlobalMessageService
        },
        {
          provide: LaunchDialogService,
          useClass: MockLaunchDialogService
        },
        {
          provide: APP_BASE_HREF,
          useValue: '/spartacus/'
        },
        {
          provide: WorldpayOrderService,
          useClass: MockWorldpayOrderService
        },
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector
        },
        {
          //useClass: MockWorldpayCheckoutPaymentService
          provide: WorldpayCheckoutPaymentService,
          useFactory: (sanitizer: DomSanitizer) => new MockWorldpayCheckoutPaymentService(sanitizer),
          deps: [DomSanitizer]
        },
      ]
    });
    service = TestBed.inject(WorldpayApmService);
    logger = TestBed.inject(LoggerService);
    converterService = TestBed.inject(ConverterService);
    eventService = TestBed.inject(EventService);
    worldpayApmConnector = TestBed.inject(WorldpayApmConnector);
    worldpayCheckoutPaymentService = TestBed.inject(WorldpayCheckoutPaymentService);
    worldpayOrderService = TestBed.inject(WorldpayOrderService);
    globalMessageService = TestBed.inject(GlobalMessageService);
    userIdService = TestBed.inject(UserIdService);
    activeCartFacade = TestBed.inject(ActiveCartFacade);

    spyOn(converterService, 'pipeable').and.callThrough();
    spyOn(eventService, 'dispatch').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('setApmPaymentDetailsCommand$', () => {
    it('should set APM payment details and dispatch CheckoutPaymentDetailsSetEvent', (done) => {
      const mockApmPaymentDetails: ApmPaymentDetails = { code: PaymentMethod.GooglePay };
      const mockCart = { apmCode: 'googlePay' };
      spyOn(worldpayApmConnector, 'setAPMPaymentInfo').and.returnValue(of(mockCart));

      service.setApmPaymentDetails(mockApmPaymentDetails).subscribe((response) => {
        expect(response).toEqual(mockCart);
        expect(service['selectedApm$'].value).toEqual(mockApmPaymentDetails);
        expect(eventService.dispatch).toHaveBeenCalledWith(
          {
            userId,
            cartId,
            paymentDetailsId: mockCart.apmCode,
            cartCode: cartId
          },
          CheckoutPaymentDetailsSetEvent
        );
        done();
      });
    });

    it('should handle error when setting APM payment details fails', (done) => {
      const mockApmPaymentDetails: ApmPaymentDetails = { code: PaymentMethod.GooglePay };
      const errorResponse = new Error('Error setting APM payment details');
      spyOn(worldpayApmConnector, 'setAPMPaymentInfo').and.returnValue(throwError(() => errorResponse));

      service.setApmPaymentDetails(mockApmPaymentDetails).subscribe({
        error: (error) => {
          expect(error).toEqual(errorResponse);
          done();
        }
      });
    });
  });

  describe('checkoutPreconditions', () => {
    it('should emit userId and cartId when conditions are met', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(userId));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of(cartId));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe(([emittedUserId, emittedCartId]) => {
        expect(emittedUserId).toEqual(userId);
        expect(emittedCartId).toEqual(cartId);
        done();
      });
    });

    it('should throw an error when userId is not available', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(null));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of(cartId));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe({
        error: (error) => {
          expect(error.message).toEqual('Checkout conditions not met');
          done();
        }
      });
    });

    it('should throw an error when cartId is not available', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(userId));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of(null));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe({
        error: (error) => {
          expect(error.message).toEqual('Checkout conditions not met');
          done();
        }
      });
    });

    it('should throw an error when userId is anonymous and cart is not a guest cart', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(OCC_USER_ID_ANONYMOUS));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of(cartId));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe({
        error: (error) => {
          expect(error.message).toEqual('Checkout conditions not met');
          done();
        }
      });
    });

    it('should emit userId and cartId when userId is anonymous and cart is a guest cart', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(OCC_USER_ID_ANONYMOUS));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of(cartId));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(true));

      service.checkoutPreconditions().subscribe(([emittedUserId, emittedCartId]) => {
        expect(emittedUserId).toEqual(OCC_USER_ID_ANONYMOUS);
        expect(emittedCartId).toEqual(cartId);
        done();
      });
    });
  });

  describe('getLoading', () => {
    it('should emit true when loading state is true', (done) => {
      spyOn(service, 'getWorldpayAvailableApmsLoading').and.returnValue(of(true));

      service.getLoading().subscribe((loading) => {
        expect(loading).toBeTrue();
        done();
      });
    });

    it('should emit false when loading state is false', (done) => {
      spyOn(service, 'getWorldpayAvailableApmsLoading').and.returnValue(of(false));

      service.getLoading().subscribe((loading) => {
        expect(loading).toBeFalse();
        done();
      });
    });
  });

  describe('getAPMRedirectUrl', () => {
    it('should retrieve and set the APM redirect URL', (done) => {
      const mockApm = { code: PaymentMethod.Card };
      const mockResponse: APMRedirectResponse = {
        postUrl: 'https://test.com',
        mappingLabels: {},
        parameters: {
          entry: [{
            key: 'url',
            value: 'https://test.com'
          }]
        }
      };
      spyOn(service, 'getWorldpayAPMRedirectUrl').and.returnValue(of(mockResponse));
      spyOn(service, 'setWorldpayAPMRedirectUrlEvent').and.callThrough();

      service.getAPMRedirectUrl(mockApm, false);

      service.getWorldpayAPMRedirectUrl(mockApm, false).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        expect(service.setWorldpayAPMRedirectUrlEvent).toHaveBeenCalledWith(mockResponse);
        done();
      });
    });

    it('should handle error when retrieving APM redirect URL fails', (done) => {
      const mockApm = { code: PaymentMethod.Card };
      const errorResponse = new Error('Error retrieving APM redirect URL');
      spyOn(service, 'getWorldpayAPMRedirectUrl').and.returnValue(throwError(() => errorResponse));
      spyOn(worldpayOrderService, 'clearLoading').and.callThrough();
      spyOn(logger, 'error');
      spyOn(service, 'showErrorMessage').and.callThrough();

      service.getAPMRedirectUrl(mockApm, false);

      service.getWorldpayAPMRedirectUrl(mockApm, false).subscribe({
        error: (error) => {
          expect(error).toEqual(errorResponse);
          expect(worldpayOrderService.clearLoading).toHaveBeenCalled();
          expect(logger.error).toHaveBeenCalledWith('WorldpayApmService getAPMRedirectUrl error', error);
          expect(service.showErrorMessage).toHaveBeenCalledWith(errorResponse);
          done();
        }
      });
    });
  });

  it('should emit the latest selected APM payment details whenever it changes', () => {
    const selectedApm1: ApmPaymentDetails = { code: PaymentMethod.GooglePay };
    const selectedApm2: ApmPaymentDetails = { code: PaymentMethod.Card };
    const selectedApm3: ApmPaymentDetails = { code: PaymentMethod.PayPal };
    const selectedApmList: ApmPaymentDetails[] = [selectedApm1, selectedApm2, selectedApm3];
    const emittedApmList: ApmPaymentDetails[] = [];
    service.getSelectedAPMFromState().subscribe((apm: ApmPaymentDetails) => {
      if (apm) {
        emittedApmList.push(apm);
      }
    });

    selectedApmList.forEach((apm: ApmPaymentDetails) => {
      service.selectAPM(apm);
    });

    expect(emittedApmList).toEqual(selectedApmList);
  });

  it('should work correctly when there is no selected APM payment details', () => {
    service.getSelectedAPMFromState().subscribe((apm: ApmPaymentDetails) => {
      expect(apm).toBeNull();
    });
  });

  it('should get apm by id', (done) => {
    const uid = 'cc-component';

    service.getApmComponentById(uid, PaymentMethod.Card)
      .subscribe((result) => {
        expect(result.code).toEqual(PaymentMethod.Card);
        done();
      });
  });

  it('should get getSelectedAPMEvent', () => {
    spyOn(eventService, 'get').and.returnValue(of(apm));
    service.getSelectedAPMEvent().subscribe();
    expect(eventService.get).toHaveBeenCalledWith(SelectWorldpayAPMEvent);
  });

  it('should get Worldpay APM Redirect Url', () => {
    spyOn(worldpayApmConnector, 'authoriseApmRedirect').and.callThrough();
    spyOn(service, 'setWorldpayAPMRedirectUrl').and.callThrough();

    let apmRedirect = null;

    service.getWorldpayAPMRedirectUrl(apm, false).subscribe((result) => {
      apmRedirect = result;
    });

    expect(apmRedirect).toEqual({
      postUrl: 'https://test.com',
      mappingLabels: {},
      parameters: {
        entry: []
      }
    });

    expect(eventService.dispatch).toHaveBeenCalledWith({
      userId,
      cartId,
      apmRedirect
    }, ClearWorldpayPaymentDetailsEvent);
  });

  it('should set Worldpay Apm Redirect Url Event', () => {
    const response: APMRedirectResponse = {
      postUrl: 'https://test.com',
      mappingLabels: {},
      parameters: {
        entry: []
      }
    };
    service.setWorldpayAPMRedirectUrlEvent(response);
    expect(eventService.dispatch).toHaveBeenCalledWith({ apmRedirectUrl: response }, SetWorldpayAPMRedirectResponseEvent);
  });

  it('should get Worldpay Apm Redirect Url from state', () => {
    spyOn(eventService, 'get').and.returnValue(of({
      apmRedirectUrl: {
        mappingLabels: 'mapping',
        parameters: {
          entry: []
        },
        postUrl: 'https://test.com'
      }
    }));
    let redirectUrl = null;
    service.getWorldpayAPMRedirectUrlFromState().subscribe((result) => {
      redirectUrl = result;
    });

    expect(eventService.get).toHaveBeenCalledWith(SetWorldpayAPMRedirectResponseEvent);
    expect(redirectUrl).toEqual({
      mappingLabels: 'mapping',
      parameters: {
        entry: []
      },
      postUrl: 'https://test.com'
    });

  });

  it('should dispatch SetSelectedApm event', () => {
    service.selectAPM(apm);
    expect(eventService.dispatch).toHaveBeenCalledWith({ apm }, SelectWorldpayAPMEvent);
    service.getSelectedAPMFromState().subscribe((result) => {
      expect(result).toEqual(apm);
    });
  });

  it('should get Available Apms state', () => {
    let availableApms = null;
    service.requestAvailableApmsState().subscribe((result) => {
      availableApms = result;
    });

    expect(availableApms).toEqual({
      loading: false,
      error: false,
      data: [
        { code: PaymentMethod.Card },
        { code: PaymentMethod.GooglePay }
      ]
    });
  });

  it('should get Available Apms loading state', () => {
    let availableApms = null;
    service.getWorldpayAvailableApmsLoading().subscribe((result) => {
      availableApms = result;
    });

    expect(availableApms).toBeFalse();
  });

  it('should get Available Apms', () => {
    let availableApms = null;
    service.getWorldpayAvailableApms().subscribe((result) => {
      availableApms = result;
    });

    expect(availableApms).toEqual([
      { code: PaymentMethod.Card },
      { code: PaymentMethod.GooglePay }
    ]);
  });

  it('should get requestAvailableApms event', () => {
    spyOn(worldpayApmConnector, 'getAvailableApms').and.callThrough();
    service.requestAvailableApmsState()
      .subscribe(result => expect(result.data).toEqual([
        { code: PaymentMethod.Card },
        { code: PaymentMethod.GooglePay }
      ])
      )
      .unsubscribe();

    expect(worldpayApmConnector.getAvailableApms).toHaveBeenCalledWith(userId, cartId);
  });

  it('should get APM Redirect URL', () => {
    spyOn(service, 'setWorldpayAPMRedirectUrlEvent').and.callThrough();
    let response = null;
    service.getAPMRedirectUrl(apm, false);
    service.getWorldpayAPMRedirectUrl(apm, false).subscribe((result) => {
      response = result;
    });
    expect(service.setWorldpayAPMRedirectUrlEvent).toHaveBeenCalledWith(response);
  });

  it('should show error message when place Worldpay order failed', () => {
    spyOn(logger, 'error');
    const error = {
      details: [{
        message: 'error'
      }]
    };
    spyOn(service, 'getWorldpayAPMRedirectUrl').and.returnValue(throwError(() => error));
    spyOn(worldpayOrderService, 'clearLoading').and.callThrough();
    service.getAPMRedirectUrl(apm, false);
    expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'error' }, GlobalMessageType.MSG_TYPE_ERROR);
    expect(worldpayOrderService.clearLoading).toHaveBeenCalled();
    expect(logger.error).toHaveBeenCalledWith('WorldpayApmService getAPMRedirectUrl error', error);
  });

  describe('setWorldpaySavedCreditCardEvent', () => {
    it('should update save credit card value when event is received', (done) => {
      const mockEvent: SetWorldpaySavedCreditCardEvent = { saved: true };
      spyOn(worldpayCheckoutPaymentService, 'setSaveCreditCardValue').and.callThrough();
      spyOn(eventService, 'get').and.returnValue(of(mockEvent));

      service.setWorldpaySavedCreditCardEvent().subscribe(() => {
        expect(worldpayCheckoutPaymentService.setSaveCreditCardValue).toHaveBeenCalledWith(mockEvent.saved);
        done();
      });
    });

    it('should handle error when setting save credit card value fails', (done) => {
      const mockEvent: SetWorldpaySavedCreditCardEvent = { saved: true };
      const errorResponse = new Error('Error setting save credit card value');
      spyOn(worldpayCheckoutPaymentService, 'setSaveCreditCardValue').and.throwError(errorResponse);
      spyOn(eventService, 'get').and.returnValue(of(mockEvent));

      service.setWorldpaySavedCreditCardEvent().subscribe({
        error: (error) => {
          expect(error).toEqual(errorResponse);
          done();
        }
      });
    });
  });

  describe('setWorldpaySaveAsDefaultCreditCardEvent', () => {
    it('should update save as default card value when event is received', (done) => {
      const mockEvent: SetWorldpaySaveAsDefaultCreditCardEvent = { saved: true };
      spyOn(worldpayCheckoutPaymentService, 'setSaveAsDefaultCardValue').and.callThrough();
      spyOn(eventService, 'get').and.returnValue(of(mockEvent));

      service.setWorldpaySaveAsDefaultCreditCardEvent().subscribe(() => {
        expect(worldpayCheckoutPaymentService.setSaveAsDefaultCardValue).toHaveBeenCalledWith(mockEvent.saved);
        done();
      });
    });

    it('should handle error when setting save as default card value fails', (done) => {
      const mockEvent: SetWorldpaySaveAsDefaultCreditCardEvent = { saved: true };
      const errorResponse = new Error('Error setting save as default card value');
      spyOn(worldpayCheckoutPaymentService, 'setSaveAsDefaultCardValue').and.throwError(errorResponse);
      spyOn(eventService, 'get').and.returnValue(of(mockEvent));

      service.setWorldpaySaveAsDefaultCreditCardEvent().subscribe({
        error: (error) => {
          expect(error).toEqual(errorResponse);
          done();
        }
      });
    });
  });

  describe('resetSelectedAPMEvent', () => {
    it('should reset selected APM to null when OrderPlacedEvent is triggered', () => {
      spyOn(eventService, 'get').and.returnValue(of({}));
      spyOn(service['selectedApm$'], 'next');

      service.resetSelectedAPMEvent();

      expect(eventService.get).toHaveBeenCalledWith(OrderPlacedEvent);
      expect(service['selectedApm$'].next).toHaveBeenCalledWith(null);
    });

    it('should not reset selected APM if OrderPlacedEvent is not triggered', () => {
      spyOn(eventService, 'get').and.returnValue(NEVER);
      spyOn(service['selectedApm$'], 'next');

      service.resetSelectedAPMEvent();

      expect(service['selectedApm$'].next).not.toHaveBeenCalled();
    });
  });

  describe(('getSaveApm'), () => {
    it('returns true when save is true', () => {
      service['save'] = true;
      expect(service.getSaveApm()).toBeTrue();
    });

    it('returns false when save is false', () => {
      service['save'] = false;
      expect(service.getSaveApm()).toBeFalse();
    });
  });

  describe(('setSaveApm'), () => {
    it('sets saveApm to true when setSaveApm is called with true', () => {
      service.setSaveApm(true);
      expect(service.getSaveApm()).toBeTrue();
    });

    it('sets saveApm to false when setSaveApm is called with false', () => {
      service.setSaveApm(false);
      expect(service.getSaveApm()).toBeFalse();
    });
  });
});
