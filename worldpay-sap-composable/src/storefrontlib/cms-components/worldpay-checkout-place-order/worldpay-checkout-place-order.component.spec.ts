import { Renderer2, ViewContainerRef } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CheckoutStepService } from '@spartacus/checkout/base/components';
import {
  Address,
  CurrencyService,
  GlobalMessageService,
  GlobalMessageType,
  HttpErrorModel,
  I18nTestingModule,
  LanguageService,
  LoggerService,
  PaymentDetails,
  QueryState,
  RoutingService,
  WindowRef
} from '@spartacus/core';
import { FormErrorsModule, LaunchDialogService } from '@spartacus/storefront';
import { of, throwError } from 'rxjs';
import {
  MockActivatedRoute,
  MockCheckoutStepService,
  MockGlobalMessageService,
  MockLaunchDialogService,
  MockRoutingService,
  MockUrlPipe,
  MockWorldpayACHFacade,
  MockWorldpayApmService,
  MockWorldpayCheckoutPaymentService,
  MockWorldpayFraudsightService,
  MockWorldpayOrderService
} from 'worldpay-sap-composable-tests';
import {
  ACHPaymentForm, ApmNormalizer,
  ApmPaymentDetails,
  OccWorldpayApmAdapter,
  OccWorldpayCheckoutPaymentAdapter,
  PaymentMethod,
  ThreeDsInfo,
  WorldpayACHFacade,
  WorldpayApmConnector,
  WorldpayApmFacade,
  WorldpayApmPaymentInfo,
  WorldpayCheckoutPaymentAdapter,
  WorldpayCheckoutPaymentFacade,
  WorldpayFraudsightFacade,
  WorldpayOrderFacade
} from '../../../core';
import { WorldpayCheckoutPlaceOrderComponent } from './worldpay-checkout-place-order.component';

const mockBillingAddress: Address = {
  formattedAddress: 'address',
  id: '0001',
};
const mockPaypal: ApmPaymentDetails = {
  billingAddress: mockBillingAddress,
  defaultPayment: false,
  saved: false,
  name: 'PayPal',
  code: 'PAYPAL-EXPRESS'
};

