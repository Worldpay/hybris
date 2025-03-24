import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UntypedFormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { Address, I18nTestingModule, MockTranslatePipe } from '@spartacus/core';
import { LaunchDialogService } from '@spartacus/storefront';
import { WorldpayApmService } from '@worldpay-services/worldpay-apm/worldpay-apm.service';
import { WorldpayApplepayService } from '@worldpay-services/worldpay-applepay/worldpay-applepay.service';
import { WorldpayGooglepayService } from '@worldpay-services/worldpay-googlepay/worldpay-googlepay.service';
import { MockCxSpinnerComponent, MockWorldpayBillingAddressComponent } from '@worldpay-tests/components';
import { MockLaunchDialogService } from '@worldpay-tests/services/launch-dialog.service.mock';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { ApmData, ApmPaymentDetails, GooglePayMerchantConfiguration, PaymentMethod } from '../../../core/interfaces';
import { WorldpayOrderService } from '../../../core/services';
import { WorldpayApmComponent } from './worldpay-apm.component';

const makeErrorsVisible = jasmine.createSpy('makeErrorsVisible').and.returnValue(null);

const mockBillingAddress: Address = {
  firstName: 'John',
  lastName: 'Doe',
  line1: 'Green Street',
  line2: '420',
  town: 'Montreal',
  postalCode: 'H3A',
  country: { isocode: 'CA' },
  region: { isocodeShort: 'QC' },
};

@Component({
  selector: 'y-worldpay-apm-googlepay',
  template: ''
})
class MockWorldpayApmGooglepayComponent {
  @Input() apm;
}

@Component({
  selector: 'y-worldpay-apm-tile',
  template: ''
})
class MockWorldpayApmTileComponent {
  @Input() apm: ApmData;
}

@Component({
  selector: 'y-worldpay-apm-ideal',
  template: ''
})
class MockWorldpayApmIdealComponent {
  @Input() apm: ApmData;
  @Output() setPaymentDetails = new EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }>();
}

@Component({
  selector: 'worldpay-applepay',
  template: ''
})
class MockWorldpayApplePayComponent {
}

@Component({
  selector: 'y-worldpay-apm-ach',
  template: ''
})
class MockWorldpayAPMACHComponent {
  @Input() apm;
}

class MockCheckoutBillingAddressFormService implements Partial<CheckoutBillingAddressFormService> {
  getBillingAddress(): Address {
    return mockBillingAddress;
  }

  isBillingAddressSameAsDeliveryAddress(): boolean {
    return true;
  }

  isBillingAddressFormValid(): boolean {
    return true;
  }

  getBillingAddressForm(): UntypedFormGroup {
    return new UntypedFormGroup({});
  }
}

