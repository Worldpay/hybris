import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgSelectModule } from '@ng-select/ng-select';
import { Address, I18nTestingModule, LoggerService, MockTranslatePipe } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { BehaviorSubject, of } from 'rxjs';
import { MockWorldpayApmService, MockWorldpayBillingAddressComponent, MockWorldpayConnector } from 'worldpay-sap-composable-tests';
import { PaymentMethod, WorldpayApmService, WorldpayBillingAddressFormService, WorldpayConnector } from '../../../../core';
import { WorldpayApmSubmitButtonsComponent } from '../../../../storefrontlib';
import { WorldpayB2BApmSepaComponent } from './worldpay-b2b-apm-sepa.component';

describe('WorldpayB2BApmSepaComponent', () => {
  let component: WorldpayB2BApmSepaComponent;
  let fixture: ComponentFixture<WorldpayB2BApmSepaComponent>;
  let billingAddressFormService: WorldpayBillingAddressFormService;
  let worldpayApmService: WorldpayApmService;

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
        WorldpayB2BApmSepaComponent,
        MockTranslatePipe,
        MockWorldpayBillingAddressComponent,
        WorldpayApmSubmitButtonsComponent,
      ],
      providers: [
        UntypedFormBuilder,
        WorldpayBillingAddressFormService,
        {
          provide: WorldpayApmService,
          useClass: MockWorldpayApmService
        },
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
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayB2BApmSepaComponent);
    billingAddressFormService = TestBed.inject(WorldpayBillingAddressFormService);
    worldpayApmService = TestBed.inject(WorldpayApmService);
    component = fixture.componentInstance;
    component.apm = {
      code: PaymentMethod.SepaDirectDebit,
      name: 'SEPA Direct Debit'
    };
    component.ngOnInit();
    fixture.detectChanges();
  });

  const regenerateComponent: (sameAsDeliveryAddress: boolean) => WorldpayB2BApmSepaComponent = (sameAsDeliveryAddress: boolean): WorldpayB2BApmSepaComponent => {
    let sameAsDeliveryAddress$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(sameAsDeliveryAddress);
    spyOn(billingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(sameAsDeliveryAddress$.asObservable());
    fixture = TestBed.createComponent(WorldpayB2BApmSepaComponent);
    return fixture.componentInstance;
  };

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('buildSepaForm', () => {
    it('should create a form with "save" control', () => {
      expect(component.sepaForm.contains('save')).toBeTrue();
      expect(component.sepaForm.get('save')!.value).toBe(true);
    });
  });

  describe('beforeCreatePaymentDetails', () => {
    it('should set saveApm and merge form values', () => {
      spyOn(worldpayApmService, 'setSaveApm');
      const details = {
        code: 'SEPA',
        name: 'SEPA Name'
      };
      component.sepaForm.get('save')!.setValue(false);

      const result = component['beforeCreatePaymentDetails'](details);

      expect(worldpayApmService.setSaveApm).toHaveBeenCalledWith(false);
      expect(result.save).toBe(false);
      expect(result.code).toBe('SEPA');
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
    component.sepaForm.setValue({ save: false });
    component.next();

    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: {
        code: PaymentMethod.SepaDirectDebit,
        name: 'SEPA Direct Debit',
        save: false
      },
      billingAddress
    });
  });

  it('should emit payment details without billing address when form is valid and sameAsShippingAddress is true', () => {
    // @ts-ignore
    spyOn(billingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
    spyOn(component.setPaymentDetails, 'emit');
    billingAddressFormService.setSameAsDeliveryAddress(true);

    component.sepaForm.setValue({ save: false });
    component.next();

    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: {
        code: PaymentMethod.SepaDirectDebit,
        name: 'SEPA Direct Debit',
        save: false
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
      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeTrue();
        done();
      });
    });

    it('should return true if isSubmitting is true', () => {
      spyOn(billingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(of(true));
      component.isSubmitting$.next(true);
      component['component.billingAddressForm'] = jasmine.createSpyObj('UntypedFormGroup', ['invalid'], { invalid: false });

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeTrue();
      });
    });

    it('should return false if sameAsDeliveryAddress is true and isSubmitting is false', () => {
      spyOn(billingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(of(true));
      component.isSubmitting$.next(false);
      component['component.billingAddressForm'] = jasmine.createSpyObj('UntypedFormGroup', ['invalid'], { invalid: false });

      component.disableContinueButton().subscribe((result) => {
        expect(result).toBeFalse();
      });
    });
  });

  describe('ngOnInit', () => {
    it('should initialize billingAddressForm and sameAsDeliveryAddress$', () => {
      const mockForm = jasmine.createSpyObj('UntypedFormGroup', ['value']);
      const mockSameAsDeliveryAddress$ = of(true);
      spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(mockForm);
      spyOn(billingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(mockSameAsDeliveryAddress$);

      component.ngOnInit();

      expect(component['billingAddressForm']).toBe(mockForm);
      expect(component.sameAsDeliveryAddress$).toBe(mockSameAsDeliveryAddress$);
    });

    it('should handle undefined billingAddressForm gracefully during initialization', () => {
      spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(undefined);
      spyOn(billingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(of(false));

      component.ngOnInit();

      expect(component['billingAddressForm']).toBeUndefined();
      expect(component.sameAsDeliveryAddress$).toBeDefined();
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
      component.sepaForm.setValue({ save: false });

      component.next();

      expect(component['createPaymentDetails']).toHaveBeenCalledWith(
        {
          code: PaymentMethod.SepaDirectDebit,
          name: 'SEPA Direct Debit',
          save: false
        },
      );
    });
  });

  describe('UI tests', () => {
    const getSubmitButtonsComponent = (): DebugElement => fixture.debugElement.query(By.directive(WorldpayApmSubmitButtonsComponent));
    const getBillingAddressComponent = (): MockWorldpayBillingAddressComponent => fixture.nativeElement.querySelector('y-worldpay-billing-address');
    const getSaveCheckbox = (): HTMLElement => fixture.nativeElement.querySelector('#save-sepa-info');
    const getContinueButton = (): DebugElement => fixture.debugElement.query(By.css('[data-test-id="sepa-continue-btn"]'));

    it('should render the save mandate checkbox', () => {
      const saveCheckbox = getSaveCheckbox();
      expect(saveCheckbox).toBeTruthy();
    });

    it('should render billing address component', () => {
      const billingAddressComponent = getBillingAddressComponent();
      expect(billingAddressComponent).toBeTruthy();
    });

    it('should render worldpay apm submit buttons component', () => {
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
      continueButton.nativeElement.click();
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

    it('should enable continue button when billing address is not same as delivery address and form is valid and is not submitting the form', () => {
      component = regenerateComponent(false);
      spyOn(component, 'next');
      component.isSubmitting$.next(false);
      component.ngOnInit();
      component['billingAddressForm'].patchValue(billingAddress);
      component['billingAddressForm'].markAsDirty();
      component['billingAddressForm'].updateValueAndValidity();
      fixture.detectChanges();

      const continueButton = getContinueButton();
      expect(continueButton.nativeElement.disabled).toBeFalse();
      continueButton.nativeElement.click();
      expect(component.next).toHaveBeenCalled();
    });

    it('should enable continue button when billing address is same as delivery address and is not submitting the form', () => {
      component = regenerateComponent(true);
      spyOn(component, 'next');
      component['billingAddressForm'] = billingAddressFormService.getBillingAddressForm();
      component.isSubmitting$.next(false);
      component.ngOnInit();
      component['billingAddressForm'].markAsDirty();
      component['billingAddressForm'].updateValueAndValidity();
      fixture.detectChanges();

      const continueButton = getContinueButton();
      expect(continueButton.nativeElement.disabled).toBeFalse();
      continueButton.nativeElement.click();
      expect(component.next).toHaveBeenCalled();
    });
  });
});