const mockCreditCard: PaymentDetails = {
  accountHolderName: 'user',
  billingAddress: mockBillingAddress,
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

const mockIdeal: WorldpayApmPaymentInfo = {
  billingAddress: mockBillingAddress,
  defaultPayment: false,
  saved: false,
  apmCode: PaymentMethod.iDeal,
  apmName: 'ING',
  name: 'iDeal',
  achPaymentForm: {
    accountType: 'checking',
  }
};

const mockActivatedRoute = {
  snapshot: {
    url: ['checkout', 'shipping-address'],
  },
};

const MockWindowRef = {
  isBrowser(): boolean {
    return true;
  },
  nativeWindow: {
    removeEventListener: () => {
    },
    addEventListener: () => {
    }
  }
};

class MockRenderer2 {
}

describe('WorldpayCheckoutPlaceOrderComponent', () => {
  let component: WorldpayCheckoutPlaceOrderComponent;
  let fixture: ComponentFixture<WorldpayCheckoutPlaceOrderComponent>;
  let controls: UntypedFormGroup['controls'];
  let orderFacade: WorldpayOrderFacade;
  let routingService: RoutingService;
  let placeOrderSpy: jasmine.Spy;
  let winRef: WindowRef;
  let mockRenderer2: MockRenderer2;
  let worldpayCheckoutPaymentFacade: WorldpayCheckoutPaymentFacade;
  let worldpayApmFacade: WorldpayApmFacade;
  let worldpayACHFacade: WorldpayACHFacade;
  let globalMessageService: GlobalMessageService;
  let logger: LoggerService;

  beforeEach(
    waitForAsync(() => {
      const mockCurrencyService = {
        getActive: () => of('USD'),
      };
      const mockLanguageService = {
        getActive: () => of('en'),
      };

      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          RouterLink,
          I18nTestingModule,
          FormErrorsModule,
        ],
        declarations: [MockUrlPipe, WorldpayCheckoutPlaceOrderComponent],
        providers: [
          ApmNormalizer,
          {
            provide: ActivatedRoute,
            useClass: MockActivatedRoute
          },
          {
            provide: WorldpayOrderFacade,
            useClass: MockWorldpayOrderService
          },
          {
            provide: RoutingService,
            useClass: MockRoutingService
          },
          {
            provide: LaunchDialogService,
            useClass: MockLaunchDialogService
          },
          {
            provide: GlobalMessageService,
            useClass: MockGlobalMessageService
          },
          {
            provide: CurrencyService,
            useValue: mockCurrencyService
          },
          {
            provide: LanguageService,
            useValue: mockLanguageService
          },
          ViewContainerRef,
          {
            provider: DomSanitizer,
            useValue: {
              bypassSecurityTrustResourceUrl: () => 'safeUrl'
            }
          },
          {
            provide: WorldpayApmConnector,
            useClass: OccWorldpayApmAdapter
          },
          {
            provide: WorldpayCheckoutPaymentAdapter,
            useClass: OccWorldpayCheckoutPaymentAdapter
          },
          {
            provide: CheckoutStepService,
            useClass: MockCheckoutStepService
          },
          {
            provide: ActivatedRoute,
            useValue: mockActivatedRoute
          },
          {
            provide: WindowRef,
            useValue: MockWindowRef
          },
          {
            provide: Renderer2,
            useValue: mockRenderer2,
          },
          {
            //useClass: MockWorldpayCheckoutPaymentService
            provide: WorldpayCheckoutPaymentFacade,
            useFactory: (sanitizer: DomSanitizer) => new MockWorldpayCheckoutPaymentService(sanitizer),
            deps: [DomSanitizer]
          },
          {
            provide: WorldpayApmFacade,
            useClass: MockWorldpayApmService
          },
          {
            provide: WorldpayACHFacade,
            useClass: MockWorldpayACHFacade
          },
          {
            provide: WorldpayFraudsightFacade,
            useClass: MockWorldpayFraudsightService
          },
          LoggerService
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayCheckoutPlaceOrderComponent);
    component = fixture.componentInstance;
    controls = component.checkoutSubmitForm.controls;
    orderFacade = TestBed.inject(WorldpayOrderFacade);
    routingService = TestBed.inject(RoutingService);
    mockRenderer2 = TestBed.inject(Renderer2);
    winRef = TestBed.inject(WindowRef);
    globalMessageService = TestBed.inject(GlobalMessageService);
    worldpayCheckoutPaymentFacade = TestBed.inject(WorldpayCheckoutPaymentFacade);
    worldpayApmFacade = TestBed.inject(WorldpayApmFacade);
    worldpayACHFacade = TestBed.inject(WorldpayACHFacade);
    logger = TestBed.inject(LoggerService);
    placeOrderSpy = spyOn(component, 'placeWorldpayOrder');
    placeOrderSpy.and.callThrough();
    spyOn(orderFacade, 'executeDDC3dsJwtCommand').and.callThrough();
  });

  function submitForm(isTermsCondition: boolean): void {
    controls['termsAndConditions'].setValue(isTermsCondition);
    component.submitForm();
  }

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should not place order when checkbox not checked', () => {
    placeOrderSpy.and.callThrough();
    submitForm(false);

    expect(component.placeWorldpayOrder).not.toHaveBeenCalled();
  });

  describe('should place order when checkbox checked', () => {
    beforeEach(() => {
      spyOn(orderFacade, 'startLoading').and.callThrough();
      controls['termsAndConditions'].setValue(true);
    });

    it('should place order with credit card', () => {
      placeOrderSpy.and.returnValue(of([
        {
          code: PaymentMethod.Card,
          name: 'Visa',
          cardType: 'visa'
        },
        {
          loading: false,
          error: false,
          data: mockCreditCard
        }
      ]));
      submitForm(true);
      expect(orderFacade.startLoading).toHaveBeenCalledWith(component['vcr']);
      expect(component.placeWorldpayOrder).toHaveBeenCalled();
      component.placeWorldpayOrder().subscribe((data) => {
        expect(data[0].code).toEqual(PaymentMethod.Card);
        expect(data[1].data).toEqual(mockCreditCard);
      });

      expect(orderFacade.executeDDC3dsJwtCommand).toHaveBeenCalled();

    });

    it('should place order with saved APM', () => {
      spyOn<any>(component, 'processPayment');
      const apm = {
        code: PaymentMethod.SepaDirectDebit,
        name: 'Sepa Direct Debit',
        isAPM: true,
        subscriptionId: '000000000'
      };

      placeOrderSpy.and.returnValue(of([
        apm,
        {
          loading: false,
          error: false,
          data: apm
        }
      ]));
      submitForm(true);
      expect(orderFacade.startLoading).toHaveBeenCalledWith(component['vcr']);
      expect(component.placeWorldpayOrder).toHaveBeenCalled();
      component.placeWorldpayOrder().subscribe((data) => {
        expect(data[0].code).toEqual(PaymentMethod.SepaDirectDebit);
        expect(data[1].data).toEqual(apm);
      });

      expect(orderFacade.executeDDC3dsJwtCommand).not.toHaveBeenCalled();
      expect(component['processPayment']).toHaveBeenCalledWith(apm.subscriptionId);

    });

    it('should place order with iDeal', () => {
      spyOn(worldpayApmFacade, 'getAPMRedirectUrl').and.callThrough();
      spyOn(worldpayApmFacade, 'getSaveApm').and.returnValue(of(false));
      spyOn(worldpayCheckoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: mockIdeal
      }));
      spyOn(worldpayApmFacade, 'selectAPM').and.callThrough();
      placeOrderSpy.and.returnValue(of([
        {
          code: PaymentMethod.iDeal,
          name: 'ING',
        },
        {
          loading: false,
          error: false,
          data: mockIdeal
        }
      ]));

      submitForm(true);

      expect(worldpayApmFacade.getAPMRedirectUrl).toHaveBeenCalledWith({
        code: PaymentMethod.iDeal,
        name: 'ING',
      });

    });
  });

  it('should change page and reset form data on a successful place order', () => {
    component.onSuccess();

    expect(routingService.go).toHaveBeenCalledWith({
      cxRoute: 'orderConfirmation',
    });
  });

  it('should execute the executeDDC3dsJwtCommand method successfully', function() {
    component.executeDDC3dsJwtCommand();
    expect(orderFacade.executeDDC3dsJwtCommand).toHaveBeenCalled();
  });

  it('should place Worldpay order', () => {
    spyOn(worldpayApmFacade, 'getSelectedAPMFromState').and.callThrough();
    spyOn(worldpayCheckoutPaymentFacade, 'getPaymentDetailsState').and.callThrough();
    component.placeWorldpayOrder().subscribe((data) => {
      expect(data[0].code).toEqual(PaymentMethod.Card);
      expect(data[1].data).toEqual(mockCreditCard);
    });
    expect(worldpayApmFacade.getSelectedAPMFromState).toHaveBeenCalled();
    expect(worldpayCheckoutPaymentFacade.getPaymentDetailsState).toHaveBeenCalled();
  });

  it('should return an observable with the selected APM and payment details state', function() {
    const selectedApm: ApmPaymentDetails = {
      code: PaymentMethod.Card,
      name: 'Visa'
    };
    const paymentDetailsState: QueryState<PaymentDetails> = {
      loading: false,
      error: false,
      data: {
        id: '00001'
      }
    };
    spyOn(worldpayApmFacade, 'getSelectedAPMFromState').and.returnValue(of(selectedApm));
    spyOn(worldpayCheckoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of(paymentDetailsState));
    const result = component.placeWorldpayOrder();

    result.subscribe(([apmData, paymentDetails]) => {
      expect(apmData).toEqual({
        code: PaymentMethod.Card,
        name: 'Visa'
      });
      expect(paymentDetails).toEqual(paymentDetailsState);
    });
  });

  it('should emit the latest values of the selected APM and payment details state when both observables emit', function() {
    const selectedApm2: ApmPaymentDetails = { code: PaymentMethod.PayPal };
    const paymentDetailsState1: QueryState<PaymentDetails> = {
      loading: false,
      error: false,
      data: {
        id: '00001'
      }
    };
    spyOn(worldpayApmFacade, 'getSelectedAPMFromState').and.returnValues(of(selectedApm2));
    spyOn(worldpayCheckoutPaymentFacade, 'getPaymentDetailsState').and.returnValues(of(paymentDetailsState1));

    const result = component.placeWorldpayOrder();

    result.subscribe(([apmData, paymentDetails]) => {
      expect(apmData).toEqual(selectedApm2);
      expect(paymentDetails).toEqual(paymentDetailsState1);
    });
  });

  it('should get order details', () => {
    let orderDetails = null;
    spyOn(orderFacade, 'getOrderDetails').and.callThrough();
    component.ngOnInit();
    expect(orderFacade.getOrderDetails).toHaveBeenCalled();
    orderFacade.getOrderDetails().subscribe(response => {
      orderDetails = response;
    });
    expect(orderDetails).toEqual({
      code: 'order-0001',
      entries: []
    });
  });

  it('should return an observable that emits an error if the checkout preconditions are not met', () => {
    spyOn(worldpayApmFacade, 'checkoutPreconditions').and.returnValue(throwError(() => new Error('Checkout conditions not met')));
    worldpayApmFacade.checkoutPreconditions().subscribe({
      error: (error) => {
        expect(error.message).toEqual('Checkout conditions not met');
      }
    });
  });

  it('should get CseToken from State', () => {
    let cseToken = null;
    spyOn(worldpayCheckoutPaymentFacade, 'getCseTokenFromState').and.callThrough();
    component.ngOnInit();
    expect(worldpayCheckoutPaymentFacade.getCseTokenFromState).toHaveBeenCalled();
    worldpayCheckoutPaymentFacade.getCseTokenFromState().subscribe(response => cseToken = response).unsubscribe();
    expect(cseToken).toEqual('token');
  });

  describe('Place order UI', () => {
    beforeEach(() => {
      component.ngOnInit();
      controls['termsAndConditions'].setValue(true);
    });

    it('should have the place order button ENABLED when terms and condition is checked', () => {
      fixture.detectChanges();

      expect(fixture.debugElement.nativeElement.querySelector('.btn-submit').disabled).toEqual(false);
    });
  });

  describe('OnDestroy', () => {
    beforeEach(() => {
      spyOn(component.nativeWindow, 'removeEventListener').and.callThrough();
      submitForm(true);
      component.ngOnDestroy();
    });

    it('removeEventListener', () => {
      expect(component.nativeWindow.removeEventListener).toHaveBeenCalled();
    });
  });

  it('should call place ACH Order when ACHForm is valid', () => {
    component.ngOnInit();
    controls['termsAndConditions'].setValue(true);
    fixture.detectChanges();
    spyOn(worldpayApmFacade, 'getSelectedAPMFromState').and.returnValue(of({
      code: PaymentMethod.ACH,
      name: 'ACH'
    }));
    spyOn(worldpayACHFacade, 'getACHPaymentFormValue').and.returnValue(
      of({
        accountType: 'type1',
        accountNumber: '123456789',
        routingNumber: '987654321',
        checkNumber: '11111',
        companyName: 'Test Company',
        customIdentifier: '12345'
      })
    );
    spyOn(orderFacade, 'placeACHOrder').and.callThrough();

    component.submitForm();
    expect(orderFacade.placeACHOrder).toHaveBeenCalledWith({
      accountType: 'type1',
      accountNumber: '123456789',
      routingNumber: '987654321',
      checkNumber: '11111',
      companyName: 'Test Company',
      customIdentifier: '12345'
    });
  });

  describe('submitForm', () => {
    it('should start loading and handle DDC and challenge if form is valid', () => {
      spyOn(orderFacade, 'startLoading');
      // @ts-ignore
      spyOn(component, 'ddcIframeHandler').and.callThrough();
      // @ts-ignore
      spyOn(component, 'challengeIframeHandler').and.callThrough();
      placeOrderSpy.and.returnValue(of([mockCreditCard, {
        loading: false,
        error: false,
        data: mockCreditCard
      }, null]));
      spyOn(component, 'executeDDC3dsJwtCommand').and.callThrough();

      component.checkoutSubmitForm.setValue({ termsAndConditions: true });
      component.submitForm();

      expect(orderFacade.startLoading).toHaveBeenCalledWith(component['vcr']);
      expect(component['ddcIframeHandler']).toHaveBeenCalled();
      expect(component['challengeIframeHandler']).toHaveBeenCalled();
      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.placingOrder' }, GlobalMessageType.MSG_TYPE_INFO);
      expect(component.placeWorldpayOrder).toHaveBeenCalled();
      expect(component.executeDDC3dsJwtCommand).toHaveBeenCalled();
    });

    it('should mark form controls as touched if form is invalid', () => {
      spyOn(component.checkoutSubmitForm, 'markAllAsTouched').and.callThrough();

      component.checkoutSubmitForm.setValue({ termsAndConditions: false });
      component.submitForm();

      expect(component.checkoutSubmitForm.markAllAsTouched).toHaveBeenCalled();
    });

    it('should handle error when placing Worldpay order fails', () => {
      const mockError = new Error('Error');
      placeOrderSpy.and.returnValue(throwError(() => mockError));
      spyOn(orderFacade, 'clearLoading').and.callThrough();
      spyOn(worldpayApmFacade, 'showErrorMessage').and.callThrough();

      component.checkoutSubmitForm.setValue({ termsAndConditions: true });
      component.submitForm();

      expect(orderFacade.clearLoading).toHaveBeenCalled();
      expect(worldpayApmFacade.showErrorMessage).toHaveBeenCalledWith(mockError as HttpErrorModel);
    });

    it('should place ACH order if achPaymentFormValue is provided', () => {
      // @ts-ignore
      spyOn(component, 'placeACHOrder').and.callThrough();
      spyOn(component, 'executeDDC3dsJwtCommand').and.callThrough();

      component.checkoutSubmitForm.setValue({ termsAndConditions: true });
      component.placeWorldpayOrder = jasmine.createSpy().and.returnValue(of([mockIdeal, { data: mockCreditCard }, { accountType: 'checking' }]));
      component.submitForm();

      expect(component['placeACHOrder']).toHaveBeenCalledWith({ accountType: 'checking' });
      expect(component.executeDDC3dsJwtCommand).not.toHaveBeenCalled();
    });

    it('should executeDDC3dsJwtCommand if apm cartType is provided', () => {
      // @ts-ignore
      spyOn(component, 'placeACHOrder').and.callThrough();
      spyOn(component, 'executeDDC3dsJwtCommand').and.callThrough();

      component.checkoutSubmitForm.setValue({ termsAndConditions: true });
      component.placeWorldpayOrder = jasmine.createSpy().and.returnValue(of([mockCreditCard, { data: mockCreditCard }, null]));
      component.submitForm();

      expect(component.executeDDC3dsJwtCommand).toHaveBeenCalled();
    });

    it('should get APM redirect URL if APM payment details are provided', () => {
      spyOn(worldpayApmFacade, 'getAPMRedirectUrl').and.callThrough();
      spyOn(component, 'executeDDC3dsJwtCommand').and.callThrough();

      component.checkoutSubmitForm.setValue({ termsAndConditions: true });
      component.placeWorldpayOrder = jasmine.createSpy().and.returnValue(of([mockPaypal, { data: mockCreditCard }, null]));
      component.submitForm();

      expect(worldpayApmFacade.getAPMRedirectUrl).toHaveBeenCalledWith(mockPaypal);
      expect(component.executeDDC3dsJwtCommand).not.toHaveBeenCalled();
    });
  });

  describe('placeACHOrder', () => {
    it('should call placeACHOrder on orderFacade with the provided ACH payment form value', () => {
      const achPaymentFormValue = { accountType: 'checking' } as ACHPaymentForm;
      spyOn(orderFacade, 'placeACHOrder').and.callThrough();

      component['placeACHOrder'](achPaymentFormValue);

      expect(orderFacade.placeACHOrder).toHaveBeenCalledWith(achPaymentFormValue);
    });

    it('should clear loading on error', () => {
      const achPaymentFormValue = { accountType: 'checking' } as ACHPaymentForm;
      const mockError = new Error('Error');
      spyOn(orderFacade, 'placeACHOrder').and.returnValue(throwError(() => mockError));
      spyOn(orderFacade, 'clearLoading').and.callThrough();

      component['placeACHOrder'](achPaymentFormValue);

      expect(orderFacade.clearLoading).toHaveBeenCalled();
    });
  });

  describe('processDdc', () => {
    it('should initiate initial payment request and set placed order if DDC response is valid', () => {
      const mockEvent = {
        origin: 'https://centinelapistag.cardinalcommerce.com',
        data: JSON.stringify({
          SessionId: '12345',
          Status: true,
        })
      } as MessageEvent;
      spyOn(orderFacade, 'initialPaymentRequest').and.returnValue(of({ order: { code: '00001' } }));
      spyOn(orderFacade, 'setPlacedOrder').and.callThrough();

      component['processDdc'](mockEvent);

      expect(orderFacade.initialPaymentRequest).toHaveBeenCalledWith(
        component.paymentDetails,
        '12345',
        component.cseToken,
        component.checkoutSubmitForm.get('termsAndConditions').value,
        component['fraudSightId'],
        component.browserInfo,
        component.scheduleReplenishmentFormData,
      );
      expect(orderFacade.setPlacedOrder).toHaveBeenCalledWith({ code: '00001' });
    });

    it('should log error and show error message if DDC response is invalid', () => {
      const mockEvent = {
        origin: 'https://centinelapistag.cardinalcommerce.com',
        data: 'invalid data'
      } as MessageEvent;
      spyOn(logger, 'error');
      spyOn(orderFacade, 'clearLoading').and.callThrough();

      component['processDdc'](mockEvent);

      expect(logger.error).toHaveBeenCalledWith('failed to process ddc response', jasmine.any(Error));
      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.threeDsChallengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(orderFacade.clearLoading).toHaveBeenCalled();
    });

    it('should show application error message and clear loading if initial payment request fails', () => {
      const mockEvent = {
        origin: 'https://centinelapistag.cardinalcommerce.com',
        data: JSON.stringify({
          SessionId: '12345',
          Status: true
        })
      } as MessageEvent;
      const mockError = new Error('Error');
      spyOn(orderFacade, 'initialPaymentRequest').and.returnValue(throwError(() => mockError));
      spyOn(orderFacade, 'clearLoading').and.callThrough();

      component['processDdc'](mockEvent);

      expect(globalMessageService.remove).toHaveBeenCalledWith(GlobalMessageType.MSG_TYPE_INFO);
      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.applicationError' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(orderFacade.clearLoading).toHaveBeenCalled();
    });

    it('should do nothing if event origin does not match expected URL', () => {
      const mockEvent = {
        origin: 'https://invalid-origin.com',
        data: JSON.stringify({
          SessionId: '12345',
          Status: true
        })
      } as MessageEvent;
      spyOn(orderFacade, 'initialPaymentRequest').and.callThrough();

      component['processDdc'](mockEvent);

      expect(orderFacade.initialPaymentRequest).not.toHaveBeenCalled();
    });
  });

  describe('challengeIframeHandler', () => {
    it('should set iframe URL and dimensions if challenge info is available', () => {
      const mockChallengeInfo = {
        threeDSFlexData: {
          challengeUrl: 'https://challengeurl.aws.e2y.io',
          jwt: 'jwt'
        },
        merchantData: '123-456'
      } as ThreeDsInfo;
      spyOn(worldpayCheckoutPaymentFacade, 'getThreeDsChallengeInfoFromState').and.returnValue(of(mockChallengeInfo));
      spyOn(worldpayCheckoutPaymentFacade, 'setThreeDsChallengeIframeUrl').and.callThrough();

      component['challengeIframeHandler']();

      expect(worldpayCheckoutPaymentFacade.setThreeDsChallengeIframeUrl).toHaveBeenCalledWith(
        mockChallengeInfo.threeDSFlexData.challengeUrl,
        mockChallengeInfo.threeDSFlexData.jwt,
        mockChallengeInfo.merchantData
      );
      expect(component.challengeIframeHeight).toBe(400);
      expect(component.challengeIframeWidth).toBeGreaterThan(0);
    });

    it('should log message if 3DS flex data is not available', () => {
      const mockChallengeInfo = {
        threeDSFlexData: null,
        merchantData: '123-456'
      } as ThreeDsInfo;
      spyOn(worldpayCheckoutPaymentFacade, 'getThreeDsChallengeInfoFromState').and.returnValue(of(mockChallengeInfo));
      spyOn(logger, 'log');

      component['challengeIframeHandler']();

      expect(logger.log).toHaveBeenCalledWith('3ds flow not implemented');
    });

    it('should unregister previous message handler for device detection', () => {
      const mockChallengeInfo = {
        threeDSFlexData: {
          challengeUrl: 'https://challengeurl.aws.e2y.io',
          jwt: 'jwt'
        },
        merchantData: '123-456'
      } as ThreeDsInfo;
      component['ddcHandler'] = jasmine.createSpy();
      spyOn(worldpayCheckoutPaymentFacade, 'getThreeDsChallengeInfoFromState').and.returnValue(of(mockChallengeInfo));
      spyOn(winRef.nativeWindow, 'removeEventListener').and.callThrough();

      component['challengeIframeHandler']();

      expect(winRef.nativeWindow.removeEventListener).toHaveBeenCalledWith('message', component['ddcHandler'], true);
    });

    it('should not set iframe URL and dimensions if challenge info is not available', () => {
      spyOn(worldpayCheckoutPaymentFacade, 'getThreeDsChallengeInfoFromState').and.returnValue(of(null));
      spyOn(worldpayCheckoutPaymentFacade, 'setThreeDsChallengeIframeUrl').and.callThrough();

      component['challengeIframeHandler']();

      expect(worldpayCheckoutPaymentFacade.setThreeDsChallengeIframeUrl).not.toHaveBeenCalled();
    });
  });

  describe('processChallenge', () => {
    it('should remove event listener if challengeHandler exists', () => {
      component['challengeHandler'] = jasmine.createSpy();
      spyOn(component.nativeWindow, 'removeEventListener').and.callThrough();

      const mockEvent = { data: { accepted: true } } as MessageEvent;
      component['processChallenge'](mockEvent);

      expect(component.nativeWindow.removeEventListener).toHaveBeenCalledWith('message', component['challengeHandler'], true);
    });

    it('should call challengeAccepted if response is accepted', () => {
      const mockEvent = { data: { accepted: true } } as MessageEvent;
      spyOn(orderFacade, 'challengeAccepted').and.callThrough();

      component['processChallenge'](mockEvent);

      expect(orderFacade.challengeAccepted).toHaveBeenCalledWith(mockEvent.data);
    });

    it('should call challengeFailed if response is not accepted', () => {
      const mockEvent = { data: { accepted: false } } as MessageEvent;
      spyOn(orderFacade, 'challengeFailed').and.callThrough();

      component['processChallenge'](mockEvent);

      expect(orderFacade.challengeFailed).toHaveBeenCalled();
    });

    it('should do nothing if response does not have accepted property', () => {
      const mockEvent = { data: { someOtherProperty: true } } as MessageEvent;
      spyOn(orderFacade, 'challengeAccepted').and.callThrough();
      spyOn(orderFacade, 'challengeFailed').and.callThrough();

      component['processChallenge'](mockEvent);

      expect(orderFacade.challengeAccepted).not.toHaveBeenCalled();
      expect(orderFacade.challengeFailed).not.toHaveBeenCalled();
    });

    it('should log error and call challengeFailed if an error occurs', () => {
      const mockEvent = null as MessageEvent;
      spyOn(logger, 'error');
      spyOn(orderFacade, 'challengeFailed').and.callThrough();

      component['processChallenge'](mockEvent);

      expect(logger.error).toHaveBeenCalledWith('Failed to process challenge event data', jasmine.any(Error));
      expect(orderFacade.challengeFailed).toHaveBeenCalled();
    });
  });
});
