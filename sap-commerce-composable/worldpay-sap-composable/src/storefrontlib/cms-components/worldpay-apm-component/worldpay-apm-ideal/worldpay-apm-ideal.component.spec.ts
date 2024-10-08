import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { Component } from '@angular/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormErrorsModule } from '@spartacus/storefront';
import { Address, I18nTestingModule, MockTranslatePipe } from '@spartacus/core';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { WorldpayApmIdealComponent } from './worldpay-apm-ideal.component';
import { ApmData, PaymentMethod } from '../../../../core/interfaces';

@Component({
  selector: 'y-worldpay-billing-address',
  template: ''
})
class MockWorldpayBillingAddressComponent {
}

describe('WorldpayApmIdealComponent', () => {
  let component: WorldpayApmIdealComponent;
  let fixture: ComponentFixture<WorldpayApmIdealComponent>;
  let checkoutBillingAddressFormService: CheckoutBillingAddressFormService;

  const billingAddress: Address = {
    firstName: 'John',
    lastName: 'Doe'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        declarations: [
          WorldpayApmIdealComponent,
          MockTranslatePipe,
          MockWorldpayBillingAddressComponent
        ],
        providers: [
          UntypedFormBuilder,
          CheckoutBillingAddressFormService,
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
    checkoutBillingAddressFormService = TestBed.inject(CheckoutBillingAddressFormService);
    component = fixture.componentInstance;
    component.sameAsShippingAddress = true;
    component.billingAddressForm = new UntypedFormGroup({
      firstName: new UntypedFormControl('', [Validators.required]),
      lastName: new UntypedFormControl('', [Validators.required])
    });
    component.billingAddressForm.setValue(billingAddress);
    fixture.detectChanges();
  });

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
      spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
      let event = null;
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
        billingAddress: null
      });
    });

    it('should prevent button click when billing address is invalid', () => {
      component.idealForm.get('bank').get('code').setValue('RABOBANK');
      component.sameAsShippingAddress = false;

      fixture.detectChanges();

      expect(document.querySelector('button[data-test-id="ideal-continue-btn"]')['disabled']).toEqual(true);
    });

    it('should validate the billing address if unchecked billing checkbox', () => {
      component.idealForm.get('bank').setValue({ code: 'RABOBANK' });
      component.sameAsShippingAddress = false;
      component.billingAddressForm.setValue({
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
    spyOn(checkoutBillingAddressFormService, 'getBillingAddressForm').and.returnValue(new UntypedFormGroup({}));
    component.ngOnInit();
    expect(component.billingAddressForm).toBe(checkoutBillingAddressFormService.getBillingAddressForm());
  });

  it('should add required validator to bank code if bank configurations are present', () => {
    component.apm = {
      bankConfigurations: [{
        code: 'ING',
        name: 'ING'
      }]
    } as ApmData;
    component.ngOnInit();
    expect(component.idealForm.get('bank').get('code').hasValidator(Validators.required)).toBeTrue();
  });

  it('should emit payment details and billing address when form is valid and sameAsShippingAddress is false', () => {
    spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
    spyOn(checkoutBillingAddressFormService, 'isBillingAddressFormValid').and.returnValue(true);
    spyOn(checkoutBillingAddressFormService, 'getBillingAddress').and.returnValue(billingAddress);
    spyOn(component.setPaymentDetails, 'emit');

    component.idealForm.setValue({ bank: { code: 'ING' } });
    component.next();

    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: {
        code: PaymentMethod.iDeal,
        shopperBankCode: 'ING'
      },
      billingAddress: billingAddress
    });
  });

  it('should emit payment details without billing address when form is valid and sameAsShippingAddress is true', () => {
    spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
    spyOn(component.setPaymentDetails, 'emit');

    component.idealForm.setValue({ bank: { code: 'ING' } });
    component.next();

    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: {
        code: PaymentMethod.iDeal,
        shopperBankCode: 'ING'
      },
      billingAddress: null
    });
  });

  it('should not emit payment details when form is invalid', () => {
    spyOn(component.setPaymentDetails, 'emit');
    component.idealForm.setValue({ bank: { code: null } });
    component.next();
    expect(component.setPaymentDetails.emit).not.toHaveBeenCalled();
  });

});
