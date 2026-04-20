import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgSelectModule } from '@ng-select/ng-select';
import { Address, I18nTestingModule, LoggerService, MockTranslatePipe } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { BehaviorSubject, of } from 'rxjs';
import { MockWorldpayBillingAddressComponent, MockWorldpayConnector } from 'worldpay-sap-composable-tests';
import { PaymentMethod, WorldpayBillingAddressFormService, WorldpayConnector } from '../../../../core';
import { WorldpayApmSubmitButtonsComponent } from '../worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.component';
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
    const mockBillingFormWithStatus = (status: 'VALID' | 'INVALID') => ({
      status,
      statusChanges: of(status),
    }) as any;

    it('should return true if sameAsDeliveryAddress is false and billingAddressForm is invalid', (done) => {
      component = regenerateComponent(false);
      component.isSubmitting$.next(false);
      component['billingAddressForm'] = undefined;
      spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(mockBillingFormWithStatus('INVALID'));

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeTrue();
        done();
      });
    });

    it('should return true if isSubmitting is true', () => {
      component.sameAsDeliveryAddress$ = of(true);
      component.isSubmitting$.next(true);
      component['billingAddressForm'] = undefined;
      spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(mockBillingFormWithStatus('VALID'));

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeTrue();
      });
    });

    it('should return false if sameAsDeliveryAddress is true and isSubmitting is false', () => {
      component.sameAsDeliveryAddress$ = of(true);
      component.isSubmitting$.next(false);
      component['billingAddressForm'] = undefined;
      spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(mockBillingFormWithStatus('INVALID'));

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeFalse();
      });
    });

    it('should return true if sameAsDeliveryAddress is true, isSubmitting is false and customInvalidForm is true', () => {
      component.sameAsDeliveryAddress$ = of(true);
      const customInvalidForm = true;
      component.isSubmitting$.next(false);
      component['billingAddressForm'] = undefined;
      spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(mockBillingFormWithStatus('VALID'));

      component.disableContinueButton(customInvalidForm).subscribe((result) => {
        expect(result).toBeTrue();
      });
    });

    it('should return false if sameAsDeliveryAddress is true, isSubmitting is false and customInvalidForm is false', () => {
      component.sameAsDeliveryAddress$ = of(true);
      const customInvalidForm = false;
      component.isSubmitting$.next(false);
      component['billingAddressForm'] = undefined;
      spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(mockBillingFormWithStatus('INVALID'));

      component.disableContinueButton(customInvalidForm).subscribe((result) => {
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

  describe('UI tests', () => {
    const getSubmitButtonsComponent = (): DebugElement => fixture.debugElement.query(By.directive(WorldpayApmSubmitButtonsComponent));
    const getBillingAddressComponent = (): MockWorldpayBillingAddressComponent => fixture.nativeElement.querySelector('y-worldpay-billing-address');
    const getContinueButton = (): DebugElement => fixture.debugElement.query(By.css('[data-test-id="ideal-continue-btn"]'));

    it('should render billing address component', () => {
      fixture.detectChanges();
      const billingAddressComponent = getBillingAddressComponent();
      expect(billingAddressComponent).toBeTruthy();
    });

    it('should render worldpay apm submit buttons component', () => {
      fixture.detectChanges();
      const submitButtonsComponent = getSubmitButtonsComponent();
      expect(submitButtonsComponent).toBeTruthy();
    });

    it('should disable continue button when submitting', () => {
      component = regenerateComponent(true);
      spyOn(component, 'next');
      const form = new UntypedFormGroup({});
      form.setErrors(null);
      spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(form);
      component.ngOnInit();
      component['billingAddressForm'].markAsDirty();
      component['billingAddressForm'].updateValueAndValidity();
      component.isSubmitting$.next(true);
      fixture.detectChanges();

      const continueButton = getContinueButton();
      expect(continueButton.nativeElement.disabled).toBeTrue();
      expect(component.next).not.toHaveBeenCalled();
    });

    it('should disable continue button when billing address is not same as delivery address and form is invalid', () => {
      component = regenerateComponent(false);
      spyOn(component, 'next');
      component.isSubmitting$.next(false);
      component.ngOnInit();
      component['billingAddressForm'].markAsDirty();
      component['billingAddressForm'].updateValueAndValidity();
      fixture.detectChanges();

      const continueButton = getContinueButton();
      expect(continueButton.nativeElement.disabled).toBeTrue();
      continueButton.nativeElement.click();
      expect(component.next).not.toHaveBeenCalled();
    });

    it('should enable continue button when billing address is same as delivery address, idealForm form is valid and is not submitting the form', () => {
      component = regenerateComponent(true);
      spyOn(component, 'next');
      component['billingAddressForm'] = billingAddressFormService.getBillingAddressForm();
      component.isSubmitting$.next(false);
      component.ngOnInit();
      component['billingAddressForm'].markAsDirty();
      component['billingAddressForm'].updateValueAndValidity();
      component.idealForm.patchValue({ bank: { code: 'ING' } });
      fixture.detectChanges();

      const continueButton = getContinueButton();
      expect(continueButton.nativeElement.disabled).toBeFalse();
      continueButton.nativeElement.click();
      expect(component.next).toHaveBeenCalled();
    });
  });
});
