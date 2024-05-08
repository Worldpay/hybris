import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { Address, I18nTestingModule } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { ReactiveFormsModule, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { WorldpayApmAchComponent } from './worldpay-apm-ach.component';
import { WorldpayACHFacade } from '../../../../core/facade/worldpay-ach.facade';
import { PaymentMethod } from '../../../../core/interfaces';
import { Component, Input } from '@angular/core';

const mockBillingAddress: Address = {
  firstName: 'John',
  lastName: 'Smith',
  line1: 'Buckingham Street 5',
  line2: '1A',
  phone: '(+11) 111 111 111',
  postalCode: 'MA8902',
  town: 'London',
  country: {
    name: 'test-country-name',
    isocode: 'UK',
  },
  formattedAddress: 'test-formattedAddress',
};

const mockACHBankAccountTypesState = of({
  loading: false,
  error: false,
  data: {
    'type1': 'Type 1',
    'type2': 'Type 2'
  }
});

const mockACHPaymentFormValue = {
  accountType: 'type1',
  accountNumber: '123456789',
  routingNumber: '987654321',
  checkNumber: '11111',
  companyName: 'Test Company',
  customIdentifier: '12345'
};

@Component({
  selector: 'y-worldpay-billing-address',
  template: ''
})
class MockWorldpayBillingAddressComponent {
  @Input() billingAddressForm;
}

describe('WorldpayApmAchComponent', () => {
  let component: WorldpayApmAchComponent;
  let fixture: ComponentFixture<WorldpayApmAchComponent>;
  const worldpayACHFacade = jasmine.createSpyObj('WorldpayACHFacade', ['getACHBankAccountTypesState', 'getACHPaymentFormValue', 'setACHPaymentFormValue']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        declarations: [
          WorldpayApmAchComponent,
          MockWorldpayBillingAddressComponent
        ],
        imports: [
          I18nTestingModule,
          FormErrorsModule,
          ReactiveFormsModule,
          NgSelectModule,
        ],
        providers: [
          {
            provide: WorldpayACHFacade,
            useValue: worldpayACHFacade
          }
        ],
      })
      .compileComponents();

    fixture = TestBed.createComponent(WorldpayApmAchComponent);
    component = fixture.componentInstance;
    worldpayACHFacade.placeACHOrder;
    worldpayACHFacade.getACHPaymentFormValue.and.returnValue(of(null));
    worldpayACHFacade.getACHBankAccountTypesState.and.returnValue(mockACHBankAccountTypesState);
    component.billingAddressForm = new UntypedFormGroup({
      firstName: new UntypedFormControl(null),
      lastName: new UntypedFormControl(null),
      line1: new UntypedFormControl(null),
      line2: new UntypedFormControl(null),
      phone: new UntypedFormControl(null),
      postalCode: new UntypedFormControl(null),
      town: new UntypedFormControl(null),
      country: new UntypedFormControl(null),
      formattedAddress: new UntypedFormControl(null),
    });

    component.billingAddressForm.setValue(mockBillingAddress);
  });

  it('should initialize ACH bank account types', () => {
    component.ngOnInit();

    expect(worldpayACHFacade.getACHBankAccountTypesState).toHaveBeenCalled();
  });

  it('should set ACH payment form value on initialization', () => {

    worldpayACHFacade.getACHPaymentFormValue.and.returnValue(of(mockACHPaymentFormValue));

    component.ngOnInit();

    fixture.detectChanges();

    expect(worldpayACHFacade.getACHPaymentFormValue).toHaveBeenCalled();
    expect(component.achForm.value).toEqual({
      ...mockACHPaymentFormValue,
      accountType: { code: mockACHPaymentFormValue.accountType }
    });
  });

  it('should emit payment details and billing address on next', () => {
    component.sameAsShippingAddress = false;
    component.ngOnInit();
    fixture.detectChanges();
    spyOn(component.setPaymentDetails, 'emit');

    component.achForm.setValue({
      accountType: { code: 'type1' },
      accountNumber: '123456789',
      routingNumber: '987654321',
      checkNumber: '11111',
      companyName: 'Test Company',
      customIdentifier: '12345'
    });

    fixture.detectChanges();
    component.next();
    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: {
        code: PaymentMethod.ACH,
        achPaymentForm: {
          accountType: 'type1',
          accountNumber: '123456789',
          routingNumber: '987654321',
          checkNumber: '11111',
          companyName: 'Test Company',
          customIdentifier: '12345'
        }
      },
      billingAddress: mockBillingAddress
    });
  });

  it('should not emit payment details and billing address if form is invalid', () => {
    spyOn(component.setPaymentDetails, 'emit');
    component.achForm.setValue({
      accountType: { code: null },
      accountNumber: null,
      routingNumber: null,
      checkNumber: null,
      companyName: null,
      customIdentifier: null
    });

    component.next();

    expect(component.setPaymentDetails.emit).not.toHaveBeenCalled();
  });

  it('should allow only numbers in input fields', () => {
    component.ngOnInit();
    fixture.detectChanges();
    component.allowNumbersOnly({ target: { value: '123abc4' } } as any, 'accountNumber');
    fixture.detectChanges();
    expect(component.achForm.get('accountNumber').value).toEqual('1234');
  });

  it('should emit back event on return', () => {
    spyOn(component.back, 'emit');

    component.return();

    expect(component.back.emit).toHaveBeenCalled();
  });
});
