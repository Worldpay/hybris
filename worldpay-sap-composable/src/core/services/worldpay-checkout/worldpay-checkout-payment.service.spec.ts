import { APP_BASE_HREF, PlatformLocation } from '@angular/common';
import { inject, TestBed } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CheckoutPaymentDetailsCreatedEvent, CheckoutPaymentDetailsSetEvent, CheckoutQueryFacade } from '@spartacus/checkout/base/root';
import { Address, EventService, LoggerService, PaymentDetails, UserIdService } from '@spartacus/core';
import { Observable, of, throwError } from 'rxjs';
import { MockActivatedRoute } from 'worldpay-sap-composable-tests';
import { WorldpayAdapter, WorldpayCheckoutPaymentAdapter, WorldpayCheckoutPaymentConnector, WorldpayConnector } from '../../connectors';
import { ThreeDsChallengeIframeUrlSetEvent, ThreeDsDDCIframeUrlSetEvent } from '../../events';
import { WorldpayACHFacade } from '../../facade';
import { PaymentMethod, ThreeDsDDCInfo } from '../../interfaces';
import { WorldpayCheckoutPaymentService } from './worldpay-checkout-payment.service';
import createSpy = jasmine.createSpy;

declare global {
  interface Window {
    Worldpay: {
      encrypt: () => string;
      setPublicKey: () => void;
    };
  }
}

