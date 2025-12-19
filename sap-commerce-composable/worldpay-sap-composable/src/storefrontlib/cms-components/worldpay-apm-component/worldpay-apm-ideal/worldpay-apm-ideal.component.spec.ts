import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { Address, I18nTestingModule, LoggerService, MockTranslatePipe } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { BehaviorSubject, of } from 'rxjs';
import { WorldpayApmSubmitButtonsComponent } from 'worldpay-sap-composable-components';
import { WorldpayConnector } from 'worldpay-sap-composable-connectors';
import { WorldpayBillingAddressFormService } from 'worldpay-sap-composable-services';
import { MockWorldpayBillingAddressComponent, MockWorldpayConnector } from 'worldpay-sap-composable-tests';
import { PaymentMethod } from 'worldpay-sap-core';
import { WorldpayApmIdealComponent } from './worldpay-apm-ideal.component';

describe('WorldpayApmIdealComponent', () => {
  let component: WorldpayApmIdealComponent;
  let fixture: ComponentFixture<WorldpayApmIdealComponent>;
  let billingAddressFormService: WorldpayBillingAddressFormService;

  const billingAddress: Address = {
    firstName: 'John',
    lastName: 'Doe',
    line1: 'Line 1',
    line2: 'Line 2',
    town: 'Town',
    country: { isocode: 'US' },
    postalCode: '12345',
    region: {
      isocodeShort: 'UK'
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        WorldpayApmIdealComponent,
        MockTranslatePipe,
        MockWorldpayBillingAddressComponent,
        WorldpayApmSubmitButtonsComponent,
      ],
      providers: [
        UntypedFormBuilder,
        WorldpayBillingAddressFormService,
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector
        },
        LoggerService,
      ],
      imports: [
        I18nTestingModule,
        FormErrorsModule,
        ReactiveFormsModule,
        NgSelectModule,
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApmIdealComponent);
    billingAddressFormService = TestBed.inject(WorldpayBillingAddressFormService);
    component = fixture.componentInstance;
    component.ngOnInit();
    fixture.detectChanges();
  });

  const regenerateComponent: (sameAsDeliveryAddress: boolean) => WorldpayApmIdealComponent = (sameAsDeliveryAddress: boolean): WorldpayApmIdealComponent => {
    let sameAsDeliveryAddress$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(sameAsDeliveryAddress);
    spyOn(billingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(sameAsDeliveryAddress$.asObservable());
    fixture = TestBed.createComponent(WorldpayApmIdealComponent);
    return fixture.componentInstance;
  };

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('APM bank configurations is empty', () => {
    beforeEach(() => {
      component.apm = {
        code: PaymentMethod.iDeal,
        bankConfigurations: [],
      };
      fixture.detectChanges();
    });

    it('should not render bank select', () => {
      expect(document.querySelector('ng-select')).toBeFalsy();
    });
  });

  describe('APM bank configuration list', () => {
    beforeEach(() => {
      component.apm = {
        code: PaymentMethod.iDeal,
        bankConfigurations: [
          {
            code: 'ING',
            name: 'ING'
          },
          {
            code: 'RABOBANK',
            name: 'RABO BANK'
          },
        ],
      };
      component.ngOnInit();
      fixture.detectChanges();
    });

    it('should validate ideal form', () => {
      component.idealForm.get('bank').setValue({
        code: null
      });
      component.ngOnInit();
      fixture.detectChanges();

      expect(document.querySelector('button[data-test-id="ideal-continue-btn"]')['disabled']).toEqual(true);
    });

    it('should validate the form before submitting', () => {
      component = regenerateComponent(true);
      component.idealForm.get('bank').setValue({
        code: 'ING'
      });

      spyOn(component.setPaymentDetails, 'emit').and.callThrough();

      component.next();

      expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
        paymentDetails: {
          shopperBankCode: 'ING',
          code: PaymentMethod.iDeal,
        },
        billingAddress: undefined
      });
    });

    it('should prevent button click when billing address is invalid', () => {
      let sameAsDeliveryAddress$ = new BehaviorSubject<boolean>(false);
      spyOn(billingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(sameAsDeliveryAddress$.asObservable());
      fixture = TestBed.createComponent(WorldpayApmIdealComponent);
      component = fixture.componentInstance;
      fixture.detectChanges(); // triggers ngOnInit
      component.idealForm.get('bank').get('code').setValue('RABOBANK');
      component.ngOnInit();
      component['billingAddressForm'].reset();
      component['billingAddressForm'].markAllAsTouched();
      fixture.detectChanges();
      expect(document.querySelector('button[data-test-id="ideal-continue-btn"]')['disabled']).toEqual(true);
    });

    it('should validate the billing address if unchecked billing checkbox', () => {
      component.idealForm.get('bank').setValue({ code: 'RABOBANK' });
      component = regenerateComponent(false);
      component.ngOnInit();
      component['billingAddressForm'].setValue({
        firstName: 'john',
        lastName: 'doe',
        line1: 'line1',
        line2: 'line2',
        town: 'town',
        country: { isocode: 'US' },
        postalCode: '12345',
        region: {
          isocodeShort: 'UK'
        }
      });

      fixture.detectChanges();

      expect(document.querySelector('button[data-test-id="ideal-continue-btn"]')['disabled']).toEqual(false);
    });
  });

  it('should initialize billing address form on init', () => {
    spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(new UntypedFormGroup({}));
    component.ngOnInit();
    expect(component['billingAddressForm']).toBe(billingAddressFormService.getBillingAddressForm());
  });

  it('should emit payment details and billing address when form is valid and sameAsShippingAddress is false', () => {
    spyOn(billingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
    spyOn(billingAddressFormService, 'isBillingAddressFormValid').and.returnValue(true);
    spyOn(billingAddressFormService, 'getBillingAddress').and.returnValue(billingAddress);
    spyOn(component.setPaymentDetails, 'emit');

    billingAddressFormService.setBillingAddress(billingAddress);

    billingAddressFormService.setBillingAddress(billingAddress);
    component.idealForm.setValue({ bank: { code: 'ING' } });
    component.next();

    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: {
        code: PaymentMethod.iDeal,
        shopperBankCode: 'ING'
      },
      billingAddress
    });
  });

  it('should emit payment details without billing address when form is valid and sameAsShippingAddress is true', () => {
    // @ts-ignore
    spyOn(billingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
    spyOn(component.setPaymentDetails, 'emit');
    billingAddressFormService.setSameAsDeliveryAddress(true);

    component.idealForm.setValue({ bank: { code: 'ING' } });
    component.next();

    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: {
        code: PaymentMethod.iDeal,
        shopperBankCode: 'ING'
      },
      billingAddress: undefined
    });
  });

  describe('disableContinueButton$', () => {
    it('should return true if sameAsDeliveryAddress is false and billingAddressForm is invalid', (done) => {
      component = regenerateComponent(false);
      component.isSubmitting$.next(false);
      component['component.billingAddressForm'] = jasmine.createSpyObj('UntypedFormGroup', ['invalid'], { invalid: true });
      fixture.detectChanges();
      component.disableContinueButton$().subscribe((result) => {
        expect(result).toBeTrue();
        done();
      });
    });

    it('should return true if isSubmitting is true', () => {
      spyOn(billingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(of(true));
      component.isSubmitting$.next(true);
      component['component.billingAddressForm'] = jasmine.createSpyObj('UntypedFormGroup', ['invalid'], { invalid: false });

      component.disableContinueButton$().subscribe((result) => {
        expect(result).toBeTrue();
      });
    });

    it('should return false if sameAsDeliveryAddress is true and isSubmitting is false', () => {
      spyOn(billingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(of(true));
      component.isSubmitting$.next(false);
      component['component.billingAddressForm'] = jasmine.createSpyObj('UntypedFormGroup', ['invalid'], { invalid: false });

      component.disableContinueButton$().subscribe((result) => {
        expect(result).toBeFalse();
      });
    });
  });

  describe('ngOnInit', () => {
    it('should add required validator to bank code if bank configurations are present', () => {
      component.apm = {
        bankConfigurations: [{
          code: 'ING',
          name: 'ING'
        }]
      };
      component.ngOnInit();
      expect(component.idealForm.get('bank').get('code').hasValidator(Validators.required)).toBeTrue();
    });

    it('should not add required validator to bank code if bank configurations are absent', () => {
      component.apm = { bankConfigurations: [] };
      component.ngOnInit();
      expect(component.idealForm.get('bank').get('code').hasValidator(Validators.required)).toBeFalse();
    });

    it('should not throw error if apm is undefined', () => {
      component.apm = undefined;
      expect(() => component.ngOnInit()).not.toThrow();
    });
  });

  describe('next', () => {
    it('should return early if billing address is invalid', () => {
      spyOn(component.setPaymentDetails, 'emit');
      // @ts-ignore
      spyOn(component, 'validateAndGetBillingAddress').and.returnValue({
        isValid: false,
        billingAddress: undefined
      });
      // @ts-ignore
      spyOn(component, 'createPaymentDetails').and.callThrough();

      component.next();

      expect(component['createPaymentDetails']).toHaveBeenCalled();
      expect(component['createPaymentDetails']).toHaveBeenCalled();
      expect(component.setPaymentDetails.emit).not.toHaveBeenCalled();
    });

    it('should create payment details when form is valid and billing address is valid', () => {
      // @ts-ignore
      spyOn(component, 'validateAndGetBillingAddress').and.returnValue({
        isValid: true,
        billingAddress
      });
      // @ts-ignore
      spyOn(component, 'createPaymentDetails');
      component.idealForm.setValue({ bank: { code: 'ING' } });

      component.next();

      expect(component['createPaymentDetails']).toHaveBeenCalledWith(
        {
          code: PaymentMethod.iDeal,
          shopperBankCode: 'ING'
        },
      );
    });
  });
});
