import { NgZone, Pipe, PipeTransform, Renderer2, ViewContainerRef } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { Address, GlobalMessageEntities, GlobalMessageService, I18nTestingModule, QueryState, RoutingService, WindowRef } from '@spartacus/core';
import { FormErrorsModule, LaunchDialogService } from '@spartacus/storefront';
import { Observable, of, throwError } from 'rxjs';
import { WorldpayCheckoutPlaceOrderComponent } from './worldpay-checkout-place-order.component';
import { WorldpayCheckoutPaymentAdapter } from '../../../core/connectors/worldpay-payment-connector/worldpay-checkout-payment.adapter';
import { OccWorldpayCheckoutPaymentAdapter } from '../../../core/occ/adapters/worldpay-checkout-payment-connector/occ-worldpay-checkout-payment.adapter';
import { CheckoutStepService } from '@spartacus/checkout/base/components';
import { ActivatedRoute } from '@angular/router';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { WorldpayFraudsightService } from '../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { PaymentDetails } from '@spartacus/cart/base/root';
import { Order } from '@spartacus/order/root/model/order.model';
import {
  ACHPaymentForm,
  ApmPaymentDetails,
  APMRedirectResponse,
  PaymentMethod,
  PlaceOrderResponse,
  ThreeDsDDCInfo,
  ThreeDsInfo,
  WorldpayApmPaymentInfo
} from '../../../core/interfaces';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { WorldpayApmConnector } from '../../../core/connectors';
import { OccWorldpayApmAdapter } from '../../../core/occ/adapters/worldpay-apm/occ-worldpay-apm.adapter';
import { WorldpayOrderService } from '../../../core/services';
import { WorldpayACHFacade } from '../../../core/facade/worldpay-ach.facade';
import createSpy = jasmine.createSpy;

