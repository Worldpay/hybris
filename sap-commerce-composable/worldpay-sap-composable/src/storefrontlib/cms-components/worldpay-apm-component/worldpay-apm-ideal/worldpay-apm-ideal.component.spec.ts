import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayApmIdealComponent } from './worldpay-apm-ideal.component';
import { Address, I18nTestingModule, MockTranslatePipe } from '@spartacus/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormErrorsModule } from '@spartacus/storefront';
import { NgSelectModule } from '@ng-select/ng-select';
import { PaymentMethod } from '../../../../core/interfaces';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'y-worldpay-billing-address',
  template: ''
})
class MockWorldpayBillingAddressComponent {
  @Input() billingAddressForm;
}

describe('WorldpayApmIdealComponent', () => {
  let component: WorldpayApmIdealComponent;
  let fixture: ComponentFixture<WorldpayApmIdealComponent>;
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
      ],
      imports: [
        I18nTestingModule, FormErrorsModule, ReactiveFormsModule,
        NgSelectModule,
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApmIdealComponent);

    component = fixture.componentInstance;
    component.sameAsShippingAddress = true;
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

  it('should validate ideal form', () => {
    component.idealForm.setValue({
      bank: { code: '' }
    });
    fixture.detectChanges();

    expect(document.querySelector('button[data-test-id="ideal-continue-btn"]')['disabled']).toEqual(true);
  });

  it('should validate the form before submitting', (done) => {
    component.idealForm.setValue({
      bank: { code: 'ING' }
    });

    component.setPaymentDetails.subscribe(($event) => {
      expect($event).toEqual({
        paymentDetails: {
          shopperBankCode: 'ING',
          code: PaymentMethod.iDeal,
        },
        billingAddress: null
      });
      done();
    });

    component.next();
  });

  it('should prevent button click when billing address is invalid', () => {
    component.idealForm.setValue({ bank: { code: 'RABOBANK' } });
    component.sameAsShippingAddress = false;
    component.billingAddressForm.setValue({
      firstName: 'john',
      lastName: ''
    });

    fixture.detectChanges();

    expect(document.querySelector('button[data-test-id="ideal-continue-btn"]')['disabled']).toEqual(true);
  });

  it('should validate the billing address if unchecked billing checkbox', () => {
    component.idealForm.setValue({ bank: { code: 'RABOBANK' } });
    component.sameAsShippingAddress = false;
    component.billingAddressForm.setValue({
      firstName: 'john',
      lastName: 'doe'
    });

    fixture.detectChanges();

    expect(document.querySelector('button[data-test-id="ideal-continue-btn"]')['disabled']).toEqual(false);
  });
});
