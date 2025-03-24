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
import { MockCxIconComponent, MockCxSpinnerComponent } from '@worldpay-tests/components';
import { MockGlobalMessageService } from '@worldpay-tests/services/global-message.service.mock';
import { BehaviorSubject, EMPTY, Observable, of, Subject, throwError } from 'rxjs';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../../core/interfaces';
import { WorldpayApmService, WorldpayCheckoutPaymentService } from '../../../core/services';
import { WorldpayCheckoutPaymentMethodComponent } from './worldpay-checkout-payment-method.component';
import createSpy = jasmine.createSpy;

@Component({
  selector: 'y-worldpay-apm-component',
  template: ''
})
class MockWorldpayApmComponent {
  @Input() apms: Observable<ApmData[]>;
  @Output() setPaymentDetails = new EventEmitter<any>();
  @Output() back = new EventEmitter<any>();
  @Input() processing: boolean;
}

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

class MockWorldpayCheckoutPaymentService implements Partial<WorldpayCheckoutPaymentService> {
  setPaymentDetails(details: PaymentDetails): Observable<unknown> {
    return of({});
  };

  createPaymentDetails(paymentDetails: PaymentDetails): Observable<unknown> {
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

  setPaymentAddress(_address: Address): Observable<Address> {
    return of({});
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

  setSaveCreditCardValue(value: boolean): void {

  }

  setSaveAsDefaultCardValue(value: boolean): void {

  }

  generateCseToken(_paymentDetails: PaymentDetails): string {
    return 'mock token';
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

class MockCheckoutStepService implements Partial<CheckoutStepService> {
  next = createSpy();
  back = createSpy();

  getBackBntText(): string {
    return 'common.back';
  }

  getCheckoutStep(): CheckoutStep {
    return {
      id: 'checkout',
      name: 'mockRoute',
      routeName: 'mockRoute',
      type: [CheckoutStepType.PAYMENT_DETAILS]
    };
  }
}

const mockActivatedRoute = {
  snapshot: {
    url: ['checkout', 'payment-method'],
  },
};

class MockActiveCartService implements Partial<ActiveCartFacade> {
  isGuestCart(): Observable<boolean> {
    return of(false);
  }
}

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

@Component({
  selector: 'wp-payment-form',
  template: '',
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
  getWorldpayAvailableApms(): Observable<any> {
    return of([]);
  }

  getPublicKey(): Observable<string> {
    return EMPTY;
  }

  getSelectedAPMFromState(): Observable<any> {
    return EMPTY;
  }

  setApmPaymentDetails(paymentDetails: ApmPaymentDetails): Observable<ApmPaymentDetails> {
    return of(mockPaymentDetails);
  }

  selectAPM(apm: ApmData): void {

  }

}

describe('WorldpayCheckoutPaymentMethodComponent', () => {
  let component: WorldpayCheckoutPaymentMethodComponent;
  let fixture: ComponentFixture<WorldpayCheckoutPaymentMethodComponent>;
  let mockUserPaymentService: UserPaymentService;
  let worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService;
  let mockActiveCartService: ActiveCartFacade;
  let checkoutStepService: CheckoutStepService;
  let globalMessageService: GlobalMessageService;
  let worldpayApmService: WorldpayApmService;

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
          WorldpayCheckoutPaymentMethodComponent,
          MockPaymentFormComponent,
          CardComponent,
          MockCxSpinnerComponent,
          MockCxIconComponent,
          MockWorldpayApmComponent
        ],
        providers: [
          {
            provide: UserPaymentService,
            useClass: MockUserPaymentService
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
            provide: WorldpayCheckoutPaymentService,
            useClass: MockWorldpayCheckoutPaymentService,
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
            provide: FeaturesConfig,
            useValue: {
              features: { level: '6.3' },
            },
          },
          {
            provide: WorldpayApmService,
            useClass: MockWorldpayApmService
          }
        ],
      }).compileComponents();

      mockUserPaymentService = TestBed.inject(UserPaymentService);
      worldpayCheckoutPaymentService = TestBed.inject(WorldpayCheckoutPaymentService);
      mockActiveCartService = TestBed.inject(ActiveCartFacade);
      worldpayApmService = TestBed.inject(WorldpayApmService);
      checkoutStepService = TestBed.inject(CheckoutStepService as Type<CheckoutStepService>);
      globalMessageService = TestBed.inject(GlobalMessageService);
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayCheckoutPaymentMethodComponent);
    component = fixture.componentInstance;

    spyOn(component, 'selectPaymentMethod').and.callThrough();
    spyOn<any>(component, 'savePaymentMethod').and.callThrough();
    spyOn(worldpayCheckoutPaymentService, 'useExistingPaymentDetails').and.callThrough();
    spyOn(worldpayCheckoutPaymentService, 'setPaymentDetails').and.callThrough();
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
      const mockPaymentDetails = { id: 'mockId' } as PaymentDetails;
      const mockBillingAddress = { id: 'mockAddressId' } as Address;
      spyOn(worldpayCheckoutPaymentService, 'createPaymentDetails').and.returnValue(of(mockPaymentDetails));
      spyOn(worldpayCheckoutPaymentService, 'setSaveCreditCardValue').and.callThrough();
      spyOn(worldpayCheckoutPaymentService, 'setSaveAsDefaultCardValue').and.callThrough();
      spyOn(component, 'hideNewPaymentForm').and.callThrough();
      spyOn(component, 'next').and.callThrough();

      component.setPaymentDetails({
        paymentDetails: mockPaymentDetails,
        billingAddress: mockBillingAddress
      });

      expect(worldpayCheckoutPaymentService.createPaymentDetails).toHaveBeenCalledWith({
        ...mockPaymentDetails,
        billingAddress: mockBillingAddress
      });
      expect(worldpayCheckoutPaymentService.setSaveCreditCardValue).toHaveBeenCalledWith(mockPaymentDetails.save);
      expect(worldpayCheckoutPaymentService.setSaveAsDefaultCardValue).toHaveBeenCalledWith(mockPaymentDetails.defaultPayment);
      expect(component.hideNewPaymentForm).toHaveBeenCalled();
      expect(component.next).toHaveBeenCalled();
    });