const mockBillingAddress: Address = {
  formattedAddress: 'address',
  id: '0001',
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

const mockPaypal: ApmPaymentDetails = {
  billingAddress: mockBillingAddress,
  defaultPayment: false,
  saved: false,
  name: 'PayPal',
  code: 'PAYPAL-EXPRESS'
};
const mockIdeal: WorldpayApmPaymentInfo = {
  billingAddress: mockBillingAddress,
  defaultPayment: false,
  saved: false,
  apmCode: PaymentMethod.iDeal,
  apmName: 'ING',
  name: 'iDeal'
};

class MockWorldpayOrderService implements Partial<WorldpayOrderService> {
  placeOrder = createSpy().and.returnValue(of({}));

  clearOrder = createSpy();

  getOrderDetails(): Observable<Order | undefined> {
    return of({
      code: 'order-0001',
      entries: []
    });
  }

  getLoading(): Observable<boolean> {
    return of(false);
  }

  startLoading(): void {

  }

  getAPMRedirectUrl(): Observable<APMRedirectResponse> {
    return of({
      postUrl: 'https://postURL.com',
      parameters: {
        entry: []
      },
      mappingLabels: {}
    });
  }

  clearLoading(): void {
  }

  executeDDC3dsJwtCommand(): Observable<ThreeDsDDCInfo> {
    return of({
      ddcUrl: 'https://centinelapistag.cardinalcommerce.com',
      jwt: 'jwt'
    });
  }

  initialPaymentRequest(): Observable<PlaceOrderResponse> {
    return of({
      threeDSecureNeeded: false,
      transactionStatus: 'SUCCESS',
      order: {
        code: '00001'
      }
    });
  }

  setPlacedOrder(): void {
  }

  placeACHOrder(): Observable<Order> {
    return of(null);
  }
}

class MockRoutingService implements Partial<RoutingService> {
  go = createSpy().and.returnValue(of(true).toPromise());
}

class MockLaunchDialogService implements Partial<LaunchDialogService> {
  launch = createSpy();
  clear = createSpy();
}

class MockCheckoutStepService implements Partial <CheckoutStepService> {

}

@Pipe({
  name: 'cxUrl',
})
class MockUrlPipe implements PipeTransform {
  transform(): any {
  }
}

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

class MockWorldpayApmService implements Partial<WorldpayApmService> {
  getWorldpayAPMRedirectUrlFromState(): Observable<APMRedirectResponse> {
    return of({
      postUrl: 'https://postURL.com',
      parameters: {
        entry: []
      },
      mappingLabels: {}
    });
  }

  getLoading(): Observable<boolean> {
    return of(false);
  }

  getAPMRedirectUrl() {

  }

  selectAPM(): void {

  }

  checkoutPreconditions(): Observable<[string, string]> {
    return of(['userId', 'cartId']);
  }

  getSelectedAPMFromState(): Observable<ApmPaymentDetails> {
    return of({
      code: PaymentMethod.Card,
      name: 'Visa',
    });
  }

  showErrorMessage(): void {

  }
}

class MockWorldpayFraudsightService implements Partial<WorldpayFraudsightService> {
  getFraudSightIdFromState(): Observable<string> {
    return of('fraudSightId');
  }
}

class MockGlobalMessageService implements Partial<GlobalMessageService> {
  get(): Observable<GlobalMessageEntities> {
    return of({});
  }

  add(): void {
  }

  remove(): void {
  }
}

class MockWorldpayACHFacade implements Partial<WorldpayACHFacade> {
  getACHPaymentFormValue(): Observable<ACHPaymentForm> {
    return of({
      accountType: 'checking',
      routingNumber: '123456789',
      accountNumber: '123456789',
      accountHolderName: 'user',
    });
  }
}

describe('WorldpayCheckoutPlaceOrderComponent', () => {
  let component: WorldpayCheckoutPlaceOrderComponent;
  let fixture: ComponentFixture<WorldpayCheckoutPlaceOrderComponent>;
  let controls: UntypedFormGroup['controls'];
  let orderFacade: WorldpayOrderService;
  let routingService: RoutingService;
  let launchDialogService: LaunchDialogService;
  let sanitizer: DomSanitizer;
  let placeOrderSpy: jasmine.Spy;
  let zone: NgZone;
  let winRef: WindowRef;
  let mockRenderer2: MockRenderer2;
  let worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService;
  let worldpayApmService: WorldpayApmService;
  let worldpayFraudsightService: WorldpayFraudsightService;
  let worldpayACHFacade: WorldpayACHFacade;
  let globalMessageService: GlobalMessageService;

  class MockWorldpayCheckoutPaymentService implements Partial<WorldpayCheckoutPaymentService> {
    getPaymentDetailsState(): Observable<any> {
      return of({
        loading: false,
        error: false,
        data: mockCreditCard
      });
    }

    getCseTokenFromState(): Observable<string> {
      return of('token');
    }

    getThreeDsDDCIframeUrlFromState(): Observable<SafeResourceUrl> {
      return of(sanitizer.bypassSecurityTrustResourceUrl('iframeUrl'));
    }

    getThreeDsChallengeIframeUrlFromState(): Observable<SafeResourceUrl> {
      return of(sanitizer.bypassSecurityTrustResourceUrl('https://centinelapistag.cardinalcommerce.com'));
    }

    getDDCInfoFromState(): Observable<ThreeDsDDCInfo> {
      return of({
        ddcUrl: 'https://centinelapistag.cardinalcommerce.com',
        jwt: 'jwt'
      });
    }

    getThreeDsChallengeInfoFromState(): Observable<ThreeDsInfo> {
      return of({
        threeDSFlexData: {
          autoSubmitThreeDSecureFlexUrl: 'https://autosubmiturl.aws.e2y.io',
          jwt: 'jwt',
          challengeUrl: 'https://challengeurl.aws.e2y.io',
          entry: []
        },
        merchantData: '123-456'
      });
    }

    setThreeDsChallengeIframeUrl(): void {

    }

    setThreeDsDDCIframeUrl(): void {

    }
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          RouterTestingModule,
          I18nTestingModule,
          FormErrorsModule,
        ],
        declarations: [MockUrlPipe, WorldpayCheckoutPlaceOrderComponent],
        providers: [
          {
            provide: WorldpayOrderService,
            useClass: MockWorldpayOrderService
          },
          ViewContainerRef,
          {
            provider: DomSanitizer,
            useValue: {
              bypassSecurityTrustResourceUrl: () => 'safeUrl'
            }
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
            provide: WorldpayApmConnector,
            useClass: OccWorldpayApmAdapter
          },
          {
            provide: WorldpayCheckoutPaymentAdapter,
            useClass: OccWorldpayCheckoutPaymentAdapter
          },
          {
            provide: GlobalMessageService,
            useClass: MockGlobalMessageService
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
            provide: WorldpayCheckoutPaymentService,
            useClass: MockWorldpayCheckoutPaymentService
          },
          {
            provide: WorldpayApmService,
            useClass: MockWorldpayApmService
          },
          {
            provide: WorldpayACHFacade,
            useClass: MockWorldpayACHFacade
          },
          {
            provide: WorldpayFraudsightService,
            useClass: MockWorldpayFraudsightService
          },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayCheckoutPlaceOrderComponent);
    component = fixture.componentInstance;
    controls = component.checkoutSubmitForm.controls;

    orderFacade = TestBed.inject(WorldpayOrderService);
    routingService = TestBed.inject(RoutingService);
    launchDialogService = TestBed.inject(LaunchDialogService);
    sanitizer = TestBed.inject(DomSanitizer);
    zone = TestBed.inject(NgZone);
    winRef = TestBed.inject(WindowRef);
    globalMessageService = TestBed.inject(GlobalMessageService);
    worldpayCheckoutPaymentService = TestBed.inject(WorldpayCheckoutPaymentService);
    worldpayApmService = TestBed.inject(WorldpayApmService);
    worldpayACHFacade = TestBed.inject(WorldpayACHFacade);
    placeOrderSpy = spyOn(component, 'placeWorldpayOrder');
    placeOrderSpy.and.callThrough();
    spyOn(orderFacade, 'executeDDC3dsJwtCommand').and.callThrough();
  });

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
      controls.termsAndConditions.setValue(true);
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

    it('should place order with iDeal', () => {
      spyOn(worldpayApmService, 'getAPMRedirectUrl').and.callThrough();
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: mockIdeal
      }));
      spyOn(worldpayApmService, 'selectAPM').and.callThrough();
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

      expect(worldpayApmService.getAPMRedirectUrl).toHaveBeenCalledWith({
        code: PaymentMethod.iDeal,
        name: 'ING',
      }, false);

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
    spyOn(worldpayApmService, 'getSelectedAPMFromState').and.callThrough();
    spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.callThrough();
    component.placeWorldpayOrder().subscribe((data) => {
      expect(data[0].code).toEqual(PaymentMethod.Card);
      expect(data[1].data).toEqual(mockCreditCard);
    });
    expect(worldpayApmService.getSelectedAPMFromState).toHaveBeenCalled();
    expect(worldpayCheckoutPaymentService.getPaymentDetailsState).toHaveBeenCalled();
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
    spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of(selectedApm));
    spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of(paymentDetailsState));
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
    const selectedApm1: ApmPaymentDetails = { code: PaymentMethod.GooglePay };
    const selectedApm2: ApmPaymentDetails = { code: PaymentMethod.PayPal };
    const paymentDetailsState1: QueryState<PaymentDetails> = {
      loading: false,
      error: false,
      data: {
        id: '00001'
      }
    };
    const paymentDetailsState2: QueryState<PaymentDetails> = {
      loading: false,
      error: false,
      data: {
        id: '00002'
      }
    };
    spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValues(of(selectedApm2));
    spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValues(of(paymentDetailsState1));

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
    spyOn(worldpayApmService, 'checkoutPreconditions').and.returnValue(throwError(new Error('Checkout conditions not met')));
    worldpayApmService.checkoutPreconditions().subscribe({
      error: (error) => {
        expect(error.message).toEqual('Checkout conditions not met');
      }
    });
  });

  it('should get CseToken from State', () => {
    let cseToken = null;
    spyOn(worldpayCheckoutPaymentService, 'getCseTokenFromState').and.callThrough();
    component.ngOnInit();
    expect(worldpayCheckoutPaymentService.getCseTokenFromState).toHaveBeenCalled();
    worldpayCheckoutPaymentService.getCseTokenFromState().subscribe(response => cseToken = response).unsubscribe();
    expect(cseToken).toEqual('token');
  });

  describe('Place order UI', () => {
    beforeEach(() => {
      component.ngOnInit();
      controls.termsAndConditions.setValue(true);
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
    controls.termsAndConditions.setValue(true);
    fixture.detectChanges();
    spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of({
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

  function submitForm(isTermsCondition: boolean): void {
    controls.termsAndConditions.setValue(isTermsCondition);
    component.submitForm();
  }
});