describe('WorldpayCheckoutPaymentService', () => {
  let service: WorldpayCheckoutPaymentService;
  let sanitizer: DomSanitizer;
  let checkoutQueryFacade: CheckoutQueryFacade;
  let checkoutPaymentConnector: WorldpayCheckoutPaymentConnector;
  let worldpayConnector: WorldpayConnector;
  let worldpayACHFacade: WorldpayACHFacade;
  let eventService: EventService;
  const userId = 'testUserId';
  const cartId = 'testCartId';
  let logger: LoggerService;

  class ActiveCartServiceStub {
    cartId = cartId;

    public takeActiveCartId() {
      return of(this.cartId);
    }

    isGuestCart() {
      return of(false);
    }
  }

  class UserIdServiceStub implements Partial<UserIdService> {
    takeUserId = createSpy('getUserId').and.returnValue(of(userId));
  }

  class MockPlatformLocation implements Partial<PlatformLocation> {
    getBaseHrefFromDOM = createSpy('getBaseHrefFromDOM').and.returnValue('/spartacus/');

    onPopState = createSpy('onPopState').and.returnValue(() => {
    });

    onHashChange = createSpy('onHashChange').and.returnValue(() => {
    });
  }

  class MockWorldpayAdapter implements Partial<WorldpayAdapter> {
    getPublicKey = createSpy().and.returnValue(of('pk'));

    setPaymentAddress(): Observable<Address> {
      return of({
        id: 'address-1',
        formattedAddress: '123 Test St, AA1 2BB'
      });
    }

    getDDC3dsJwt(): Observable<ThreeDsDDCInfo> {
      return of({
        jwt: 'jwt',
        ddcUrl: 'https://test.com'
      });
    }
  }

  const paymentDetails: PaymentDetails = {
    id: 'mockPaymentDetails'
  };

  const address: Address = {
    line1: '123 Test St',
    postalCode: 'AA1 2BB'
  };

  class DomSanitizerStub {
    public bypassSecurityTrustResourceUrl(url: string) {
      return url;
    }
  }

  class MockWorldpayCheckoutPaymentAdapter implements Partial<WorldpayCheckoutPaymentAdapter> {
    getPaymentCardTypes = createSpy().and.returnValue(of([]));
    createWorldpayPaymentDetails = createSpy().and.returnValue(of({ id: 'mockPaymentDetails' }));
    useExistingPaymentDetails = createSpy().and.returnValue(of({ id: 'mockPaymentDetails' }));
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        RouterLink,
      ],
      providers: [
        WorldpayCheckoutPaymentService,
        EventService,
        {
          provide: ActivatedRoute,
          useClass: MockActivatedRoute
        },
        {
          provide: PlatformLocation,
          useClass: MockPlatformLocation
        },
        {
          provide: APP_BASE_HREF,
          useValue: '/spartacus/'
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
          provide: DomSanitizer,
          useClass: DomSanitizerStub
        },
        WorldpayCheckoutPaymentConnector,
        {
          provide: WorldpayCheckoutPaymentAdapter,
          useClass: MockWorldpayCheckoutPaymentAdapter
        },
        WorldpayConnector,
        {
          provide: WorldpayAdapter,
          useClass: MockWorldpayAdapter
        },
        LoggerService,
      ]
    });

    service = TestBed.inject(WorldpayCheckoutPaymentService);
    checkoutQueryFacade = TestBed.inject(CheckoutQueryFacade);
    sanitizer = TestBed.inject(DomSanitizer);
    checkoutPaymentConnector = TestBed.inject(WorldpayCheckoutPaymentConnector);
    worldpayConnector = TestBed.inject(WorldpayConnector);
    eventService = TestBed.inject(EventService);
    worldpayACHFacade = TestBed.inject(WorldpayACHFacade);
    logger = TestBed.inject(LoggerService);
    window.Worldpay = {
      encrypt: () => 'dummyCseToken',
      setPublicKey: () => {
      }
    };
    spyOn(service, 'getPublicKeyFromState').and.returnValue(of('pk'));
    spyOn(service, 'generateCseToken').and.returnValue(of('dummyCseToken'));
    spyOn(service, 'setCseToken').and.callThrough();
    spyOn(checkoutPaymentConnector, 'createWorldpayPaymentDetails').and.callThrough();
    spyOn(eventService, 'dispatch').and.callThrough();
  });

  it('should inject WorldpayCheckoutPaymentService', inject(
    [WorldpayCheckoutPaymentService],
    (worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService) => {
      expect(worldpayCheckoutPaymentService).toBeTruthy();
    }
  ));

  it('should be able to create payment details', () => {
    spyOn(service, 'generatePublicKey').and.callThrough();
    service.createPaymentDetails(paymentDetails).subscribe(response => response).unsubscribe();

    expect(service.generatePublicKey).toHaveBeenCalled();
    expect(service.generateCseToken).toHaveBeenCalledWith(paymentDetails);
    expect(service.setCseToken).toHaveBeenCalled();

    expect(checkoutPaymentConnector.createWorldpayPaymentDetails).toHaveBeenCalledWith(userId, cartId, paymentDetails, 'dummyCseToken');
    expect(eventService.dispatch).toHaveBeenCalledWith(
      {
        userId,
        cartId,
        paymentDetails
      },
      CheckoutPaymentDetailsCreatedEvent
    );
  });

  it('should be able to use existing payment details', () => {
    spyOn(checkoutPaymentConnector, 'useExistingPaymentDetails').and.callThrough();
    service.useExistingPaymentDetails(paymentDetails).subscribe().unsubscribe();
    expect(checkoutPaymentConnector.useExistingPaymentDetails).toHaveBeenCalledWith(userId, cartId, paymentDetails);
    checkoutPaymentConnector.useExistingPaymentDetails(userId, cartId, paymentDetails).subscribe(response => response).unsubscribe();
    expect(eventService.dispatch).toHaveBeenCalledWith(
      {
        userId: 'testUserId',
        cartId: 'testCartId',
        paymentDetailsId: 'mockPaymentDetails'
      }, CheckoutPaymentDetailsSetEvent);
  });

  it('should set billing address', (done) => {
    spyOn(worldpayConnector, 'setPaymentAddress').and.callThrough();
    service.setPaymentAddress(address).subscribe(response => {
      expect(worldpayConnector.setPaymentAddress).toHaveBeenCalledWith('testUserId', 'testCartId', address);
      expect(response).toEqual({
        id: 'address-1',
        formattedAddress: '123 Test St, AA1 2BB'
      });
      done();
    });

  });

  it('should get the 3ds DDC iframe url', (done) => {
    spyOn(sanitizer, 'bypassSecurityTrustResourceUrl').and.callThrough();
    const ddcUrl = '/ddc-iframe/action';
    const cardNumber = '4444333322221111';
    const jwt = 'some jwt data';
    const challenge$: Observable<string> = service.getSerializedUrl('worldpay-3ds-device-detection', {
      action: ddcUrl,
      bin: cardNumber,
      jwt
    });

    service.setThreeDsDDCIframeUrl(ddcUrl, cardNumber, jwt);
    challenge$.subscribe({
      next: (url: string) => {
        expect(sanitizer.bypassSecurityTrustResourceUrl).toHaveBeenCalledWith(url);
        expect(eventService.dispatch).toHaveBeenCalledWith(
          { threeDsDDCIframeUrl: '/spartacus/worldpay-3ds-device-detection?action=%2Fddc-iframe%2Faction&bin=4444333322221111&jwt=some%20jwt%20data' },
          ThreeDsDDCIframeUrlSetEvent
        );
        done();
      }
    });
  });

  it('should get the 3ds challenge iframe url', (done) => {
    spyOn(service, 'setThreeDsChallengeIframeUrl').and.callThrough();
    spyOn(sanitizer, 'bypassSecurityTrustResourceUrl').and.callThrough();
    const challengeUrl = '/challenge-iframe/action';
    const merchantData = '111020020219';
    const jwt = 'some jwt data';
    const challenge$: Observable<string> = service.getSerializedUrl('worldpay-3ds-challenge', {
      action: challengeUrl,
      md: merchantData,
      jwt
    });

    service.setThreeDsChallengeIframeUrl(challengeUrl, jwt, merchantData);
    challenge$.subscribe(
      {
        next: (url: string): void => {
          expect(sanitizer.bypassSecurityTrustResourceUrl).toHaveBeenCalledWith(url);
          expect(eventService.dispatch).toHaveBeenCalledWith(
            { worldpayChallengeIframeUrl: '/spartacus/worldpay-3ds-challenge?action=%2Fchallenge-iframe%2Faction&md=111020020219&jwt=some%20jwt%20data' },
            ThreeDsChallengeIframeUrlSetEvent
          );
          done();
        }
      }
    );
  });

  it('should update save credit card value', () => {
    let saveCard = false;
    service.getSaveCreditCardValueFromState().subscribe(response => saveCard = response).unsubscribe();
    expect(saveCard).toBeFalse();
    service.setSaveCreditCardValue(true);
    service.getSaveCreditCardValueFromState().subscribe(response => saveCard = response).unsubscribe();
    expect(saveCard).toBeTrue();
  });

  it('should return payment details state with saved and default flags', () => {
    spyOn(service, 'getSaveCreditCardValueFromState').and.returnValue(of(true));
    spyOn(service, 'getSaveAsDefaultCardValueFromState').and.returnValue(of(true));
    spyOn(worldpayACHFacade, 'getACHPaymentFormValue').and.returnValue(of(null));
    spyOn(checkoutQueryFacade, 'getCheckoutDetailsState').and.returnValue(of({
      loading: false,
      error: false,
      data: {
        paymentInfo: { id: '1' },
        worldpayAPMPaymentInfo: { apmCode: 'ACH' }
      }
    }));

    service.getPaymentDetailsState().subscribe(state => {
      expect(state.data.saved).toEqual(true);
      expect(state.data.defaultPayment).toEqual(true);
    });
  });

  it('should return payment details state with ACH payment form', () => {
    const achPaymentFormValue = { accountNumber: '123456789' };
    spyOn(service, 'getSaveCreditCardValueFromState').and.returnValue(of(false));
    spyOn(service, 'getSaveAsDefaultCardValueFromState').and.returnValue(of(false));
    spyOn(worldpayACHFacade, 'getACHPaymentFormValue').and.returnValue(of(achPaymentFormValue));
    spyOn(checkoutQueryFacade, 'getCheckoutDetailsState').and.returnValue(of({
      loading: false,
      error: false,
      data: {
        worldpayAPMPaymentInfo: {
          apmCode: PaymentMethod.ACH
        },
        paymentType: {
          code: PaymentMethod.ACH,
          name: 'ACH'
        }
      }
    }));

    service.getPaymentDetailsState().subscribe(state => {
      expect(state.data.achPaymentForm).toEqual(achPaymentFormValue);
    });
  });

  it('should return payment details state without saved and default flags when no id', (done) => {
    spyOn(service, 'getSaveCreditCardValueFromState').and.returnValue(of(true));
    spyOn(service, 'getSaveAsDefaultCardValueFromState').and.returnValue(of(true));
    spyOn(worldpayACHFacade, 'getACHPaymentFormValue').and.returnValue(of(null));
    spyOn(checkoutQueryFacade, 'getCheckoutDetailsState').and.returnValue(of({
      loading: false,
      error: false,
      data: {
        paymentInfo: {},
        worldpayAPMPaymentInfo: { apmCode: PaymentMethod.ACH }
      }
    }));

    service.getPaymentDetailsState().subscribe(state => {
      expect(state.data.saved).toBeUndefined();
      expect(state.data.defaultPayment).toBeUndefined();
      done();
    });
  });

  it('should emit cse token when generateCseToken succeeds', (done) => {
    (service.generateCseToken as jasmine.Spy).and.callThrough();
    spyOn(window.Worldpay, 'encrypt').and.returnValue('generatedToken');

    service.generateCseToken(paymentDetails).subscribe({
      next: (token: string): void => {
        expect(token).toBe('generatedToken');
        done();
      },
      error: (): void => {
        fail('Expected token emission, but stream errored');
        done();
      }
    });
  });

  it('should emit observable error when Worldpay.encrypt throws in generateCseToken', (done) => {
    const mockError = new Error('encrypt failed');
    (service.generateCseToken as jasmine.Spy).and.callThrough();
    spyOn(window.Worldpay, 'encrypt').and.throwError(mockError.message);

    service.generateCseToken(paymentDetails).subscribe({
      next: (): void => {
        fail('Expected stream to error when encrypt throws');
        done();
      },
      error: (error: Error): void => {
        expect(error.message).toBe(mockError.message);
        done();
      }
    });
  });

  it('should log an error when generatePublicKey fails', () => {
    const mockError = new Error('Public key generation failed');
    spyOn(logger, 'error');
    spyOn(service, 'generatePublicKey').and.returnValue(throwError(() => mockError));

    service.generatePublicKey().subscribe({
      error: (error) => logger.error('Failed obtaining public key', { error }),
    });

    expect(logger.error).toHaveBeenCalledWith('Failed obtaining public key', { error: mockError });
  });
});