    it('should handle error when createPaymentDetails fails', () => {
      const mockPaymentDetails = { id: 'mockId' } as PaymentDetails;
      const mockBillingAddress = { id: 'mockAddressId' } as Address;
      spyOn(worldpayCheckoutPaymentService, 'createPaymentDetails').and.returnValue(throwError(() => new Error('Error')));
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
      spyOn(worldpayCheckoutPaymentService, 'createPaymentDetails').and.returnValue(of(null));
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
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(
        worldpayCheckoutPaymentService,
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
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));

      component.ngOnInit();
      fixture.detectChanges();

      expect(worldpayCheckoutPaymentService.setPaymentDetails).toHaveBeenCalledWith(mockPayments[1]);
    });

    it('should show form to add new payment method, when there are no existing methods and Card Payment method is selected', () => {
      component.isUpdating$ = of(false);
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of({
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
      spyOn(mockUserPaymentService, 'getPaymentMethodsLoading').and.returnValue(of(false));
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(selectedPaymentMethod$);
      spyOn(worldpayCheckoutPaymentService, 'createPaymentDetails').and.callThrough();
      spyOn(component, 'hideNewPaymentForm').and.callThrough();

      component.ngOnInit();
      fixture.detectChanges();

      component.setPaymentDetails({
        paymentDetails: mockPaymentDetails,
        billingAddress: mockAddress,
      });

      expect(worldpayCheckoutPaymentService.createPaymentDetails).toHaveBeenCalledWith({
        ...mockPaymentDetails,
        billingAddress: mockAddress,
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
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of([mockPaymentDetails]));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));

      component.ngOnInit();
      fixture.detectChanges();
      fixture.debugElement.queryAll(By.css('.btn-new-payment-method'))
        .filter((btn) => btn.nativeElement.innerText === 'paymentForm.addNewPayment')[0]
        .nativeElement.click();
      fixture.detectChanges();

      expect(fixture.debugElement.queryAll(By.css('cx-card')).length).toEqual(0);
      expect(fixture.debugElement.query(By.css('cx-spinner'))).toBeFalsy();
      expect(fixture.debugElement.query(By.css('wp-payment-form'))).toBeTruthy();
    });

    it('should show CVV error message', () => {
      const getContinueButton = () => {
        return fixture.debugElement.queryAll(By.css('.btn-primary')).filter((btn) => btn.nativeElement.innerText === 'common.continue')[0];
      };
      const getErrorMessage = () => {
        return fixture.debugElement.queryAll(By.css('.cVVNumber cx-form-errors'))[0];
      };

      const selectedPaymentMethod$ = new BehaviorSubject<QueryState<PaymentDetails | undefined>>({
        loading: false,
        error: false,
        data: undefined,
      });

      component.isUpdating$ = of(false);
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of([mockPaymentDetails]));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(selectedPaymentMethod$);

      component.ngOnInit();
      fixture.detectChanges();

      expect(getContinueButton().nativeElement.disabled).toBeTruthy();
      selectedPaymentMethod$.next({
        loading: false,
        error: false,
        data: mockPaymentDetails,
      });
      fixture.detectChanges();

      component.cvnForm.markAllAsTouched();
      fixture.detectChanges();
      expect(getErrorMessage().nativeElement.innerText).toEqual('formErrors.required');

      component.cvnForm.setValue({ cvn: '1' });
      component.cvnForm.markAllAsTouched();
      fixture.detectChanges();
      expect(getErrorMessage().nativeElement.innerText).toEqual('formErrors.minlength actualLength:1 requiredLength:3');
      expect(component.cvnForm.valid).toBeFalse();
      expect(getContinueButton().nativeElement.disabled).toBeFalse();
    });

    it('should have enabled button when there is selected method', () => {
      const getContinueButton = () => {
        return fixture.debugElement.queryAll(By.css('.btn-primary')).filter((btn) => btn.nativeElement.innerText === 'common.continue')[0];
      };

      const selectedPaymentMethod$ = new BehaviorSubject<QueryState<PaymentDetails | undefined>>({
        loading: false,
        error: false,
        data: undefined,
      });

      component.isUpdating$ = of(false);
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of([mockPaymentDetails]));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(selectedPaymentMethod$);

      component.ngOnInit();
      fixture.detectChanges();

      expect(getContinueButton().nativeElement.disabled).toBeTruthy();
      selectedPaymentMethod$.next({
        loading: false,
        error: false,
        data: mockPaymentDetails,
      });
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
        img: 'CREDIT_CARD',
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
        },
      ];
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: mockPaymentDetails
      }));
      spyOn(worldpayCheckoutPaymentService, 'setPaymentAddress').and.callThrough();

      component.ngOnInit();
      fixture.detectChanges();
      component.cvnForm.setValue({ cvn: '123' });
      fixture.detectChanges();
      fixture.debugElement
        .queryAll(By.css('cx-card'))[1]
        .query(By.css('.link'))
        .nativeElement.click();

      expect(worldpayCheckoutPaymentService.useExistingPaymentDetails).toHaveBeenCalledWith({
        ...mockPayments[1],
        save: true
      });
      expect(worldpayCheckoutPaymentService.setPaymentAddress).toHaveBeenCalledWith(mockPayments[1].billingAddress);
      expect(component.setSelectedPayment).toHaveBeenCalledWith({
        ...mockPayments[1],
        save: true
      });
    });

    it('should not try to load methods for guest checkout', () => {
      spyOn(mockUserPaymentService, 'loadPaymentMethods').and.stub();
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(mockActiveCartService, 'isGuestCart').and.returnValue(of(true));

      component.ngOnInit();

      expect(mockUserPaymentService.loadPaymentMethods).not.toHaveBeenCalled();
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
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of(mockPayments));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(
        of({
          loading: false,
          error: false,
          data: mockPaymentDetails
        })
      );

      component.ngOnInit();
      fixture.detectChanges();

      expect(worldpayCheckoutPaymentService.setPaymentDetails).not.toHaveBeenCalled();
    });

    it('should go to previous step after clicking back', () => {
      component.isUpdating$ = of(false);
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of([mockPaymentDetails]));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));

      component.ngOnInit();
      fixture.detectChanges();
      fixture.debugElement
        .queryAll(By.css('button'))
        .filter((btn) => btn.nativeElement.innerText === 'common.back')[0]
        .nativeElement.click();
      fixture.detectChanges();

      expect(checkoutStepService.back).toHaveBeenCalledWith(<any>mockActivatedRoute);
    });

    it('should be able to select payment method', () => {
      spyOn(worldpayCheckoutPaymentService, 'setPaymentAddress').and.callThrough();
      component.cvnForm.setValue({ cvn: '123' });
      fixture.detectChanges();
      component.selectPaymentMethod(mockPaymentDetails);

      expect(worldpayCheckoutPaymentService.setPaymentAddress).toHaveBeenCalledWith(mockPaymentDetails.billingAddress);
      expect(worldpayCheckoutPaymentService.useExistingPaymentDetails).toHaveBeenCalledWith(mockPaymentDetails);
      expect(component.setSelectedPayment).toHaveBeenCalledWith(mockPaymentDetails);
    });

    it('should NOT be able to select payment method if the selection is the same as the currently set payment details', () => {
      worldpayCheckoutPaymentService.getPaymentDetailsState = createSpy().and.returnValue(
        of({
          loading: false,
          error: false,
          data: mockPayments[0]
        })
      );
      component.cvnForm.setValue({ cvn: '123' });
      component.selectPaymentMethod(mockPayments[0]);

      expect(worldpayCheckoutPaymentService.setPaymentDetails).not.toHaveBeenCalledWith(mockPayments[0]);
      expect(component.setSelectedPayment).toHaveBeenCalledWith(mockPayments[0]);
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
      spyOn(worldpayCheckoutPaymentService, 'setPaymentAddress').and.returnValue(of({}));
      spyOn(worldpayApmService, 'setApmPaymentDetails').and.returnValue(of({}));
      // @ts-ignore
      spyOn(component, 'onSuccess').and.callThrough();
      spyOn(component, 'next').and.callThrough();

      component.setApmPaymentDetails(mockEvent);

      expect(worldpayCheckoutPaymentService.setPaymentAddress).toHaveBeenCalledWith(mockEvent.billingAddress);
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
      spyOn(worldpayCheckoutPaymentService, 'setPaymentAddress').and.returnValue(of({}));
      spyOn(worldpayApmService, 'setApmPaymentDetails').and.returnValue(throwError(() => new Error('Error')));
      // @ts-ignore
      spyOn(component, 'onError').and.callThrough();

      component.setApmPaymentDetails(mockEvent);

      expect(worldpayCheckoutPaymentService.setPaymentAddress).toHaveBeenCalledWith(mockEvent.billingAddress);
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
      spyOn(worldpayCheckoutPaymentService, 'setPaymentAddress').and.returnValue(of({}));
      spyOn(worldpayApmService, 'setApmPaymentDetails').and.returnValue(of({}));
      // @ts-ignore
      spyOn(component, 'onSuccess').and.callThrough();
      spyOn(component, 'next').and.callThrough();

      component.setApmPaymentDetails(mockEvent);

      expect(worldpayCheckoutPaymentService.setPaymentAddress).toHaveBeenCalledWith(component['deliveryAddress']);
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
      spyOn(mockUserPaymentService, 'getPaymentMethods').and.returnValue(of([]));
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: undefined
      }));
      component.ngOnInit();
      fixture.detectChanges();
      const creditCardForm = fixture.debugElement.query(By.css('wp-payment-form'));
      const cards = fixture.debugElement.query(By.css('cx-card'));
      const cvvForm = fixture.debugElement.query(By.css('.cVVNumber'));
      const apmComponent = fixture.debugElement.query(By.css('y-worldpay-apm-component'));

      expect(creditCardForm).toBeFalsy();
      expect(cvvForm).toBeFalsy();
      expect(cards).toBeFalsy();
      expect(apmComponent).toBeTruthy();
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

});
