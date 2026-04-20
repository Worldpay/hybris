import { Component, EventEmitter, Input, Output, Type } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CheckoutStepService } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutStep, CheckoutStepType, } from '@spartacus/checkout/base/root';
import {
  Address,
  FeatureConfigService,
  FeaturesConfig,
  FeaturesConfigModule,
  GlobalMessageService,
  GlobalMessageType,
  I18nTestingModule,
  PaymentDetails,
  QueryState,
  UserPaymentService,
} from '@spartacus/core';
import { CardComponent, FormErrorsModule } from '@spartacus/storefront';
import { BehaviorSubject, EMPTY, Observable, of, Subject, throwError } from 'rxjs';
import {
  MockActiveCartService,
  MockCheckoutStepService,
  MockCxFeatureDirective,
  MockCxIconComponent,
  MockCxSpinnerComponent,
  MockGlobalMessageService,
  MockWorldpayBillingAddressFormService
} from 'worldpay-sap-composable-tests';
import { ApmData, ApmNormalizer, ApmPaymentDetails, createCreditCardCard, PaymentMethod, WorldpayCheckoutPaymentFacade } from '../../../core';
import { WorldpayApmService, WorldpayBillingAddressFormService, WorldpayCheckoutPaymentService, WorldpayUserPaymentService } from '../../../core';
import { MockWorldpayApmComponent } from '../../../tests/components/worldpay-apm-component.mock';
import { WorldpayB2bCheckoutPaymentMethodComponent } from './worldpay-b2b-checkout-payment-method.component';
import createSpy = jasmine.createSpy;

const mockPaymentDetails: PaymentDetails = {
  id: 'mock payment id',
  accountHolderName: 'Name',
  cardNumber: '123456789',
  cardType: {
    code: 'Visa',
    name: 'Visa',
  },
  expiryMonth: '01',
  expiryYear: '2022',
  cvn: '123',
  code: PaymentMethod.Card,
  save: true
};
const mockAddress: Address = {
  id: 'mock address id',
  firstName: 'John',
  lastName: 'Doe',
  titleCode: 'mr',
  line1: 'Toyosaki 2 create on cart',
  line2: 'line2',
  town: 'town',
  region: { isocode: 'JP-27' },
  postalCode: 'zip',
  country: { isocode: 'JP' },
};

const mockPayments: PaymentDetails[] = [
  {
    id: 'non default method',
    accountHolderName: 'Name',
    cardNumber: '123456789',
    cardType: {
      code: 'Visa',
      name: 'Visa',
    },
    expiryMonth: '01',
    expiryYear: '2022',
    cvn: '123',
    code: PaymentMethod.Card
  },
  {
    id: 'default payment method',
    accountHolderName: 'Name',
    cardNumber: '123456789',
    cardType: {
      code: 'Visa',
      name: 'Visa',
    },
    expiryMonth: '01',
    expiryYear: '2022',
    cvn: '123',
    defaultPayment: true,
    code: PaymentMethod.Card
  },
  mockPaymentDetails,
];

class MockUserPaymentService implements Partial<UserPaymentService> {
  loadPaymentMethods(): void {
  }

  getPaymentMethods(): Observable<PaymentDetails[]> {
    return EMPTY;
  }

  getPaymentMethodsLoading(): Observable<boolean> {
    return EMPTY;
  }
}

class MockCheckoutPaymentService implements Partial<WorldpayCheckoutPaymentService> {
  setPaymentDetails(): Observable<unknown> {
    return of({});
  }

  createPaymentDetails(_paymentDetails: PaymentDetails): Observable<unknown> {
    return of({
      ...mockPaymentDetails,
      ...mockAddress
    });
  }

  getPaymentDetails(): Observable<PaymentDetails> {
    return of(mockPaymentDetails);
  }

  paymentProcessSuccess() {
  }

  getPaymentDetailsState(): Observable<QueryState<PaymentDetails | undefined>> {
    return EMPTY;
  }

  setPaymentAddress(_address: Address): Observable<unknown> {
    return EMPTY;
  }

  getPublicKey(): Observable<QueryState<string>> {
    return EMPTY;
  }

  getPublicKeyFromState(): Observable<string> {
    return EMPTY;
  }

  useExistingPaymentDetails(_paymentDetails: PaymentDetails): Observable<unknown> {
    return EMPTY;
  }

  setSaveCreditCardValue(): void {

  }

  setSaveAsDefaultCardValue(): void {

  }

  generateCseToken(_paymentDetails: PaymentDetails): Observable<string> {
    return of('mock token');
  }

  generatePublicKey(): Observable<string> {
    return of('public key');
  }

  setCseToken(_token: string): void {

  }
}

class MockCheckoutDeliveryFacade implements Partial<CheckoutDeliveryAddressFacade> {
  getDeliveryAddressState(): Observable<QueryState<Address | undefined>> {
    return of({
      loading: false,
      error: false,
      data: undefined
    });
  }
}

const mockActivatedRoute = {
  snapshot: {
    url: ['checkout', 'payment-method'],
  },
};