describe('WorldpayApmComponent', () => {
  let component: WorldpayApmComponent;
  let fixture: ComponentFixture<WorldpayApmComponent>;
  let element: DebugElement;
  let worldpayApmService;
  let checkoutBillingAddressFormService: CheckoutBillingAddressFormService;
  let worldpayGooglepayService: WorldpayGooglepayService;
  let worldpayOrderService: WorldpayOrderService;
  let worldpayApplepayService: WorldpayApplepayService;

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
    { code: PaymentMethod.ACH },
  ];

  const merchantConfig: GooglePayMerchantConfiguration = {
    allowedAuthMethods: [],
    allowedCardNetworks: [],
    cardType: 'VISA',
    environment: 'test',
    gatewayMerchantId: '1234',
    merchantName: 'johnsnow',
    merchantId: '12122'
  };

  class MockWorldpayApmService implements Partial<WorldpayApmService> {
    selectAPM(): void {
    }

    getSelectedAPMFromState(): Observable<ApmData> {
      return apmSubject;
    }

    getWorldpayAvailableApmsFromState(): Observable<ApmData[]> {
      return of(apms);
    }

    getApmComponentById(): Observable<ApmData> {
      return of(apm);
    }

    getLoading(): Observable<boolean> {
      return of(false);
    }
  }

  class MockWorldpayGooglepayService implements Partial<WorldpayGooglepayService> {
    requestMerchantConfiguration(): void {
    }

    getMerchantConfigurationFromState(): Observable<GooglePayMerchantConfiguration> {
      return of(merchantConfig);
    }
  }

  class MockWorldpayOrderService {
    getLoading() {
      return of(false);
    }
  }

  class MockWorldpayApplepayService implements Partial<WorldpayApplepayService> {
    applePayButtonAvailable() {
      return true;
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [I18nTestingModule],
      declarations: [
        WorldpayApmComponent,
        MockTranslatePipe,
        MockWorldpayBillingAddressComponent,
        MockWorldpayApmGooglepayComponent,
        MockWorldpayApmTileComponent,
        MockWorldpayApmIdealComponent,
        MockCxSpinnerComponent,
        MockWorldpayApplePayComponent,
        MockWorldpayAPMACHComponent
      ],
      providers: [
        {
          provide: WorldpayApmService,
          useClass: MockWorldpayApmService
        },
        {
          provide: WorldpayGooglepayService,
          useClass: MockWorldpayGooglepayService
        },
        {
          provide: WorldpayOrderService,
          useClass: MockWorldpayOrderService
        },
        {
          provide: WorldpayApplepayService,
          useClass: MockWorldpayApplepayService
        },
        {
          provide: LaunchDialogService,
          useClass: MockLaunchDialogService
        },
        {
          provide: CheckoutBillingAddressFormService,
          useClass: MockCheckoutBillingAddressFormService,
        },
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApmComponent);
    component = fixture.componentInstance;
    element = fixture.debugElement;

    worldpayApmService = TestBed.inject(WorldpayApmService);
    checkoutBillingAddressFormService = TestBed.inject(CheckoutBillingAddressFormService);
    worldpayGooglepayService = TestBed.inject(WorldpayGooglepayService);
    worldpayOrderService = TestBed.inject(WorldpayOrderService);
    worldpayApplepayService = TestBed.inject(WorldpayApplepayService);
    apmSubject.next({
      code: PaymentMethod.Card
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngAfterViewInit', () => {
    it('should initialize component and set default card payment method', () => {
      // @ts-ignore
      spyOn(component, 'selectDefaultCardPaymentMethod').and.callThrough();
      component.ngAfterViewInit();
      expect(component['selectDefaultCardPaymentMethod']).toHaveBeenCalled();
    });

    it('should set sameAsDeliveryAddress based on checkoutBillingAddressFormService', () => {
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      component.ngAfterViewInit();
      expect(component.sameAsDeliveryAddress).toBeFalse();
    });

    it('should handle error during initialization', () => {
      const error = new Error('error');
      spyOn(console, 'error');
      spyOn(worldpayApmService.getSelectedAPMFromState(), 'subscribe').and.callFake(() => {
        throw error;
      });
      component.ngAfterViewInit();
      expect(console.error).toHaveBeenCalledWith('Failed to initialize FraudSight, check component configuration', error);
    });

    it('should initialize billing address form', () => {
      spyOn(checkoutBillingAddressFormService, 'getBillingAddressForm').and.callThrough();
      component.ngAfterViewInit();
      expect(checkoutBillingAddressFormService.getBillingAddressForm).toHaveBeenCalled();
    });

    it('should initialize Google Pay', () => {
      // @ts-ignore
      spyOn(component, 'initializeGooglePay').and.callThrough();
      component.ngAfterViewInit();
      expect(component['initializeGooglePay']).toHaveBeenCalled();
    });

    it('should initialize Apple Pay', () => {
      // @ts-ignore
      spyOn(component, 'initializeApplePay').and.callThrough();
      component.ngAfterViewInit();
      expect(component['initializeApplePay']).toHaveBeenCalled();
    });
  });

  describe('setSameAsDeliveryAddress', () => {
    it('should set sameAsDeliveryAddress to true', () => {
      component.setSameAsDeliveryAddress(true);
      expect(component.sameAsDeliveryAddress).toBeTrue();
    });

    it('should set sameAsDeliveryAddress to false', () => {
      component.setSameAsDeliveryAddress(false);
      expect(component.sameAsDeliveryAddress).toBeFalse();
    });
  });

  describe('showBillingFormAndContinueButton', () => {
    it('should return false for Card payment method', () => {
      expect(component.showBillingFormAndContinueButton(PaymentMethod.Card)).toBeFalse();
    });

    it('should return false for Google Pay payment method', () => {
      expect(component.showBillingFormAndContinueButton(PaymentMethod.GooglePay)).toBeFalse();
    });

    it('should return false for Apple Pay payment method', () => {
      expect(component.showBillingFormAndContinueButton(PaymentMethod.ApplePay)).toBeFalse();
    });

    it('should return false for iDeal payment method', () => {
      expect(component.showBillingFormAndContinueButton(PaymentMethod.iDeal)).toBeFalse();
    });

    it('should return false for ACH payment method', () => {
      expect(component.showBillingFormAndContinueButton(PaymentMethod.ACH)).toBeFalse();
    });

    it('should return true for unknown payment method', () => {
      expect(component.showBillingFormAndContinueButton('UnknownMethod')).toBeTrue();
    });
  });

  describe('selectApmPaymentDetails', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });
    it('should emit payment details and billing address when billing address is same as delivery address', () => {
      spyOn(component.setPaymentDetails, 'emit');
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);

      component.selectApmPaymentDetails();

      expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
        paymentDetails: {
          code: component['paymentDetails'].code,
          name: component['paymentDetails'].name,
        },
        billingAddress: undefined
      });
    });

    it('should emit payment details and billing address when billing address form is valid', () => {
      spyOn(component.setPaymentDetails, 'emit');
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressFormValid').and.returnValue(true);
      spyOn(checkoutBillingAddressFormService, 'getBillingAddress').and.returnValue(mockBillingAddress);

      component.selectApmPaymentDetails();

      expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
        paymentDetails: {
          code: component['paymentDetails'].code,
          name: component['paymentDetails'].name,
        },
        billingAddress: mockBillingAddress
      });
    });

    it('should make form errors visible and not emit payment details when billing address form is invalid', () => {
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressFormValid').and.returnValue(false);
      spyOn(component.setPaymentDetails, 'emit');

      component.selectApmPaymentDetails();
      expect(component.setPaymentDetails.emit).not.toHaveBeenCalled();
    });

    it('should set submitting state to true when billing address is same as delivery address', () => {
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);

      component.selectApmPaymentDetails();

      expect(component.submitting$.value).toBeTrue();
    });

    it('should set submitting state to true when billing address form is valid', () => {
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressFormValid').and.returnValue(true);

      component.selectApmPaymentDetails();

      expect(component.submitting$.value).toBeTrue();
    });
  });

  describe('selectDefaultCardPaymentMethod', () => {
    it('should select default card payment method when apm is not provided', () => {
      spyOn(worldpayApmService, 'selectAPM');
      component['selectDefaultCardPaymentMethod'](null);
      expect(worldpayApmService.selectAPM).toHaveBeenCalledWith({ code: PaymentMethod.Card });
    });

    it('should set payment details when apm is provided', () => {
      const mockApm: ApmData = {
        code: PaymentMethod.GooglePay,
        name: 'Google Pay'
      };
      component['selectDefaultCardPaymentMethod'](mockApm);
      expect(component['paymentDetails']).toEqual(mockApm);
    });
  });

  describe('initializeGooglePay', () => {
    it('should initialize Google Pay and set googlePay$ observable', () => {
      const mockApmData: ApmData = {
        code: PaymentMethod.GooglePay,
        name: 'Google Pay'
      };
      spyOn(worldpayApmService, 'getApmComponentById').and.returnValue(of(mockApmData));
      spyOn(worldpayGooglepayService, 'requestMerchantConfiguration');
      spyOn(worldpayGooglepayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));

      component['initializeGooglePay']();

      component.googlePay$.subscribe((apmData) => {
        expect(apmData).toEqual(mockApmData);
      });
      expect(worldpayGooglepayService.requestMerchantConfiguration).toHaveBeenCalled();
    });

    it('should not set googlePay$ observable if merchant configuration is not available', (doneFn) => {
      const mockApmData: ApmData = {
        code: PaymentMethod.GooglePay,
        name: 'Google Pay'
      };
      spyOn(console, 'error');
      spyOn(worldpayApmService, 'getApmComponentById').and.returnValue(of(mockApmData));
      spyOn(worldpayGooglepayService, 'requestMerchantConfiguration');
      spyOn(worldpayGooglepayService, 'getMerchantConfigurationFromState').and.returnValue(throwError(() => new Error('error')));

      component['initializeGooglePay']();

      component.googlePay$.subscribe({
        next: (apmData) => {
          expect(apmData).toBeUndefined();
          doneFn();
        },
        error: () => {
          doneFn();
        }
      });
      expect(worldpayGooglepayService.requestMerchantConfiguration).toHaveBeenCalled();
    });
  });

  describe('initializeApplePay', () => {
    it('should initialize Apple Pay and set applePay$ observable when Apple Pay button is available', () => {
      const mockApmData: ApmData = {
        code: PaymentMethod.ApplePay,
        name: 'Apple Pay'
      };
      spyOn(worldpayApplepayService, 'applePayButtonAvailable').and.returnValue(true);
      spyOn(worldpayApmService, 'getApmComponentById').and.returnValue(of(mockApmData));

      component['initializeApplePay']();

      component.applePay$.subscribe((apmData) => {
        expect(apmData).toEqual(mockApmData);
      });
    });

    it('should not set applePay$ observable when Apple Pay button is not available', () => {
      spyOn(worldpayApplepayService, 'applePayButtonAvailable').and.returnValue(false);
      spyOn(worldpayApmService, 'getApmComponentById').and.returnValue(of(undefined));

      component['initializeApplePay']();
      expect(worldpayApmService.getApmComponentById).not.toHaveBeenCalled();
      expect(component.applePay$).toBeUndefined();
    });
  });

  it('should not show spinner by default', () => {
    expect(element.query(By.css('cx-spinner'))).toBeFalsy();
  });

  it('should not show billing form for Card payments', () => {
    fixture = TestBed.createComponent(WorldpayApmComponent);
    component = fixture.componentInstance;
    element = fixture.debugElement;
    fixture.detectChanges();
    expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
  });

  it('should show billing form for APM', (done) => {
    apmSubject.next({
      code: PaymentMethod.PayPal
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeTruthy();
      done();
    });
  });

  it('should show component for Google Pay', (done) => {
    apmSubject.next({
      code: PaymentMethod.GooglePay
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-apm-googlepay'))).toBeTruthy();
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
      done();
    });
  });

  it('should show component for iDeal', (done) => {
    apmSubject.next({
      code: PaymentMethod.iDeal
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-apm-ideal'))).toBeTruthy();
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
      done();
    });
  });

  it('should show component for ACH', (done) => {
    apmSubject.next({
      code: PaymentMethod.ACH
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-apm-ach'))).toBeTruthy();
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
      done();
    });
  });

  it('should show component for Apple Pay', (done) => {
    apmSubject.next({
      code: PaymentMethod.ApplePay
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('worldpay-applepay'))).toBeTruthy();
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
      done();
    });
  });

  it('should trigger back functionality when an APM is selected', (done) => {
    spyOn(component, 'return').and.callThrough();
    apmSubject.next({
      code: 'KlARNA-SL'
    });
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      const backButton = fixture.debugElement.query(By.css('.btn-back'));
      expect(backButton).toBeTruthy();
      backButton.nativeElement.click();
      expect(component.return).toHaveBeenCalled();
      done();
    });
  });

  it('should hide back button when card payment method is selected', (done) => {
    spyOn(component, 'return').and.callThrough();
    apmSubject.next({
      code: PaymentMethod.Card
    });
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      const backButton = fixture.debugElement.query(By.css('.btn-back'));
      expect(backButton).toBeFalsy();
      done();
    });
  });

  it('should emit back event when return is called', () => {
    spyOn(component.back, 'emit');

    component.return();

    expect(component.back.emit).toHaveBeenCalled();
  });

});