@Component({
  selector: 'wp-payment-form',
  template: '',
  standalone: false
})
class MockPaymentFormComponent {
  @Output() setPaymentDetails = new EventEmitter<any>;
  @Output() closeForm = new EventEmitter<any>;
  @Output() goBack = new EventEmitter<any>;
  @Input() paymentMethodsCount: number;
  @Input() setAsDefaultField: boolean;
  @Input() loading: boolean;
  @Input() paymentDetails: PaymentDetails;
}

class MockWorldpayApmService implements Partial<WorldpayApmService> {
  selectedApm$: BehaviorSubject<ApmPaymentDetails> = new BehaviorSubject<ApmPaymentDetails>(null);

  getWorldpayAvailableApms(): Observable<any> {
    return of([]);
  }

  getPublicKey(): Observable<string> {
    return EMPTY;
  }

  getSelectedAPMFromState(): Observable<any> {
    return EMPTY;
  }

  setApmPaymentDetails(): Observable<ApmPaymentDetails> {
    return of(mockPaymentDetails);
  }

  selectAPM(): void {

  }

}

class MockFeatureConfigService implements Partial<FeatureConfigService> {
  isEnabled(_feature: string) {
    return true;
  }
}

describe('WorldpayCheckoutPaymentMethodComponent', () => {
  let component: WorldpayB2bCheckoutPaymentMethodComponent;
  let fixture: ComponentFixture<WorldpayB2bCheckoutPaymentMethodComponent>;
  let userPaymentService: WorldpayUserPaymentService;
  let checkoutPaymentFacade: WorldpayCheckoutPaymentFacade;
  let activeCartFacade: ActiveCartFacade;
  let checkoutStepService: CheckoutStepService;
  let globalMessageService: GlobalMessageService;
  let worldpayApmService: WorldpayApmService;
  let worldpayBillingAddressFormService: WorldpayBillingAddressFormService;

  
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          I18nTestingModule,
          FeaturesConfigModule,
          ReactiveFormsModule,
          FormErrorsModule,
        ],
        declarations: [
          WorldpayB2bCheckoutPaymentMethodComponent,
          MockPaymentFormComponent,
          CardComponent,
          MockCxSpinnerComponent,
          MockCxIconComponent,
          MockWorldpayApmComponent,
          MockCxFeatureDirective,
        ],
        providers: [
          ApmNormalizer,
          {
            provide: WorldpayUserPaymentService,
            useClass: MockUserPaymentService
          },
          {
            provide: UserPaymentService,
            useExisting: WorldpayUserPaymentService
          },
          {
            provide: CheckoutDeliveryAddressFacade,
            useClass: MockCheckoutDeliveryFacade,
          },
          {
            provide: ActiveCartFacade,
            useClass: MockActiveCartService,
          },
          {
            provide: WorldpayCheckoutPaymentFacade,
            useClass: MockCheckoutPaymentService,
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
            provide: GlobalMessageService,
            useClass: MockGlobalMessageService
          },
          {
            // eslint-disable-next-line deprecation/deprecation
            provide: FeaturesConfig,
            useValue: {
              features: { level: '6.3' },
            },
          },
          {
            provide: WorldpayApmService,
            useClass: MockWorldpayApmService
          },
          {
            provide: FeatureConfigService,
            useClass: MockFeatureConfigService,
          },
          {
            provide: WorldpayBillingAddressFormService,
            useClass: MockWorldpayBillingAddressFormService
          }
        ],
      }).compileComponents();

      userPaymentService = TestBed.inject(WorldpayUserPaymentService);
      checkoutPaymentFacade = TestBed.inject(WorldpayCheckoutPaymentFacade);
      activeCartFacade = TestBed.inject(ActiveCartFacade);
      worldpayApmService = TestBed.inject(WorldpayApmService);
      checkoutStepService = TestBed.inject(CheckoutStepService as Type<CheckoutStepService>);
      globalMessageService = TestBed.inject(GlobalMessageService);
      worldpayBillingAddressFormService = TestBed.inject(WorldpayBillingAddressFormService);
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayB2bCheckoutPaymentMethodComponent);
    component = fixture.componentInstance;

    spyOn(component, 'selectPaymentMethod').and.callThrough();
    spyOn<any>(component, 'savePaymentMethod').and.callThrough();
    spyOn(checkoutPaymentFacade, 'useExistingPaymentDetails').and.callThrough();
    spyOn(checkoutPaymentFacade, 'setPaymentDetails').and.callThrough();
    spyOn(component, 'setSelectedPayment').and.callThrough();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize component and set shouldRedirect to false', () => {
      component.ngOnInit();
      expect(component.shouldRedirect).toBeFalse();
    });

    it('should set isCardPayment to true when selected APM is Card', () => {
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of({ code: PaymentMethod.Card }));
      component.ngOnInit();
      expect(component.isCardPayment).toBeTrue();
    });

    it('should set isCardPayment to false when selected APM is not Card', () => {
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of({ code: 'OtherAPM' }));
      component.ngOnInit();
      expect(component.isCardPayment).toBeFalse();
    });

    it('should handle error when fetching selected APM fails', () => {
      const mockError = 'Failed to fetch APM';
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(throwError(() => mockError));
      component.ngOnInit();
      expect(globalMessageService.add).toHaveBeenCalledWith({ raw: mockError }, GlobalMessageType.MSG_TYPE_ERROR);
    });

    it('should call listenToAvailableApmsAndProtectSelectedApm on initialization', () => {
      // @ts-ignore
      spyOn(component, 'listenToAvailableApmsAndProtectSelectedApm').and.callThrough();
      component.ngOnInit();
      expect(component['listenToAvailableApmsAndProtectSelectedApm']).toHaveBeenCalled();
    });
  });

  describe('setPaymentDetails', () => {
    it('should set payment details and redirect on success', () => {
      const mockBillingAddress = { id: 'mockAddressId' } as Address;
      spyOn(checkoutPaymentFacade, 'createPaymentDetails').and.returnValue(of(mockPaymentDetails));
      spyOn(checkoutPaymentFacade, 'setSaveCreditCardValue').and.callThrough();
      spyOn(checkoutPaymentFacade, 'setSaveAsDefaultCardValue').and.callThrough();
      spyOn(component, 'hideNewPaymentForm').and.callThrough();
      spyOn(component, 'next').and.callThrough();

      component.setPaymentDetails({
        paymentDetails: mockPaymentDetails,
        billingAddress: mockBillingAddress
      });

      expect(checkoutPaymentFacade.createPaymentDetails).toHaveBeenCalledWith({
        ...mockPaymentDetails,
        billingAddress: mockBillingAddress,
        saved: true
      });
      expect(checkoutPaymentFacade.setSaveCreditCardValue).toHaveBeenCalledWith(mockPaymentDetails.save);
      expect(checkoutPaymentFacade.setSaveAsDefaultCardValue).toHaveBeenCalledWith(mockPaymentDetails.defaultPayment);
      expect(component.hideNewPaymentForm).toHaveBeenCalled();
      expect(component.next).toHaveBeenCalled();
    });

    it('should handle error when createPaymentDetails fails', () => {
      const mockPaymentDetails = { id: 'mockId' } as PaymentDetails;
      const mockBillingAddress = { id: 'mockAddressId' } as Address;
      spyOn(checkoutPaymentFacade, 'createPaymentDetails').and.returnValue(throwError(() => new Error('Error')));
      // @ts-ignore
      spyOn(component, 'onError').and.callThrough();

      component.setPaymentDetails({
        paymentDetails: mockPaymentDetails,
        billingAddress: mockBillingAddress
      });

      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'paymentForm.invalid.applicationError' }, GlobalMessageType.MSG_TYPE_ERROR);
      // @ts-ignore
      expect(component['onError']).toHaveBeenCalled();
    });

    it('should handle null response from createPaymentDetails', () => {
      const mockPaymentDetails = { id: 'mockId' } as PaymentDetails;
      const mockBillingAddress = { id: 'mockAddressId' } as Address;
      spyOn(checkoutPaymentFacade, 'createPaymentDetails').and.returnValue(of(null));
      // @ts-ignore
      spyOn(component, 'onError').and.callThrough();

      component.setPaymentDetails({
        paymentDetails: mockPaymentDetails,
        billingAddress: mockBillingAddress
      });

      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.tokenizationFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      // @ts-ignore
      expect(component['onError']).toHaveBeenCalled();
    });
  });

  describe('Credit card Payment Method component behavior', () => {
    beforeEach(() => {
      component.isCardPayment = true;
    });

    it('should show loader during existing payment methods loading', () => {
      component.isUpdating$ = of(true);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(
        checkoutPaymentFacade,
        'getPaymentDetailsState'
      ).and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));

      component.ngOnInit();
      fixture.detectChanges();

      expect(fixture.debugElement.queryAll(By.css('cx-card')).length).toEqual(0);
      expect(fixture.debugElement.query(By.css('cx-spinner'))).toBeTruthy();
      expect(fixture.debugElement.query(By.css('wp-payment-form'))).toBeFalsy();
    });

    it('should select default payment method when nothing is selected', () => {
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));

      component.ngOnInit();
      component.cvnForm.setValue({ cvn: '123' });
      fixture.detectChanges();

      expect(checkoutPaymentFacade.setPaymentDetails).toHaveBeenCalledWith(mockPayments[1]);
      expect(component.setSelectedPayment).toHaveBeenCalledWith({
        ...mockPayments[1],
        cvn: '123',
        save: true
      });
    });

    it('should not select a default payment method if one is already selected', () => {
      const mockPaymentMethods = [
        {
          payment: {
            id: '1',
            defaultPayment: false
          },
          expiryTranslation: 'Expires 01/23'
        },
        {
          payment: {
            id: '2',
            defaultPayment: true
          },
          expiryTranslation: 'Expires 02/24'
        },
      ];
      const mockSelectedMethod = {
        id: '2',
        defaultPayment: true
      };

      component.selectDefaultPaymentMethod(mockPaymentMethods, mockSelectedMethod);

      expect(component.selectPaymentMethod).toHaveBeenCalledWith(mockSelectedMethod);
    });

    it('should not select any payment method if no default method exists', () => {
      const mockPaymentMethods = [
        {
          payment: {
            id: '1',
            defaultPayment: false
          },
          expiryTranslation: 'Expires 01/23'
        },
        {
          payment: {
            id: '2',
            defaultPayment: false
          },
          expiryTranslation: 'Expires 02/24'
        },
      ];
      const mockSelectedMethod = undefined;

      component.selectDefaultPaymentMethod(mockPaymentMethods, mockSelectedMethod);

      expect(component.selectPaymentMethod).not.toHaveBeenCalled();
    });

    it('should not attempt auto-selection more than once', () => {
      const mockPaymentMethods = [
        {
          payment: {
            id: '1',
            defaultPayment: false
          },
          expiryTranslation: 'Expires 01/23'
        },
        {
          payment: {
            id: '2',
            defaultPayment: true
          },
          expiryTranslation: 'Expires 02/24'
        },
      ];
      const mockSelectedMethod = undefined;

      component['doneAutoSelect'] = true;
      component.selectDefaultPaymentMethod(mockPaymentMethods, mockSelectedMethod);

      expect(component.selectPaymentMethod).not.toHaveBeenCalled();
    });

    it('should show form to add new payment method, when there are no existing methods and Card Payment method is selected', () => {
      component.isUpdating$ = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));

      component.ngOnInit();
      fixture.detectChanges();

      expect(fixture.debugElement.queryAll(By.css('cx-card')).length).toEqual(0);
      expect(fixture.debugElement.query(By.css('cx-spinner'))).toBeFalsy();
      expect(fixture.debugElement.query(By.css('wp-payment-form'))).toBeTruthy();
    });

    it('should create and select new payment method and redirect', () => {
      const selectedPaymentMethod$ = new Subject<QueryState<PaymentDetails | undefined>>();
      spyOn(userPaymentService, 'getPaymentMethodsLoading').and.returnValue(of(false));
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(selectedPaymentMethod$);
      spyOn(checkoutPaymentFacade, 'createPaymentDetails').and.callThrough();
      spyOn(component, 'hideNewPaymentForm').and.callThrough();

      component.ngOnInit();
      fixture.detectChanges();

      component.setPaymentDetails({
        paymentDetails: mockPaymentDetails,
        billingAddress: mockAddress,
      });

      expect(checkoutPaymentFacade.createPaymentDetails).toHaveBeenCalledWith({
        ...mockPaymentDetails,
        billingAddress: mockAddress,
        saved: true
      });
      expect(component.hideNewPaymentForm).toHaveBeenCalled();

      selectedPaymentMethod$.next({
        loading: false,
        error: false,
        data: mockPaymentDetails,
      });

      expect(checkoutStepService.next).toHaveBeenCalledWith(<any>mockActivatedRoute);
    });

    it('should show form for creating new method after clicking new payment method button', () => {
      component.isUpdating$ = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([mockPaymentDetails]));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));

      component.ngOnInit();
      fixture.detectChanges();
      const btn = fixture.debugElement.query(By.css('.btn-new-payment-method'));
      btn.nativeElement.click();
      fixture.detectChanges();

      expect(fixture.debugElement.queryAll(By.css('cx-card')).length).toEqual(0);
      expect(fixture.debugElement.query(By.css('cx-spinner'))).toBeFalsy();
      expect(fixture.debugElement.query(By.css('wp-payment-form'))).toBeTruthy();
    });

    it('should show CVV error message', () => {
      const getContinueButton = () => fixture.debugElement.queryAll(By.css('[data-test-id="continueBtn"]')).filter((btn) => btn.nativeElement.innerText === 'Common.Continue')[0];
      const getErrorMessage = () => fixture.debugElement.query(By.css('.cVVNumber cx-form-errors'));
      const selectedPaymentMethod$ = new BehaviorSubject<QueryState<PaymentDetails | undefined>>({
        loading: false,
        error: false,
        data: undefined,
      });

      component.isUpdating$ = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([mockPaymentDetails]));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(selectedPaymentMethod$);
      component.ngOnInit();
      fixture.detectChanges();

      expect(getContinueButton().nativeElement.disabled).toBeTruthy();
      worldpayApmService.selectedApm$.next(mockPaymentDetails);
      fixture.detectChanges();

      component.cvnForm.setValue({ cvn: '1' });
      component.cvnForm.markAllAsTouched();
      fixture.detectChanges();
      expect(getErrorMessage().nativeElement.innerText).toEqual('formErrors.labeled.minlength,formErrors.minlength actualLength:1 requiredLength:3');
      expect(component.cvnForm.valid).toBeFalse();
      expect(getContinueButton().nativeElement.disabled).toBeTrue();
    });

    it('should have enabled button when there is selected method', () => {
      const getContinueButton = () => fixture.debugElement.queryAll(By.css('.btn-primary')).filter((btn) => btn.nativeElement.innerText === 'Common.Continue')[0];

      const selectedPaymentMethod$ = new BehaviorSubject<QueryState<PaymentDetails | undefined>>({
        loading: false,
        error: false,
        data: mockPaymentDetails,
      });

      component.isUpdating$ = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([mockPaymentDetails]));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(selectedPaymentMethod$);

      component.ngOnInit();
      fixture.detectChanges();

      expect(getContinueButton().nativeElement.disabled).toBeTruthy();
      worldpayApmService.selectedApm$.next(mockPaymentDetails);
      component.cvnForm.setValue({ cvn: '123' });
      fixture.detectChanges();
      expect(getContinueButton().nativeElement.disabled).toBeFalse();
    });

    it('should display credit card info correctly', () => {
      const selectedPaymentMethod: PaymentDetails = {
        id: 'selected payment method',
        accountHolderName: 'Name',
        cardNumber: '123456789',
        cardType: {
          code: 'Visa',
          name: 'Visa',
        },
        expiryMonth: '01',
        expiryYear: '2022',
        cvn: '123',
        defaultPayment: true,
      };

      expect(
        component['createCard'](
          selectedPaymentMethod,
          {
            textDefaultPaymentMethod: '✓ DEFAULT',
            textExpires: 'Expires',
            textUseThisPayment: 'Use this payment',
            textSelected: 'Selected',
          },
          selectedPaymentMethod
        )
      ).toEqual({
        role: 'region',
        title: '✓ DEFAULT',
        textBold: 'Name',
        text: ['123456789', 'Expires'],
        img: 'VISA',
        actions: [{
          name: 'Use this payment',
          event: 'send'
        }],
        header: 'Selected',
        label: 'paymentCard.defaultPaymentLabel',
      });
    });

    it('should after each payment method selection change that in backend', () => {
      const mockPayments: PaymentDetails[] = [
        mockPaymentDetails,
        {
          id: 'default payment method',
          accountHolderName: 'Name',
          cardNumber: '123456789',
          cardType: {
            code: 'Visa',
            name: 'Visa',
          },
          expiryMonth: '01',
          expiryYear: '2022',
          cvn: '123',
          defaultPayment: true,
          code: PaymentMethod.Card,
        },
      ];
      spyOn(checkoutPaymentFacade, 'setPaymentAddress').and.callThrough();
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: mockPaymentDetails
      }));

      component.ngOnInit();
      fixture.detectChanges();
      component.cvnForm.setValue({ cvn: '123' });
      fixture.detectChanges();
      fixture.debugElement
        .queryAll(By.css('cx-card'))[1]
        .query(By.css('.link'))
        .nativeElement.click();

      expect(checkoutPaymentFacade.useExistingPaymentDetails).toHaveBeenCalledWith({
        ...mockPayments[1],
        save: true
      });
      expect(checkoutPaymentFacade.setPaymentAddress).toHaveBeenCalledWith(mockPayments[1].billingAddress);
      expect(component.setSelectedPayment).toHaveBeenCalledWith({
        ...mockPayments[1],
        save: true
      });
    });

    it('should not try to load methods for guest checkout', () => {
      spyOn(userPaymentService, 'loadPaymentMethods').and.stub();
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(true));

      component.ngOnInit();

      expect(userPaymentService.loadPaymentMethods).not.toHaveBeenCalled();
    });

    it('should show selected card, when there was previously selected method', () => {
      const mockPayments: PaymentDetails[] = [
        mockPaymentDetails,
        {
          id: 'default payment method',
          accountHolderName: 'Name',
          cardNumber: '123456789',
          cardType: {
            code: 'Visa',
            name: 'Visa',
          },
          expiryMonth: '01',
          expiryYear: '2022',
          cvn: '123',
          defaultPayment: true,
        },
      ];
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(
        of({
          loading: false,
          error: false,
          data: mockPaymentDetails
        })
      );

      component.ngOnInit();
      fixture.detectChanges();

      expect(checkoutPaymentFacade.setPaymentDetails).not.toHaveBeenCalled();
    });

    it('should go to previous step after clicking back', () => {
      component.isUpdating$ = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([mockPaymentDetails]));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));

      component.ngOnInit();
      fixture.detectChanges();
      fixture.debugElement
        .queryAll(By.css('button'))
        .filter((btn) => btn.nativeElement.innerText === 'Common.Back')[0]
        .nativeElement.click();
      fixture.detectChanges();

      expect(checkoutStepService.back).toHaveBeenCalledWith(<any>mockActivatedRoute);
    });

    it('should be able to select payment method', () => {
      spyOn(checkoutPaymentFacade, 'setPaymentAddress').and.callThrough();
      component.cvnForm.setValue({ cvn: '123' });
      fixture.detectChanges();
      component.selectPaymentMethod(mockPaymentDetails);

      expect(checkoutPaymentFacade.setPaymentAddress).toHaveBeenCalledWith(mockPaymentDetails.billingAddress);
      expect(checkoutPaymentFacade.useExistingPaymentDetails).toHaveBeenCalledWith(mockPaymentDetails);
      expect(component.setSelectedPayment).toHaveBeenCalledWith(mockPaymentDetails);
    });

    it('should NOT be able to select payment method if the selection is the same as the currently set payment details', () => {
      const paymentDetails = {
        ...mockPaymentDetails[0],
        billingAddress: mockAddress
      };
      spyOn(worldpayBillingAddressFormService, 'updateSameAsDeliveryAddressFormData').and.callThrough();
      checkoutPaymentFacade.getPaymentDetailsState = createSpy().and.returnValue(
        of({
          loading: false,
          error: false,
          data: paymentDetails
        })
      );
      component.cvnForm.setValue({ cvn: '123' });
      component.selectPaymentMethod(paymentDetails);

      expect(checkoutPaymentFacade.setPaymentDetails).not.toHaveBeenCalledWith(paymentDetails);
      expect(component.setSelectedPayment).toHaveBeenCalledWith({
        ...paymentDetails,
        cvn: '123'
      });
      expect(worldpayBillingAddressFormService.updateSameAsDeliveryAddressFormData).toHaveBeenCalledWith(paymentDetails.billingAddress, component['deliveryAddress']);
    });
  });

  describe('getCheckoutStepUrl', () => {
    it('should return the correct route name for a given step type', () => {
      const mockStep = { routeName: 'mockRoute' } as CheckoutStep;
      spyOn(checkoutStepService, 'getCheckoutStep').and.returnValue(mockStep);

      const result = component.getCheckoutStepUrl(CheckoutStepType.PAYMENT_DETAILS);

      expect(result).toBe('mockRoute');
    });

    it('should return undefined if the step is not found', () => {
      spyOn(checkoutStepService, 'getCheckoutStep').and.returnValue(undefined);

      const result = component.getCheckoutStepUrl(CheckoutStepType.PAYMENT_DETAILS);

      expect(result).toBeUndefined();
    });
  });

  describe('setApmPaymentDetails', () => {
    it('should set APM payment details and handle success response', () => {
      const mockEvent = {
        paymentDetails: { id: 'mockId' } as ApmPaymentDetails,
        billingAddress: { id: 'mockAddressId' } as Address
      };
      spyOn(checkoutPaymentFacade, 'setPaymentAddress').and.returnValue(of({}));
      spyOn(worldpayApmService, 'setApmPaymentDetails').and.returnValue(of({}));
      // @ts-ignore
      spyOn(component, 'onSuccess').and.callThrough();
      spyOn(component, 'next').and.callThrough();

      component.setApmPaymentDetails(mockEvent);

      expect(checkoutPaymentFacade.setPaymentAddress).toHaveBeenCalledWith(mockEvent.billingAddress);
      expect(worldpayApmService.setApmPaymentDetails).toHaveBeenCalledWith({
        ...mockEvent.paymentDetails,
        billingAddress: mockEvent.billingAddress
      });
      expect(component['onSuccess']).toHaveBeenCalled();
      expect(component.next).toHaveBeenCalled();
    });

    it('should handle error response when setting APM payment details', () => {
      const mockEvent = {
        paymentDetails: { id: 'mockId' } as ApmPaymentDetails,
        billingAddress: { id: 'mockAddressId' } as Address
      };
      spyOn(checkoutPaymentFacade, 'setPaymentAddress').and.returnValue(of({}));
      spyOn(worldpayApmService, 'setApmPaymentDetails').and.returnValue(throwError(() => new Error('Error')));
      // @ts-ignore
      spyOn(component, 'onError').and.callThrough();

      component.setApmPaymentDetails(mockEvent);

      expect(checkoutPaymentFacade.setPaymentAddress).toHaveBeenCalledWith(mockEvent.billingAddress);
      expect(worldpayApmService.setApmPaymentDetails).toHaveBeenCalledWith({
        ...mockEvent.paymentDetails,
        billingAddress: mockEvent.billingAddress
      });
      expect(component['onError']).toHaveBeenCalled();
    });

    it('should use delivery address if billing address is not provided', () => {
      const mockEvent = {
        paymentDetails: { id: 'mockId' } as ApmPaymentDetails,
        billingAddress: null
      };
      component['deliveryAddress'] = { id: 'deliveryAddressId' } as Address;
      spyOn(checkoutPaymentFacade, 'setPaymentAddress').and.returnValue(of({}));
      spyOn(worldpayApmService, 'setApmPaymentDetails').and.returnValue(of({}));
      // @ts-ignore
      spyOn(component, 'onSuccess').and.callThrough();
      spyOn(component, 'next').and.callThrough();

      component.setApmPaymentDetails(mockEvent);

      expect(checkoutPaymentFacade.setPaymentAddress).toHaveBeenCalledWith(component['deliveryAddress']);
      expect(worldpayApmService.setApmPaymentDetails).toHaveBeenCalledWith({
        ...mockEvent.paymentDetails,
        billingAddress: component['deliveryAddress']
      });
      expect(component['onSuccess']).toHaveBeenCalled();
      expect(component.next).toHaveBeenCalled();
    });
  });

  describe('APM component behavior', () => {
    beforeEach(() => {
      component.isCardPayment = false;
      component.isUpdating$ = of(false);
    });

    it('should not show credit card info', () => {
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));
      component.ngOnInit();
      fixture.detectChanges();
      const creditCardForm = fixture.debugElement.query(By.css('wp-payment-form'));
      const cards = fixture.debugElement.query(By.css('cx-card'));
      const cvvForm = fixture.debugElement.query(By.css('.cVVNumber'));
      const apmComponent = fixture.debugElement.query(By.directive(MockWorldpayApmComponent));

      expect(creditCardForm).toBeFalsy();
      expect(cvvForm).toBeFalsy();
      expect(cards).toBeFalsy();
      expect(apmComponent).toBeTruthy();
    });
  });

  describe('selectDefaultPaymentMethod', () => {
    it('should select the default payment method when no method is selected', () => {
      const mockPaymentMethods = [
        {
          payment: {
            id: '1',
            defaultPayment: false
          },
          expiryTranslation: 'Expires 01/23'
        },
        {
          payment: {
            id: '2',
            defaultPayment: true
          },
          expiryTranslation: 'Expires 02/24'
        },
      ];
      const mockSelectedMethod = undefined;
      component.selectDefaultPaymentMethod(mockPaymentMethods, mockSelectedMethod);

      expect(component.selectPaymentMethod).toHaveBeenCalledWith(mockPaymentMethods[1].payment);
      expect(component['savePaymentMethod']).toHaveBeenCalledWith(mockPaymentMethods[1].payment);
    });
  });

  describe('listenToAvailableApmsAndProtectSelectedApm', () => {
    it('should select Card payment method if selected APM is not available', () => {
      const mockApms = [{ code: 'APM1' }] as ApmData[];
      const mockSelectedApm = { code: 'APM2' } as ApmData;
      spyOn(worldpayApmService, 'getWorldpayAvailableApms').and.returnValue(of(mockApms));
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of(mockSelectedApm));
      spyOn(worldpayApmService, 'selectAPM').and.callThrough();

      component['listenToAvailableApmsAndProtectSelectedApm']();

      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'paymentForm.apmChanged' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(worldpayApmService.selectAPM).toHaveBeenCalledWith({ code: PaymentMethod.Card });
    });

    it('should not change selected APM if it is available', () => {
      const mockApms = [{ code: 'APM1' }] as ApmData[];
      const mockSelectedApm = { code: 'APM1' } as ApmData;
      spyOn(worldpayApmService, 'getWorldpayAvailableApms').and.returnValue(of(mockApms));
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of(mockSelectedApm));
      spyOn(worldpayApmService, 'selectAPM').and.callThrough();

      component['listenToAvailableApmsAndProtectSelectedApm']();

      expect(globalMessageService.add).not.toHaveBeenCalled();
      expect(worldpayApmService.selectAPM).not.toHaveBeenCalled();
    });

    it('should handle error when fetching available APMs or selected APM fails', () => {
      const mockError = 'Failed to fetch APMs';
      spyOn(worldpayApmService, 'getWorldpayAvailableApms').and.returnValue(throwError(() => mockError));
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of({ code: 'APM1' } as ApmData));

      component['listenToAvailableApmsAndProtectSelectedApm']();

      expect(globalMessageService.add).toHaveBeenCalledWith({ raw: mockError }, GlobalMessageType.MSG_TYPE_ERROR);
    });

    it('should not change selected APM if it is Card, ApplePay, GooglePay, or PayPal', () => {
      const mockApms = [{ code: 'APM1' }] as ApmData[];
      const mockSelectedApm = { code: PaymentMethod.Card } as ApmData;
      spyOn(worldpayApmService, 'getWorldpayAvailableApms').and.returnValue(of(mockApms));
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of(mockSelectedApm));
      spyOn(worldpayApmService, 'selectAPM').and.callThrough();

      component['listenToAvailableApmsAndProtectSelectedApm']();

      expect(globalMessageService.add).not.toHaveBeenCalled();
      expect(worldpayApmService.selectAPM).not.toHaveBeenCalled();
    });
  });

  describe('disableContinueButton', () => {
    const getContinueButton = () => fixture.debugElement.queryAll(By.css('[data-test-id="continueBtn"]')).filter((btn) => btn.nativeElement.innerText === 'Common.Continue')[0];

    it('should return true when busy is true', (done) => {
      component['isUpdating$'] = of(true);
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: null
      }));
      worldpayApmService.selectedApm$.next(null);
      component.cvnForm.patchValue({ cvn: '' });

      component.ngOnInit();
      fixture.detectChanges();

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeTrue();
        expect(getContinueButton()).toBeUndefined();
        done();
      });
    });

    it('should return false when cvnForm is valid and selected method is Card', (done) => {
      component['isUpdating$'] = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: mockPaymentDetails
      }));
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of({ code: PaymentMethod.Card }));
      component.cvnForm.patchValue({ cvn: '123' });

      component.ngOnInit();
      fixture.detectChanges();

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeFalse();
        expect(getContinueButton().nativeElement.disabled).toBeFalse();
        done();
      });
    });

    it('should return false when cvnForm is valid and selected method is Card and it is an APM', (done) => {
      component['isUpdating$'] = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: {
          ...mockPaymentDetails,
          isAPM: true,
        }
      }));
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of({ code: PaymentMethod.Card }));

      component.ngOnInit();
      fixture.detectChanges();

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeFalse();
        expect(getContinueButton().nativeElement.disabled).toBeFalse();
        done();
      });
    });

    it('should return true when cvnForm is invalid and selected method is Card', (done) => {
      component['isUpdating$'] = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: mockPaymentDetails
      }));
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of({ code: PaymentMethod.Card }));
      component.cvnForm.patchValue({ cvn: '' });

      component.ngOnInit();
      fixture.detectChanges();

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeTrue();
        expect(getContinueButton().nativeElement.disabled).toBeTrue();
        done();
      });
    });

    it('should return true when no selected method or payment is provided', (done) => {
      component['isUpdating$'] = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: null
      }));
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of(null));
      component.cvnForm.patchValue({ cvn: '123' });

      component.ngOnInit();
      fixture.detectChanges();

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeTrue();
        expect(getContinueButton()).toBeUndefined();
        done();
      });
    });

    it('should return true when a non-Card payment method is selected', (done) => {
      component['isUpdating$'] = of(false);
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: mockPaymentDetails
      }));
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of({ code: PaymentMethod.ACH }));
      component.cvnForm.patchValue({ cvn: '' });

      component.ngOnInit();
      fixture.detectChanges();

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeTrue();
        expect(component.isCardPayment).toBeFalse();
        expect(getContinueButton()).toBeUndefined();
        done();
      });
    });
  });

  describe('createCreditCardCard', () => {
    beforeEach(() => {
      component['isUpdating$'] = of(false);
      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: mockPaymentDetails
      }));
      component.isCardPayment = true;
      spyOn(worldpayApmService, 'getSelectedAPMFromState').and.returnValue(of(mockPaymentDetails));
    });

    it('returns formatted cardholder name and card details when all fields are provided', () => {
      const paymentDetails = {
        accountHolderName: 'John Doe',
        cardNumber: '**** **** **** 1234',
        expiryMonth: '12',
        expiryYear: '24',
      };
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([...mockPayments, paymentDetails]));
      const result = createCreditCardCard(paymentDetails, `Expires ${paymentDetails.expiryMonth}/${paymentDetails.expiryYear}`);

      expect(result).toEqual({
        textBold: 'John Doe',
        text: ['**** **** **** 1234', 'Expires 12/24'],
      });

      component.ngOnInit();
      fixture.detectChanges();

      const savedAPMCard = fixture.debugElement.queryAll(By.css('cx-card')).find(card => card.nativeElement.textContent.includes('John Doe'));

      expect(savedAPMCard).toBeDefined();
      expect(savedAPMCard.nativeElement.textContent.includes('John Doe')).toBeTrue();
      expect(savedAPMCard.nativeElement.textContent.includes('paymentCard.expires month:12 year:24')).toBeTrue();
      expect(savedAPMCard.nativeElement.textContent.includes('**** **** **** 1234')).toBeTrue();
    });

    it('returns empty strings when cardholder name and card number are missing', () => {
      const paymentDetails = {};
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([...mockPayments, paymentDetails]));
      const result = createCreditCardCard(paymentDetails, undefined);

      expect(result).toEqual({
        textBold: undefined,
        text: [],
      });

      const savedAPMCard = fixture.debugElement.queryAll(By.css('cx-card')).find(card => card.nativeElement.textContent.includes('John Doe'));

      expect(savedAPMCard).toBeUndefined();
    });

    it('returns only expiration text when card number is missing', () => {
      const paymentDetails = {
        accountHolderName: 'John Doe',
        expiryMonth: '12',
        expiryYear: '24',
      };
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([...mockPayments, paymentDetails]));
      const result = createCreditCardCard(paymentDetails, `Expires ${paymentDetails.expiryMonth}/${paymentDetails.expiryYear}`);

      expect(result).toEqual({
        textBold: 'John Doe',
        text: ['Expires 12/24'],
      });

      component.ngOnInit();
      fixture.detectChanges();

      const savedAPMCard = fixture.debugElement.queryAll(By.css('cx-card')).find(card => card.nativeElement.textContent.includes('John Doe'));

      expect(savedAPMCard).toBeDefined();
      expect(savedAPMCard.nativeElement.textContent.includes('John Doe')).toBeTrue();
      expect(savedAPMCard.nativeElement.textContent.includes('paymentCard.expires month:12 year:24')).toBeTrue();
      expect(savedAPMCard.nativeElement.textContent.includes('**** **** **** 1234')).toBeFalse();
    });

    it('returns only cardholder name when expiration text is empty', () => {
      const paymentDetails = {
        accountHolderName: 'John Doe',
        cardNumber: '**** **** **** 1234',
      };
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([...mockPayments, paymentDetails]));
      const result = createCreditCardCard(paymentDetails, 'Expire /');

      expect(result).toEqual({
        textBold: 'John Doe',
        text: ['**** **** **** 1234', 'Expire /'],
      });

      component.ngOnInit();
      fixture.detectChanges();

      const savedAPMCard = fixture.debugElement.queryAll(By.css('cx-card')).find(card => card.nativeElement.textContent.includes('John Doe'));

      expect(savedAPMCard).toBeDefined();
      expect(savedAPMCard.nativeElement.textContent.includes('John Doe')).toBeTrue();
      expect(savedAPMCard.nativeElement.textContent.includes('paymentCard.expires month:12 year:24')).toBeFalse();
      expect(savedAPMCard.nativeElement.textContent.includes('**** **** **** 1234')).toBeTrue();
    });

    it('returns APM response and data', () => {
      const paymentDetails = {
        apmName: 'Sepa',
        billingAddress: mockAddress,
        isAPM: true,
        expiryMonth: '12',
        expiryYear: '24',
      };
      spyOn(userPaymentService, 'getPaymentMethods').and.returnValue(of([...mockPayments, paymentDetails]));
      const result = createCreditCardCard(paymentDetails, `Expires ${paymentDetails.expiryMonth}/${paymentDetails.expiryYear}`);

      expect(result).toEqual({
        textBold: 'Sepa',
        text: ['Expires 12/24'],
      });

      component.ngOnInit();
      fixture.detectChanges();

      const savedAPMCard = fixture.debugElement.queryAll(By.css('cx-card')).find(card => card.nativeElement.textContent.includes('Sepa'));
      expect(savedAPMCard).toBeDefined();
      expect(savedAPMCard.nativeElement.textContent.includes('paymentCard.expires month:12 year:24')).toBeTrue();
    });
  });
});
